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
    private boolean isDead = false;
    private Loader loader;
    private final int START_ROW = 24;
    private String soulNumber;
    private boolean hasWon = false;
    private String playerID;
    private int numberOfPlayers = 4;
    private ColourMap colourMap;
    private Lava lava;
    private Timer timer;


    public Game() {


        this.grid = new Grid();
        this.view = new View();
        this.screen = view.getScreen();
        this.player = new Player(this.view, this);
        loader = new Loader();
        this.colourMap = new ColourMap();
        colourMap.init();
        lava = new Lava(this.grid, this.view);
        this.timer = new Timer();
    }

    /*
    *
    * */
    public void start(String ip, int port) {

        inicialScreen();

        try {

            connection = new Socket(ip, port);

            waitingForPlayers();

            loadGameScreen();

            gameUpDater();


        } catch (IOException e) {
            e.printStackTrace();
        }

        spawnPlayer(START_ROW);
        riseLava();
        player.keyHandler();

    }

    private void riseLava() {

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                lava.riseLava();
                refresh();

            }
        }, 1000L, 1000L);
    }


    /*
    *
    * */
    private void gameUpDater() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    BufferedReader pos = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while (pos != null) {

                        String read = pos.readLine();

                        checkForWinner(read);

                        setDeadPlayer(read);

                        updateGrid(read);

                        showPlayer(read);

                        checkDead();

                        refresh();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    /*
    *
    * */
    private void loadGameScreen() {

        loadScreen(loader.readFile("Nivel1"));
        playerList();
        refresh();
    }

    /*
    *
    * */
    private void waitingForPlayers() {

        screen.clear();

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String startMessage;

            while (!(startMessage = bufferedReader.readLine()).equals("Start")) {
                playerID = startMessage;
                playerID(startMessage);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    *
    * */
    private void inicialScreen() {

        loadScreen(loader.readFile("Menu"));
        this.screen.putString(45, 29, "WAITING FOR SOULS", Terminal.Color.RED, Terminal.Color.BLACK, ScreenCharacterStyle.Bold);
        screen.setCursorPosition(null);
        refresh();
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

        if (isDead || hasWon) {
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

        if (hasWon) {
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

        if (isDead || hasWon) {
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
    public void refresh() {

        for (int row = 0; row < 29; row++) {
            for (int col = 10; col < 90; col++) {

                this.screen.putString(col, row, " ", Terminal.Color.CYAN, colourMap.getColour(grid.getGrid()[row][col]));

            }
        }

        screen.refresh();
    }


    /*
    *
    * */


    /*
    *
    * */
    public void checkDead() {

        if (view.playerPos_Y() <= lava.getDeathRow()) {
            return;
        }
        isDead = true;
        try {
            PrintStream out = new PrintStream(connection.getOutputStream());
            out.println("RIP:" + soulNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
    *
    * */
    public void loadScreen(String map) {

        String[] split;
        String[] resultSplit = map.split("/");

        for (int i = 0; i < 29; i++) {

            split = resultSplit[i].split("");

            for (int j = 11; j < 90; j++) {

                this.grid.getGrid()[i][j] = Integer.parseInt(split[j]);

            }
        }
    }

    public void weHaveAWinner(String message) {

        hasWon = true;

        loadScreen(message);

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

    public void checkForWinner(String winningPlayer) {

        if (winningPlayer.split(":")[0].equals("WIN")) {

            isDead = true;

            loadScreen(loader.readFile(winningPlayer.split(":")[1].split(" ")[1]));

        }

    }

    /*
    *
    * */

    public void playerList() {

        for (int i = 0; i < numberOfPlayers; i++) {

            this.screen.putString(1, 1 + i * 3, "SOUL " + (i + 1), Terminal.Color.CYAN, Terminal.Color.BLACK, ScreenCharacterStyle.Bold);

        }

    }

    public void checkWin() {

        if (this.view.playerPos_Y() == 0) {

            try {
                PrintStream out = new PrintStream(connection.getOutputStream());
                out.println("WIN:" + soulNumber);
            } catch (IOException e) {
                e.printStackTrace();
            }
            weHaveAWinner(loader.readFile(soulNumber.split(" ")[1]));

        }
    }


    /*
    * Getters & Setters
    * */
    public boolean isDead() {
        return isDead;
    }

    public boolean isHasWon() {
        return hasWon;
    }
}
