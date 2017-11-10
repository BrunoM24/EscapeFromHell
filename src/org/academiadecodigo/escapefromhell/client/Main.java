package org.academiadecodigo.escapefromhell.client;

/**
 * EscapeFromHell Created by BrunoM24 on 07/11/2017.
 */

public class Main {

    public static void main(String[] args) {

        Game game = new Game();
        if(args.length > 0){
            game.start(args[0], 6650);
        }else {
            game.start("127.0.0.1", 6650);
        }
    }

}
