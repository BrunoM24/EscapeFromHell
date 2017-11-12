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

    public void keyHandler(){

        while (true) {

            Key key = screen.readInput();

            if (key != null) {

                if (key.getKind() == Key.Kind.ArrowLeft) {
                    game.checkMove(-1);

                }

                if (key.getKind() == Key.Kind.ArrowRight) {
                    game.checkMove(1);
                }

                if (key.getCharacter() == 'a') {
                    game.draw(-1);
                }

                if (key.getCharacter() == 's') {
                    game.draw(1);
                }

            }
        }
    }
}
