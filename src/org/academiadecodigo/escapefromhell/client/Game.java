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
    private Loadmenu loadmenu = new Loadmenu();


    public Game() {

        this.grid = new Grid();
        this.view = new View();
        this.screen = view.getScreen();
        this.player = new Player(this.view, this);
        this.timer = new Timer();


    }

    public void start(String ip, int port) {



        try {

            connection = new Socket(ip, port);
            System.out.println("here1");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));


            loadLevel(bufferedReader.readLine());
            System.out.println("here2");
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
                            }
                            refresh();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            new BufferedInputStream(this.connection.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        init();
        player.keyHandler();

    }

    /*
    *
    * */

    private void loadMenu(String menuMap) {

        String[] split;
        String[] resultSplit = menuMap.split("/");

        for (int i = 0; i < 30; i++) {

            split = resultSplit[i].split("");

            for (int j = 0; j < 100; j++) {

                this.grid.getGrid()[i][j] = Integer.parseInt(split[j]);

            }
        }

    }

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

        refresh();

        //loadLevel();

        spawnPlayer(23);


        /*timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(deathRow == 0)
                    return;

                riseLava();
                refresh();
            }
        }, 1000L, 2000L);*/

    }


    /*
    *
    * */

    private void spawnPlayer(int row) {

        view.setPlayerPos((int) (Math.random() * view.terminalSize_X()), row);

    }

    public void drawRight() {

        if (view.playerPos_X() == view.terminalSize_X() - 1) {
            return;
        }

        grid.getGrid()[view.playerPos_Y()][view.playerPos_X() + 1] = 1;
        refresh();

        try {
            PrintStream out = new PrintStream(connection.getOutputStream());
            out.println(view.playerPos_Y() + "/" + (view.playerPos_X() + 1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drawLeft() {

        if (view.playerPos_X() == 0) {
            return;
        }

        grid.getGrid()[view.playerPos_Y()][view.playerPos_X() - 1] = 1;
        refresh();

        try {
            PrintStream out = new PrintStream(connection.getOutputStream());
            out.println(view.playerPos_Y() + "/" + (view.playerPos_X() - 1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkMove(int direction){

        if((direction == 1 && (view.playerPos_X() == view.terminalSize_X() - 1)) || (direction == -1 && (view.playerPos_X() == 0))){
            return;
        }

        if(grid.getGrid()[view.playerPos_Y()][view.playerPos_X() + direction] == 1 && grid.getGrid()[view.playerPos_Y() - 1][view.playerPos_X()] == 1){
            return;
        }

        if(grid.getGrid()[view.playerPos_Y()][view.playerPos_X() + direction] != 1){
            move(direction, 0);
            return;
        }

        if(grid.getGrid()[view.playerPos_Y()][view.playerPos_X() + direction] == 1 && grid.getGrid()[view.playerPos_Y() - 1][view.playerPos_X() + direction] != 1){
            move(direction, 1);
            return;
        }

    }

    private void move(int direction, int row){

        int oldX = view.playerPos_X();
        int oldY = view.playerPos_Y();

        view.setPlayerPos(view.playerPos_Y() - row, view.playerPos_X() + direction);

        checkFall();
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

    public void riseLava() {
        deathRow --;

        for (int i = 0; i < view.terminalSize_X(); i++) {


            grid.getGrid()[deathRow][i] = 3;

        }
    }

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

    public void moveGrid() {

        int mem1;
        int mem2 = 0;

        for (int col = 0; col < 100; col++) {
            for (int row = 0; row < 29; row++) {
                mem1 = this.grid.getValue(row + 1, col);
                this.grid.updateCell(mem2, row + 1, col);
                mem2 = mem1;
                setRowBlack();

            }
        }

    }


    public void setRowBlack(){

        for (int col = 0; col <100 ; col++) {

            this.grid.updateCell(0,1,col);
        }
    }

}
