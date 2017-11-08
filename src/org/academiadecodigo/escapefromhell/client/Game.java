package org.academiadecodigo.escapefromhell.client;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;

/**
 * EscapeFromHell Created by BrunoM24 on 07/11/2017.
 */


public class Game {

    private Screen screen;
    private Grid grid;
    private View view;
    private Player player;


    /*
    *
    * */

    public Game(){

        this.grid = new Grid();
        this.view = new View();
        this.screen = view.getScreen();
        this.player = new Player(this.view,this);
    }


    /*
    *
    * */

    public void start() {

        init();
        player.moveDirection();

    }


    /*
    *
    * */

    public void init() {

        view.setPlayerPos(view.terminalSize_X() / 2 - 1, view.terminalSize_Y() - 1);

        refresh();

        loadLevel();

    }


    /*
    *
    * */

    public void drawRight() {

        grid.getGrid()[view.playerPos_Y()][view.playerPos_X() + 1] = true;
        refresh();

    }


    /*
    *
    * */

    public void drawLeft() {

        grid.getGrid()[view.playerPos_Y()][view.playerPos_X() - 1] = true;
        refresh();

    }


    /*
    *
    * */

    public void moveRight() {

        if(grid.getGrid()[view.playerPos_Y()][view.playerPos_X() + 1]){

            view.setPlayerPos(view.playerPos_X() + 1, view.playerPos_Y() - 1);

        }else {

            view.setPlayerPos(view.playerPos_X() + 1, view.playerPos_Y());

        }

        refresh();
    }


    /*
    *
    * */

    public void moveLeft() {

        if(grid.getGrid()[view.playerPos_Y()][view.playerPos_X() - 1]){

            view.setPlayerPos(view.playerPos_X() - 1, view.playerPos_Y() - 1);

        }else {

            view.setPlayerPos(view.playerPos_X() - 1, view.playerPos_Y());
        }

        refresh();
    }


    /*
    *
    * */

    private void refresh() {

        for (int row = 0; row < 30; row++) {
            for (int col = 0; col < 100; col++) {

                if (grid.getGrid()[row][col]) {
                    this.screen.putString(col, row, " ", Terminal.Color.BLACK, Terminal.Color.CYAN);
                } else {
                    this.screen.putString(col, row, " ", Terminal.Color.BLACK, Terminal.Color.WHITE);
                }

            }
        }

        screen.refresh();
    }


    /*
    *
    * */

    public void loadLevel() {

        LoadLevel r = new LoadLevel();
        r.readFile();

        String[] split;
        String[] resultSplit = LoadLevel.result.split("/");;

        for (int i = 0; i < 30; i++) {

            split = resultSplit[i].split("");

            for (int j = 0; j < 100; j++) {

                if (split[j].equals("1"))
                    grid.getGrid()[i][j] = true;

                else
                    grid.getGrid()[i][j] = false;
            }
        }
    }

}
