package org.academiadecodigo.escapefromhell.server;

import java.io.*;
import java.net.Socket;

/**
 * Created by codecadet on 04/11/2017.
 */

public class PlayerHandler implements Runnable {


    private Server server;
    private Socket connection;
    private BufferedReader in;
    private PrintStream out;
    private boolean readyToPlay;
    private int ID;


    /*
    * Constructor recives an accepted client Socket from the server
    * Receives a Server for futures communications
    * */

    public PlayerHandler(Socket client, Server server, int ID) {

        this.connection = client;
        this.server = server;
        this.ID = ID;


    }

    /*
    *
    * */

    @Override
    public void run() {

        try {

            PrintStream out = new PrintStream(connection.getOutputStream());
            out.println("ID:Soul " + ID);

        } catch (IOException e) {
            e.printStackTrace();
        }

        server.sendStartMessage(connection);

        try {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            boolean shutdownRequested = false;

            while (!shutdownRequested) {


                String message;
                if ((message = in.readLine()) != null) {

                    System.out.println(message);
                    server.sendMessage(message);

                } else {

                    shutdownRequested = true;
                    shuDownConection();

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
    * Close socket connection when PLayer out
    * Remove player from the Arraylis
    * */

    private void shuDownConection() throws IOException {

        connection.close();
        server.playerRemove(this);
    }

    /*
    * Return the Socket connection
    * */

    public Socket getConnection() {
        return connection;
    }

    public void startGame() {
        readyToPlay = true;
    }

}
