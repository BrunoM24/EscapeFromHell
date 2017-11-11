package org.academiadecodigo.escapefromhell.client;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.*;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * EscapeFromHell Created by BrunoM24 on 07/11/2017.
 */

public class Game {

    private Screen screen;
    private Grid grid;
    private View view;
    private Player player;
    private Socket connection;
    private Timer timer;
    private int deathRow = 30;
    private boolean isDead = false;
    private Loadmenu loadmenu = new Loadmenu();
    private LoadLevel loadLevel;
    private final int START_ROW = 25;


    public Game() {

        this.grid = new Grid();
        this.view = new View();
        this.screen = view.getScreen();
        this.player = new Player(this.view, this);
        this.timer = new Timer();
        loadLevel = new LoadLevel();

    }

    /*
    *
    * */
    public void start(String ip, int port) {

        loadLevel(loadmenu.readFile());
        screen.setCursorPosition(null);

        refresh();

        try {

            connection = new Socket(ip, port);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String menu = bufferedReader.readLine();
            if(menu.equals("Start")) {

                loadLevel(loadLevel.readFile());

                refresh();
            }
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        BufferedReader pos = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while (pos != null) {

                            String read = bufferedReader.readLine();
                            updateGrid(read);

                            if (read.length() == 5) {

                                updateGrid(read);
                            }
                            refresh();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }

        init();
        player.keyHandler();

    }


    /*
    *
    * */
    private void showPlayer(String read) {

        int oldRow = Integer.parseInt(read.split("/")[0]);
        int oldCol = Integer.parseInt(read.split("/")[1]);
        int row = Integer.parseInt(read.split("/")[2]);
        int col = Integer.parseInt(read.split("/")[3]);

        this.grid.updateCell(0, oldCol, oldRow);
        this.grid.updateCell(2, col, row);
    }


    /*
    *
    * */
    private void updateGrid(String s) {

        int row = Integer.parseInt(s.split("/")[0]);
        int col = Integer.parseInt(s.split("/")[1]);
        this.grid.updateCell(1, row, col);

    }

    /*
    *
    * */
    public void init() {


        spawnPlayer(START_ROW);

        refresh();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (deathRow == 0)
                    return;

                riseLava();
                refresh();
            }
        }, 1000L, 2000L);

    }


    /*
    *
    * */
    private void spawnPlayer(int row) {

        screen.setCursorPosition(((int) (Math.random() * 78)) + 10, row);

    }


    /*
    *
    * */
    public void draw(int direction) {

        if (isDead) {
            return;
        }

        if (view.playerPos_X() == view.terminalSize_X() - 1 || view.playerPos_X() == 0) {
            return;
        }


        grid.getGrid()[view.playerPos_Y()][view.playerPos_X() +direction] = 1;
        refresh();


        try {
            PrintStream out = new PrintStream(connection.getOutputStream());
            out.println(view.playerPos_Y() + "/" + (view.playerPos_X() + direction));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
    *
    * */
    public void checkMove(int direction) {


        if (isDead) {
            return;
        }

        if ((direction == 1 && (view.playerPos_X() == view.terminalSize_X() - 1)) || (direction == -1 && (view.playerPos_X() == 0))){

            return;
        }

        if (grid.getGrid()[view.playerPos_Y()][view.playerPos_X() + direction] == 1 && grid.getGrid()[view.playerPos_Y() - 1][view.playerPos_X()] == 1) {
            return;
        }

        if (grid.getGrid()[view.playerPos_Y()][view.playerPos_X() + direction] != 1) {
            move(direction, 0);
            return;
        }

        if (grid.getGrid()[view.playerPos_Y()][view.playerPos_X() + direction] == 1 && grid.getGrid()[view.playerPos_Y() - 1][view.playerPos_X() + direction] != 1) {
            move(direction, 1);
            return;
        }

    }


    /*
    *
    * */
    private void move(int direction, int row) {

        int oldX = view.playerPos_X();
        int oldY = view.playerPos_Y();

        view.setPlayerPos(view.playerPos_Y() - row, view.playerPos_X() + direction);

        checkFall();
        checkDead();

        refresh();
    }


    /*
    * cheack if the player position is on the botton row
    * while cell below player is empty incrise pY position of the player
    * */

    private void checkFall() {

        if (this.view.playerPos_Y() == view.terminalSize_Y() - 1) {
            return;
        }
        if (grid.getGrid()[this.view.playerPos_Y() + 1][this.view.playerPos_X()] == 0) {

            while (grid.getGrid()[this.view.playerPos_Y() + 1][this.view.playerPos_X()] == 0) {

                this.view.setPlayerPos(this.view.playerPos_Y() + 1, this.view.playerPos_X());

                if (this.view.playerPos_Y() == view.terminalSize_Y() - 1) {
                    break;
                }
            }

        }

    }


    /*
    *
    * */
    private void refresh() {

        for (int row = 0; row < 30; row++) {
            for (int col = 0; col < 100; col++) {

                if (grid.getGrid()[row][col] == 1) {
                    this.screen.putString(col, row, " ", Terminal.Color.CYAN, Terminal.Color.WHITE);
                } else if (grid.getGrid()[row][col] == 2) {
                    this.screen.putString(col, row, " ", Terminal.Color.CYAN, Terminal.Color.GREEN);
                } else if (grid.getGrid()[row][col] == 3) {
                    this.screen.putString(col, row, " ", Terminal.Color.CYAN, Terminal.Color.RED);
                } else {
                    this.screen.putString(col, row, " ", Terminal.Color.CYAN, Terminal.Color.BLACK);
                }

            }
        }

        screen.refresh();
    }


    /*
    *
    * */
    public void riseLava() {

        deathRow--;

        for (int i = 11; i < (view.terminalSize_X() - 11); i++) {


            grid.getGrid()[deathRow][i] = 3;

        }
    }


    /*
    *
    * */
    public void checkDead(){

        if (!(this.view.playerPos_Y() == deathRow-1)) {

            return;
        }
            isDead = true;
            //Change color to red;
        //Send status string

    }


    /*
    *
    * */
    public void loadLevel(String map) {

        String[] split;
        String[] resultSplit = map.split("/");

        for (int i = 0; i < 30; i++) {

            split = resultSplit[i].split("");

            for (int j = 0; j < 100; j++) {

                this.grid.getGrid()[i][j] = Integer.parseInt(split[j]);

            }
        }
    }


    /*
    *
    * */
    public void checkWin(){

       if (this.view.playerPos_Y() == 0) {

           isDead = true;
           //Load win file and send everyone
       }
   }

}
