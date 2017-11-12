package org.academiadecodigo.escapefromhell.client;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by codecadet on 12/11/17.
 */
public class Lava {


    private int deathRow = 30;
    private Grid grid;
    private View view;
    private boolean stop = false;


    public Lava(Grid grid, View view) {

        this.grid = grid;
        this.view = view;
    }

    /**
     *
     */
    public void riseLava() {

        if (stop) {
            return;
        }
        deathRow--;
        for (int i = 12; i < (view.terminalSize_X() - 11); i++) {

            for (int j = deathRow; j < view.terminalSize_Y(); j++) {

                int number = ((int) (Math.random() * 2)) + 3;
                grid.getGrid()[j][i] = number;

            }
        }
    }

    /**
     * @return
     */
    public int getDeathRow() {
        return deathRow;
    }

    public void stopLava() {
        stop = true;
    }
}
