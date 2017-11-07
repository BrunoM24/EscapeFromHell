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

    private Position position;

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
    }

    public void init() {

        this.screen = TerminalFacade.createScreen();
        screen.startScreen();

        System.out.println(this.screen.getTerminalSize().getColumns());
        System.out.println(this.screen.getTerminalSize().getRows());
        screen.setCursorPosition(this.screen.getTerminalSize().getColumns() / 2 - 1, this.screen.getTerminalSize().getRows() - 1);
        //screen.refresh();
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

            }

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
        //TODO check fall
        if(grid[this.screen.getCursorPosition().getRow()][this.screen.getCursorPosition().getColumn() + 1]){
            this.screen.setCursorPosition(this.screen.getCursorPosition().getColumn() + 1, this.screen.getCursorPosition().getRow() - 1);
        }else {
            this.screen.setCursorPosition(this.screen.getCursorPosition().getColumn() + 1, this.screen.getCursorPosition().getRow());
        }

        refresh();
    }

    private void moveLeft() {
        //TODO check fall
        if(grid[this.screen.getCursorPosition().getRow()][this.screen.getCursorPosition().getColumn() - 1]){
            this.screen.setCursorPosition(this.screen.getCursorPosition().getColumn() - 1, this.screen.getCursorPosition().getRow() - 1);
        }else {
            this.screen.setCursorPosition(this.screen.getCursorPosition().getColumn() - 1, this.screen.getCursorPosition().getRow());
        }
        refresh();
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
