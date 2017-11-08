package org.academiadecodigo.escapefromhell.client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by codecadet on 04/11/2017.
 */
public class Server {

    /*
    * Create cachedPoll to handle the tasks(threads)
    * Create arrayList thread safe to save connections
    * */

    private ExecutorService cachedPool = Executors.newCachedThreadPool();
    //private CopyOnWriteArrayList<PlayerHandler> playerConected;
    private int port;


    public Server(int port){

        this.port = port;
        //playerConected = new CopyOnWriteArrayList<>();
    }


    /*
    * Open a socket Server
    * Accept client connection
    * Instantiate  a PLayerHandler
    * Add a connection to CopyOnWriteArrayList
    * Submit the new task
    * */

    public void openServer()  {

        try {
            ServerSocket server = new ServerSocket(this.port);

            while (true) {

                Socket connection = server.accept();

                //PlayerHandler playerSandler = new PlayerHandler(connection, this);

                //playerConected.add(playerSandler);

                //cachedPool.submit(playerSandler);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    /*
    * Broadcast message for all clients
    * connected with the player
    * */
/*
    public void sendMessage(String message) {

        synchronized (playerConected) {
            try {
                for (int i = 0; i < playerConected.size(); i++) {
                    PrintStream out = new PrintStream(playerConected.get(i).getConnection().getOutputStream());

                    out.println(message);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

+/
    /*
    * Remove Player fom the List
    * when player close connection
    * with the server
    * */
/*
    public void playerRemove(PlayerHandler playerHandler){

        playerConected.remove(playerHandler);
    }
*/
}
/*
* syncronize
* */