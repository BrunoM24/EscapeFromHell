package org.academiadecodigo.escapefromhell.client;

/**
 * EscapeFromHell Created by BrunoM24 on 07/11/2017.
 */


public class Game {

    private Grid grid;

    Game(){
        this.grid = new Grid(100, 50);
    }

    public void start() {

        grid.init();
    }

}
