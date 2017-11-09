package org.academiadecodigo.escapefromhell.client;

import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;

public class Player {

    private Screen screen;
    private Game game;



    public Player(View view, Game game){

        this.screen = view.getScreen();
        this.game = game;
    }

    /*
    *
    * */

    public void moveDirection(){

        while (true) {

            Key key = screen.readInput();

            if (key != null) {

                if (key.getKind() == Key.Kind.ArrowLeft) {
                    game.moveLeft();
                }

                if (key.getKind() == Key.Kind.ArrowRight) {
                    game.moveRight();
                }

                if (key.getCharacter() == 'a') {
                    game.drawLeft();
                }

                if (key.getCharacter() == 's') {
                    game.drawRight();
                }

                if (key.getCharacter() == 'h') {
                    game.harakiri(0);
                }

            }

        }
    }
}
