package org.academiadecodigo.escapefromhell.client;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import org.academiadecodigo.escapefromhell.server.LoadLevel;

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
    private LavaTimer lavaTimer;
    private Timer timer;
    private int rowY = 29;


    /*
    *
    * */

    public Game() {

        this.grid = new Grid();
        this.view = new View();
        this.screen = view.getScreen();
        this.player = new Player(this.view, this);
        this.lavaTimer = new LavaTimer();
        this.timer = new Timer();
    }


    /*
    *
    * */

    public void start(String ip, int port) {

        try {
            connection = new Socket(ip, port);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            loadLevel(bufferedReader.readLine());
            refresh();

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        BufferedReader pos = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while (connection != null) {
                            String read = pos.readLine();
                            if (read.length() == 5) {
                                updateGrid(read);
                            } else {
                                showPlayer(read);
                                //System.out.println(read);
                            }
                            refresh();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


            BufferedInputStream bufferedInputStream = new BufferedInputStream(this.connection.getInputStream());


        } catch (IOException e) {
            e.printStackTrace();
        }

        init();
        player.moveDirection();

    }

    private void showPlayer(String read) {
        int oldRow = Integer.parseInt(read.split("/")[0]);
        int oldCol = Integer.parseInt(read.split("/")[1]);
        int row = Integer.parseInt(read.split("/")[2]);
        int col = Integer.parseInt(read.split("/")[3]);

        System.out.println(read);
        this.grid.updateCell(0, oldCol, oldRow);
        this.grid.updateCell(2, col, row);
    }

    private void updateGrid(String s) {
        int row = Integer.parseInt(s.split("/")[0]);
        int col = Integer.parseInt(s.split("/")[1]);
        this.grid.updateCell(1, row, col);

    }

    /*
    *
    * */

    public void init() {

        refresh();

        //loadLevel();

        spawnPlayer(23);


        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                riseLava();
                refresh();
            }
        }, 1000L, 1000L);


    }

    private void spawnPlayer(int row) {

        view.setPlayerPos((int) (Math.random() * view.terminalSize_X()), row);

    }


    /*
    *
    * */

    public void drawRight() {

        grid.getGrid()[view.playerPos_Y()][view.playerPos_X() + 1] = 1;

        refresh();
        try {
            PrintStream out = new PrintStream(connection.getOutputStream());
            out.println(view.playerPos_Y() + "/" + (view.playerPos_X() + 1));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
    *
    * */

    public void drawLeft() {

        grid.getGrid()[view.playerPos_Y()][view.playerPos_X() - 1] = 1;
        refresh();
        try {
            PrintStream out = new PrintStream(connection.getOutputStream());
            out.println(view.playerPos_Y() + "/" + (view.playerPos_X() - 1));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /*
    *
    * verify if next cell is filled
    * if not occupy that is  a stair climb, if its a wall do nothing
    * 2 cell above and cell to the right are fill do nothing
    * */

    public void moveRight() {

        int oldX = view.playerPos_X();
        int oldY = view.playerPos_Y();


        if (grid.getGrid()[view.playerPos_Y()][view.playerPos_X() + 1] == 1) {

            if (grid.getGrid()[view.playerPos_Y() - 1][view.playerPos_X() + 1] == 1) {
                return;
            }
            //2
            if (grid.getGrid()[view.playerPos_Y() - 1][view.playerPos_X()] == 1 && grid.getGrid()[view.playerPos_Y()][view.playerPos_X() + 1] == 1) {
                return;
            }

            view.setPlayerPos(view.playerPos_X() + 1, view.playerPos_Y() - 1);

        } else {

            view.setPlayerPos(view.playerPos_X() + 1, view.playerPos_Y());

        }

        checkFall();

        refresh();
        try {
            new PrintStream(connection.getOutputStream()).println(oldX + "/" + oldY + "/" + view.playerPos_X() + "/" + view.playerPos_Y());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
    *
    * verify if next cell is filled
    * if not occupy that is  a stair climb, if its a wall do nothing
    * 2 cell above and cell to the left are fill do nothing
    * */

    public void moveLeft() {

        int oldX = view.playerPos_X();
        int oldY = view.playerPos_Y();

        if (grid.getGrid()[view.playerPos_Y()][view.playerPos_X() - 1] == 1) {

            if (grid.getGrid()[view.playerPos_Y() - 1][view.playerPos_X() - 1] == 1) {
                return;
            }

            //block on top - cannot cross stair
            if (grid.getGrid()[view.playerPos_Y() - 1][view.playerPos_X()] == 1 && grid.getGrid()[view.playerPos_Y()][view.playerPos_X() - 1] == 1) {
                return;
            }

            view.setPlayerPos(view.playerPos_X() - 1, view.playerPos_Y() - 1);

        } else {

            view.setPlayerPos(view.playerPos_X() - 1, view.playerPos_Y());
        }

        checkFall();

        refresh();

        try {
            new PrintStream(connection.getOutputStream()).println(oldX + "/" + oldY + "/" + view.playerPos_X() + "/" + view.playerPos_Y());
        } catch (IOException e) {
            e.printStackTrace();
        }
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

                this.view.setPlayerPos(this.view.playerPos_X(), this.view.playerPos_Y() + 1);

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


    public void harakiri(int row) {

        spawnPlayer(row);
    }


    public void riseLava() {

        for (int i = 0; i < view.terminalSize_X(); i++) {


            grid.getGrid()[rowY][i] = 3;

        }
        rowY -= 1;
    }

    /*
    *
    * */


    public void loadLevel(String map) {

        //System.out.println(map);

        String[] split;
        String[] resultSplit = map.split("/");
        ;


        for (int i = 0; i < 30; i++) {


            split = resultSplit[i].split("");

            for (int j = 0; j < 100; j++) {

                this.grid.getGrid()[i][j] = Integer.parseInt(split[j]);

            }
        }
    }

}
