package org.academiadecodigo.escapefromhell.server;

import com.sun.tools.internal.ws.wsdl.document.soap.SOAPUse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
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


    /*
    * Constructor recives an accepted client Socket from the server
    * Receives a Server for futures communications
    * */

    public PlayerHandler(Socket client, Server server) {

        this.connection = client;
        this.server = server;

    }


    /*
    *
    * */

    @Override
    public void run() {


        try {

            server.releaselock();
            try {
                server.increaseNmlock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            server.sendMap(connection);

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
        } //catch (InterruptedException e){
            //System.err.println(e.getMessage());
        //}
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
