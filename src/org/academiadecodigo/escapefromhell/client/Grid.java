package org.academiadecodigo.escapefromhell.client;

/**
 * EscapeFromHell Created by BrunoM24 on 07/11/2017.
 */

public class Grid {


    private int[][] grid;
    private final int cols = 100;
    private final int rows = 30;


    public Grid() {

        this.grid = new int[rows][cols];
        initGrid();
    }

    /*
    *
    * */

    private void initGrid() {

        for (int row = 0; row < 30; row++) {
            for (int col = 10; col < 90; col++) {
                grid[row][col] = 0;
            }
        }
    }

    /*
    *
    * */

    public int[][] getGrid() {

        return grid;
    }

    public void update(int[][] grid){
        this.grid = grid;
    }

    public void updateCell(int val, int row, int col){
        this.grid[row][col] = val;
    }

    public int getValue(int row, int col) {
        return this.grid[row][col];
    }
}
