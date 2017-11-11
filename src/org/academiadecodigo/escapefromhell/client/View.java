package org.academiadecodigo.escapefromhell.client;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.screen.Screen;

public class View {

    private Screen screen;

    public View() {

        this.screen = TerminalFacade.createScreen();
        screen.startScreen();
    }

    public int terminalSize_X() {

        return this.screen.getTerminalSize().getColumns();
    }

    public int terminalSize_Y() {

        return this.screen.getTerminalSize().getRows();
    }

    public int playerPos_X() {

        return this.screen.getCursorPosition().getColumn();
    }

    public int playerPos_Y() {

        return this.screen.getCursorPosition().getRow();
    }

    public Screen getScreen() {

        return screen;
    }

    public void setPlayerPos(int rows, int cols) {

        screen.setCursorPosition(cols, rows);
    }
}
