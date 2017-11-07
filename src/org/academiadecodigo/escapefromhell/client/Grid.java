package org.academiadecodigo.escapefromhell.client;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.terminal.Terminal;

/**
 * EscapeFromHell Created by BrunoM24 on 07/11/2017.
 */


public class Grid {

    private int cols;
    private int rows;

    public Grid(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
    }

    public void init(){
        Screen screen = TerminalFacade.createScreen();
        screen.getTerminal().getTerminalSize().setColumns(this.cols);
        screen.getTerminal().getTerminalSize().setRows(this.rows);

        ScreenWriter screenWriter = new ScreenWriter(screen);
        screenWriter.setBackgroundColor(Terminal.Color.WHITE);
        screenWriter.setForegroundColor(Terminal.Color.BLUE);

        screen.startScreen();
    }
}
