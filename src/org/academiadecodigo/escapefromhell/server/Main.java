package org.academiadecodigo.escapefromhell.server;

import org.academiadecodigo.escapefromhell.client.Server;

/**
 * EscapeFromHell Created by BrunoM24 on 07/11/2017.
 */

public class Main {
    public static void main(String[] args) {

        Server server = new Server(666);
        server.openServer();
    }
}
