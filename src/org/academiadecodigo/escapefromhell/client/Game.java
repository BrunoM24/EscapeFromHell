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

    private final int NUMBER_OF_PLAYERS = 4;
    private final int START_ROW = 24;

    private Screen screen;
    private Grid grid;
    private View view;
    private Player player;
    private Socket connection;
    private String soulNumber;
    private ColourMap colourMap;
    private Lava lava;
    private Timer timer;
    private boolean isDead = false;
    private boolean hasWon = false;


    public Game() {

        this.grid = new Grid();
        this.view = new View();
        this.screen = view.getScreen();
        this.player = new Player(this.view, this, this.grid);
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
        player.spawnPlayer(START_ROW);
        riseLava();
        player.keyHandler(connection);

    }

    /*
    *
    * */
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

                        updateStairs(read);

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

        loadScreen(new Loader().readFile("Nivel1"));
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
                String playerID = startMessage;
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

        loadScreen(new Loader().readFile("Menu"));
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
    private void updateStairs(String s) {

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

    /*
    *
    * */
    public void weHaveAWinner(String message) {

        hasWon = true;

        loadScreen(message);

    }
    /*
    *
    * */

    public void setDeadPlayer(String deadPlayer) {

        if (deadPlayer.split(":")[0].equals("RIP")) {

            int number = Integer.parseInt(deadPlayer.split(" ")[1]) - 1;
            this.screen.putString(1, 2 + number * 3, "SOUL LOST", Terminal.Color.RED, Terminal.Color.BLACK, ScreenCharacterStyle.Bold);

            refresh();
        }

    }

    /*
    *
    * */
    public void checkForWinner(String winningPlayer) {

        if (winningPlayer.split(":")[0].equals("WIN")) {

            isDead = true;
            lava.stopLava();


            loadScreen(new Loader().readFile(winningPlayer.split(":")[1].split(" ")[1]));

        }

    }

    /*
    *
    * */
    public void playerList() {

        for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {

            this.screen.putString(1, 1 + i * 3, "SOUL " + (i + 1), Terminal.Color.CYAN, Terminal.Color.BLACK, ScreenCharacterStyle.Bold);

        }

    }

    /*
    *
    * */
    public void checkWin() {

        if (this.view.playerPos_Y() == 0) {

            try {
                PrintStream out = new PrintStream(connection.getOutputStream());
                out.println("WIN:" + soulNumber);
            } catch (IOException e) {
                e.printStackTrace();
            }
            weHaveAWinner(new Loader().readFile(soulNumber.split(" ")[1]));

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
