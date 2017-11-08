package org.academiadecodigo.escapefromhell.client;

/**
 * EscapeFromHell Created by BrunoM24 on 07/11/2017.
 */

public class Grid {


    private Boolean[][] grid;
    private final int cols = 100;
    private final int rows = 30;


    public Grid() {

        this.grid = new Boolean[rows][cols];
        initGrid();
    }


    /*
    *
    * */

    private void initGrid() {

        for (int row = 0; row < 30; row++) {
            for (int col = 0; col < 100; col++) {
                grid[row][col] = false;
            }
        }
    }


    /*
    *
    * */

    public Boolean[][] getGrid() {

        return grid;
    }

}
