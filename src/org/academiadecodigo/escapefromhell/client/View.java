package org.academiadecodigo.escapefromhell.client;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.screen.Screen;

public class View {

    private Screen screen;

    /*
    *
    * */
    public View() {

        this.screen = TerminalFacade.createScreen();
        screen.startScreen();
    }

    /*
    * Return columns
    * */
    public int terminalSize_X() {

        return this.screen.getTerminalSize().getColumns();
    }

    /*
    * Return rows
    * */
    public int terminalSize_Y() {

        return this.screen.getTerminalSize().getRows();
    }

    /*
    * Return Column of the player
    * */
    public int playerPos_X() {

        return this.screen.getCursorPosition().getColumn();
    }

    /*
    * Return Row of the player
    * */
    public int playerPos_Y() {

        return this.screen.getCursorPosition().getRow();
    }

    /*
    *
    * */
    public Screen getScreen() {

        return screen;
    }

    /*
    * Set position of the Player
    * */
    public void setPlayerPos(int rows, int cols) {

        screen.setCursorPosition(cols, rows);
    }
}
