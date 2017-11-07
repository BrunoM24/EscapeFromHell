package org.academiadecodigo.escapefromhell.client;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;

/**
 * EscapeFromHell Created by BrunoM24 on 07/11/2017.
 */

public class Grid {

    private Screen screen;

    private int cols;
    private int rows;

    public Grid(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
    }

    public void init() {

        this.screen = TerminalFacade.createScreen();
        screen.startScreen();

        screen.setCursorPosition(this.screen.getTerminalSize().getColumns() / 2 -1, this.screen.getTerminalSize().getRows()-1);
        screen.refresh();

        while (true) {
            Key key = screen.readInput();

            if (key != null) {

                if(key.getKind() == Key.Kind.ArrowLeft){
                    moveLeft();
                }

                if(key.getKind() == Key.Kind.ArrowRight){
                    moveRight();
                }

                if(key.getCharacter() == 'a'){
                    drawLeft();
                }

                if(key.getCharacter() == 's'){
                    drawRight();
                }

            }

        }


    }

    private void drawRight() {
        System.out.println("s");
    }

    private void drawLeft() {
        System.out.println("a");
    }

    private void moveRight() {
       this.screen.setCursorPosition(this.screen.getCursorPosition().getColumn() + 1, this.screen.getCursorPosition().getRow());
       refresh();
    }

    private void moveLeft() {
        this.screen.setCursorPosition(this.screen.getCursorPosition().getColumn() - 1, this.screen.getCursorPosition().getRow());
        refresh();
    }

    private void refresh(){
        screen.refresh();
    }
}
