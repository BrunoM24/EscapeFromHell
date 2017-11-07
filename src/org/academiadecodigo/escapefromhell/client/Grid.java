package org.academiadecodigo.escapefromhell.client;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.CharacterPattern;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;

/**
 * EscapeFromHell Created by BrunoM24 on 07/11/2017.
 */

public class Grid {

    private Screen screen;
    private Boolean[][] grid;

    private int cols;
    private int rows;

    public Grid(int cols, int rows) {
        //this.cols = cols;
        //this.rows = rows;

        this.grid = new Boolean[30][100];
        initGrid();
    }

    private void initGrid() {
        for (int row = 0; row < 30; row++) {
            for (int col = 0; col < 100; col++) {
                grid[row][col] = false;
            }
        }
        /*for (int i = cols; i < cols; i++) {
            grid[29][i] = true;
        }*/
    }

    public void init() {

        this.screen = TerminalFacade.createScreen();
        screen.startScreen();
        // screen.setCursorPosition(this.screen.getTerminalSize().getColumns() / 2 - 1, this.screen.getTerminalSize().getRows() - 1);

        // random spawn from row 0

        spawnPlayer();

        refresh();

        while (true) {
            Key key = screen.readInput();

            if (key != null) {

                if (key.getKind() == Key.Kind.ArrowLeft) {
                    moveLeft();
                }

                if (key.getKind() == Key.Kind.ArrowRight) {
                    moveRight();
                }

                if (key.getCharacter() == 'a') {
                    drawLeft();
                }

                if (key.getCharacter() == 's') {
                    drawRight();
                }

                if (key.getCharacter() == 'p') {
                    showGrid();
                }

            }

        }

    }

    private void spawnPlayer () {

        screen.setCursorPosition((int)(Math.random()*(this.screen.getTerminalSize().getColumns()-1)), 0);

    }
    private void showGrid() {
        for (int row = 0; row < 30; row++) {
            for (int col = 0; col < 100; col++) {
                if (grid[row][col]) {
                    System.out.print("1");
                } else {
                    System.out.print("0");
                }
            }
            System.out.println("");
        }
    }

    private void drawRight() {
        this.grid[this.screen.getCursorPosition().getRow()][this.screen.getCursorPosition().getColumn() + 1] = true;
        refresh();
    }

    private void drawLeft() {
        this.grid[this.screen.getCursorPosition().getRow()][this.screen.getCursorPosition().getColumn() - 1] = true;
        refresh();
    }

    private void moveRight() {
        if (grid[this.screen.getCursorPosition().getRow()][this.screen.getCursorPosition().getColumn() + 1]) {

            if(grid[this.screen.getCursorPosition().getRow()-1][this.screen.getCursorPosition().getColumn() + 1]) {
                return;
            }

            this.screen.setCursorPosition(this.screen.getCursorPosition().getColumn() + 1, this.screen.getCursorPosition().getRow() - 1);

        } else {

            this.screen.setCursorPosition(this.screen.getCursorPosition().getColumn() + 1, this.screen.getCursorPosition().getRow());
        }
        checkFall();
        refresh();
        //TODO check fall
    }

    private void moveLeft() {
        if (grid[this.screen.getCursorPosition().getRow()][this.screen.getCursorPosition().getColumn() - 1]) {

            if(grid[this.screen.getCursorPosition().getRow()-1][this.screen.getCursorPosition().getColumn() - 1]) {
                return;
            }

            this.screen.setCursorPosition(this.screen.getCursorPosition().getColumn() - 1, this.screen.getCursorPosition().getRow() - 1);
        } else {

            this.screen.setCursorPosition(this.screen.getCursorPosition().getColumn() - 1, this.screen.getCursorPosition().getRow());
        }
        checkFall();
        refresh();
        System.out.println("x " + this.screen.getCursorPosition().getColumn() + " + y " + this.screen.getCursorPosition().getRow());
        //TODO check fall
    }


    private void checkFall() {

        if (this.screen.getCursorPosition().getRow() == 29) {
            return;
        }
        if (!grid[this.screen.getCursorPosition().getRow() + 1][this.screen.getCursorPosition().getColumn()]) {

            while (!grid[this.screen.getCursorPosition().getRow() + 1][this.screen.getCursorPosition().getColumn()]) {
                System.out.println(grid[this.screen.getCursorPosition().getRow() + 1][this.screen.getCursorPosition().getColumn()]);

                this.screen.setCursorPosition(this.screen.getCursorPosition().getColumn(), this.screen.getCursorPosition().getRow() + 1);
                if (this.screen.getCursorPosition().getRow() == 29) {
                    break;
                }
            }

        }

    }

    private void refresh() {

        for (int row = 0; row < 30; row++) {
            for (int col = 0; col < 100; col++) {
                if (this.grid[row][col]) {
                    this.screen.putString(col, row, " ", Terminal.Color.BLACK, Terminal.Color.CYAN);
                } else {
                    this.screen.putString(col, row, " ", Terminal.Color.BLACK, Terminal.Color.WHITE);
                }
            }
        }

        screen.refresh();
    }

}
