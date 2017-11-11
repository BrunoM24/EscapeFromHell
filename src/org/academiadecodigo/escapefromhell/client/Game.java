package org.academiadecodigo.escapefromhell.client;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenCharacterStyle;
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
    private String soulNumber;
    private LoadWin loadWin;
    private boolean hasWon = false;


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
            String serverMessage;


            while (!(serverMessage = bufferedReader.readLine()).equals("Start")) {

                playerID(serverMessage);

            }

            loadLevel(loadLevel.readFile());
            playerList();


            refresh();

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        BufferedReader pos = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while (pos != null) {

                            String read = bufferedReader.readLine();
                            setDeadPlayer(read);
                            updateGrid(read);

                            if (read.length() == 5) {

                                updateGrid(read);
                            }

                            showPlayer(read);


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
    private void playerID(String serverMessage) {

        if (serverMessage.length() != 9)
            return;

        soulNumber = serverMessage.split(":")[1];
        this.screen.putString(91, 2, "YOU ARE:", Terminal.Color.CYAN, Terminal.Color.BLACK, ScreenCharacterStyle.Bold);
        this.screen.putString(91, 4, soulNumber, Terminal.Color.CYAN, Terminal.Color.BLACK, ScreenCharacterStyle.Bold);


    }


    /*
    *
    * */
    private void showPlayer(String read) {

         String[] coordinates = read.split(":");

        if (coordinates[0].equals("POS")) {

            String[] newCoordinates = coordinates[1].split("/");
            int oldRow = Integer.parseInt(newCoordinates[0]);
            int oldCol = Integer.parseInt(newCoordinates[1]);
            int row = Integer.parseInt(newCoordinates[2]);
            int col = Integer.parseInt(newCoordinates[3]);
            System.out.println("old col " + oldCol);
            System.out.println("old row " + oldRow);
            System.out.println("col " + col);
            System.out.println("row " + row);
            this.grid.updateCell(0, oldRow, oldCol);
            this.grid.updateCell(2, row, col);
        }
    }


    /*
    *
    * */
    private void updateGrid(String s) {
        String[] coordinates = s.split(":");
        if (s.split(":")[0].equals("CELL")) {
            String[] newCoordinates = coordinates[1].split("/");

            int row = Integer.parseInt(newCoordinates[0]);
            int col = Integer.parseInt(newCoordinates[1]);
            this.grid.updateCell(1, row, col);
        }
    }

    /*
    *
    * */
    public void init() {


        spawnPlayer(4);

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

        screen.setCursorPosition(((int) (Math.random() * 77)) + 11, row);

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


        grid.getGrid()[view.playerPos_Y()][view.playerPos_X() + direction] = 1;
        refresh();


        try {
            PrintStream out = new PrintStream(connection.getOutputStream());
            out.println("CELL:" + view.playerPos_Y() + "/" + (view.playerPos_X() + direction));
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

        if ((direction == 1 && (view.playerPos_X() == view.terminalSize_X() - 1)) || (direction == -1 && (view.playerPos_X() == 0))) {

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

        if (isDead) {
            return;
        }


        int oldX = view.playerPos_X();
        int oldY = view.playerPos_Y();

        view.setPlayerPos(view.playerPos_Y() - row, view.playerPos_X() + direction);

        try {
            PrintStream out = new PrintStream(connection.getOutputStream());
            out.println("POS:" + oldY + "/" + oldX + "/" + view.playerPos_Y() + "/" + view.playerPos_X());
        } catch (IOException e) {
            e.printStackTrace();
        }
        checkWin();
        checkFall();
        // checkDead();

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

                int oldY = this.view.playerPos_Y();
                int oldX = this.view.playerPos_X();

                this.view.setPlayerPos(this.view.playerPos_Y() + 1, this.view.playerPos_X());
                try {
                    PrintStream out = new PrintStream(connection.getOutputStream());
                    out.println("POS:" + oldY + "/" + oldX + "/" + view.playerPos_Y() + "/" + view.playerPos_X());
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
            for (int col = 10; col < 90; col++) {

                if (grid.getGrid()[row][col] == 1) {
                    this.screen.putString(col, row, " ", Terminal.Color.CYAN, Terminal.Color.WHITE);
                } else if (grid.getGrid()[row][col] == 2) {
                    this.screen.putString(col, row, " ", Terminal.Color.CYAN, Terminal.Color.GREEN);
                } else if (grid.getGrid()[row][col] == 3) {
                    this.screen.putString(col, row, " ", Terminal.Color.CYAN, Terminal.Color.RED);
                } else if (grid.getGrid()[row][col] == 4) {
                    this.screen.putString(col, row, " ", Terminal.Color.CYAN, Terminal.Color.YELLOW);
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
        if (view.playerPos_Y() >= deathRow) {

            checkDead();
        }
    }


    /*
    *
    * */
    public void checkDead() {

        /*if (!(this.view.playerPos_Y() >= deathRow - 1)) {

            return false;
        }*/

        isDead = true;


        try {
            PrintStream out = new PrintStream(connection.getOutputStream());
            out.println("RIP:" + soulNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }


        //screen.setCursorPosition(null);
        //refresh();
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

            for (int j = 11; j < 90; j++) {

                this.grid.getGrid()[i][j] = Integer.parseInt(split[j]);

            }
        }
    }

    /*
    *
    * */
    public void setDeadPlayer(String deadPlayer) {

        if (deadPlayer.split(":")[0].equals("RIP")) {

            System.out.println(deadPlayer);
            int number = Integer.parseInt(deadPlayer.split(" ")[1]) - 1;
            this.screen.putString(1, 2 + number * 3, "SOUL LOST", Terminal.Color.RED, Terminal.Color.BLACK, ScreenCharacterStyle.Bold);

            refresh();
        }


    }


    /*
    *
    * */
    public void playerList() {
        this.screen.putString(1, 1, "SOUL 1", Terminal.Color.CYAN, Terminal.Color.BLACK, ScreenCharacterStyle.Bold);
        this.screen.putString(1, 4, "SOUL 2", Terminal.Color.CYAN, Terminal.Color.BLACK, ScreenCharacterStyle.Bold);
        this.screen.putString(1, 7, "SOUL 3", Terminal.Color.CYAN, Terminal.Color.BLACK, ScreenCharacterStyle.Bold);
        this.screen.putString(1, 10, "SOUL 4", Terminal.Color.CYAN, Terminal.Color.BLACK, ScreenCharacterStyle.Bold);

    }

    public void checkWin() {
        if (this.view.playerPos_Y() == 0) {
            System.out.println("winner1");
            weHaveAWinner(loadWin.readWine());
            System.out.println("winner2");

        }
    }


    public void weHaveAWinner(String message) {
        System.out.println("I got in");
        hasWon = true;

        String[] split;
        String[] resultSplit = message.split("/");

        System.out.println("I got the power");
        for (int i = 0; i < 30; i++) {

            split = resultSplit[i].split("");

            for (int j = 11; j < 90; j++) {

                this.grid.getGrid()[i][j] = Integer.parseInt(split[j]);
            }


        }
    }
}
