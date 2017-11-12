package org.academiadecodigo.escapefromhell.client;

import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class Player {

    private Screen screen;
    private Game game;
    private Grid grid;
    private View view;


    public Player(View view, Game game, Grid grid ) {

        this.screen = view.getScreen();
        this.game = game;
        this.view = view;
        this.grid = grid;

    }

    /*
    *
    * */

    public void keyHandler(Socket connection) {

        while (true) {

            Key key = screen.readInput();

            if (key != null) {

                if (key.getKind() == Key.Kind.ArrowLeft) {
                    checkMove(-1, connection);

                }

                if (key.getKind() == Key.Kind.ArrowRight) {
                    checkMove(1, connection);
                }

                if (key.getCharacter() == 'a') {
                    draw(-1, connection);
                }

                if (key.getCharacter() == 's') {
                    draw(1, connection);
                }

            }
        }
    }

    /*
    *
    * */
    public void spawnPlayer(int row) {

        screen.setCursorPosition(((int) (Math.random() * 77)) + 11, row);

    }

    /*
    *
    * */
    public void draw(int direction, Socket connection) {

        if (game.isDead() || game.isHasWon()) {
            return;
        }

        if (view.playerPos_X() == view.terminalSize_X() - 1 || view.playerPos_X() == 0) {
            return;
        }

        grid.getGrid()[view.playerPos_Y()][view.playerPos_X() + direction] = 1;
        game.refresh();

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
    public void checkMove(int direction, Socket connection) {


        if (game.isDead()) {
            return;
        }

        if (game.isHasWon()) {
            return;
        }

        if ((direction == 1 && (view.playerPos_X() == view.terminalSize_X() - 1)) || (direction == -1 && (view.playerPos_X() == 0))) {

            return;
        }

        if (grid.getGrid()[view.playerPos_Y()][view.playerPos_X() + direction] == 1 && grid.getGrid()[view.playerPos_Y() - 1][view.playerPos_X()] == 1) {
            return;
        }

        if (grid.getGrid()[view.playerPos_Y()][view.playerPos_X() + direction] != 1) {
            move(direction, 0, connection);
            return;
        }

        if (grid.getGrid()[view.playerPos_Y()][view.playerPos_X() + direction] == 1 && grid.getGrid()[view.playerPos_Y() - 1][view.playerPos_X() + direction] != 1) {
            move(direction, 1, connection);
            return;
        }

    }

    /*
    *
    * */
    private void move(int direction, int row, Socket connection) {

        if (game.isDead() || game.isHasWon()) {
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
        game.checkWin();
        checkFall(connection);
        game.refresh();
    }

        /*
    * cheack if the player position is on the botton row
    * while cell below player is empty incrise pY position of the player
    * */

    public void checkFall(Socket connection) {

        if (this.view.playerPos_Y() == view.terminalSize_Y() - 1) {
            return;
        }

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