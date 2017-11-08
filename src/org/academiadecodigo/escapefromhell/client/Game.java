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

        refresh();

        loadLevel();

        spawnPlayer(23);

    }

    private void spawnPlayer (int row) {

        view.setPlayerPos((int)(Math.random()*view.terminalSize_X()), row);

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

            if(grid.getGrid()[view.playerPos_Y()-1][view.playerPos_X() + 1]){
                return;
            }
            //block on top - cannot cross stair
            if(grid.getGrid()[view.playerPos_Y()-1][view.playerPos_X()] && grid.getGrid()[view.playerPos_Y()][view.playerPos_X()+1]) {
                return;
            }

            view.setPlayerPos(view.playerPos_X() + 1, view.playerPos_Y() - 1);

        }else {

            view.setPlayerPos(view.playerPos_X() + 1, view.playerPos_Y());

        }

        checkFall();

        refresh();
    }


    /*
    *
    * */

    public void moveLeft() {

        if(grid.getGrid()[view.playerPos_Y()][view.playerPos_X() - 1]){

            if(grid.getGrid()[view.playerPos_Y()-1][view.playerPos_X() - 1]){
                return;
            }

            //block on top - cannot cross stair
            if(grid.getGrid()[view.playerPos_Y()-1][view.playerPos_X()] && grid.getGrid()[view.playerPos_Y()][view.playerPos_X()-1]) {
                return;
            }

            view.setPlayerPos(view.playerPos_X() - 1, view.playerPos_Y() - 1);

        }else {

            view.setPlayerPos(view.playerPos_X() - 1, view.playerPos_Y());
        }

        checkFall();

        refresh();
    }

    private void checkFall() {

        if (this.view.playerPos_Y() == view.terminalSize_Y()-1) {
            return;
        }
        if (!grid.getGrid()[this.view.playerPos_Y() + 1][this.view.playerPos_X()]) {

            while (!grid.getGrid()[this.view.playerPos_Y() + 1][this.view.playerPos_X()]) {

                this.view.setPlayerPos(this.view.playerPos_X(), this.view.playerPos_Y()+1);
                if (this.view.playerPos_Y() == view.terminalSize_Y()-1) {
                    break;
                }
            }

        }

    }
    /*
    *
    * */

    private void refresh() {

        for (int row = 0; row < 30; row++) {
            for (int col = 0; col < 100; col++) {

                if (grid.getGrid()[row][col]) {
                    this.screen.putString(col, row, " ", Terminal.Color.CYAN, Terminal.Color.WHITE);
                } else {
                    this.screen.putString(col, row, " ", Terminal.Color.CYAN, Terminal.Color.BLACK);
                }

            }
        }

        screen.refresh();
    }


    public void harakiri (int row) {

        spawnPlayer(row);
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
