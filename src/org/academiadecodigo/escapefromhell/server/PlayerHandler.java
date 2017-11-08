package org.academiadecodigo.escapefromhell.server;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPUse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Created by codecadet on 04/11/2017.
 */

public class PlayerHandler implements Runnable{


    private Server server;
    private Socket connection;
    private BufferedReader in;
    private PrintStream out;


    /*
    * Constructor recives an accepted client Socket from the server
    * Receives a Server for futures communications
    * */

    public PlayerHandler(Socket client, Server server){

        this.connection = client;
        this.server = server;

    }


    /*
    *
    * */

    @Override
    public void run() {

        try {

            System.out.println("connected");
            boolean shutdownRequested = false;
            while (!shutdownRequested){

                if(!server.checkNumberOfPlayers()){

                    out.println("waiting for players");
                    continue;
                }
                String message;
                if((message = in.readLine()) != null){

                    server.sendMessage();

                } else{

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

    public Socket getConnection(){
        return connection;
    }


}
