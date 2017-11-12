package org.academiadecodigo.escapefromhell.server;

import org.academiadecodigo.escapefromhell.client.Loader;

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
    private CopyOnWriteArrayList<PlayerHandler> playerConected;
    private int port;


    public Server(int port) {

        this.port = port;
        playerConected = new CopyOnWriteArrayList<>();
    }


    /*
    * Open a socket Server
    * Accept client connection
    * Instantiate  a PLayerHandler
    * Add a connection to CopyOnWriteArrayList
    * Submit the new task
    * */

    public void openServer() {

        ServerSocket server = null;

        try {
            server = new ServerSocket(this.port);

            while (playerConected.size() < 2) {

                Socket connection = server.accept();
                PlayerHandler playerHandler = new PlayerHandler(connection, this, playerConected.size() + 1);
                playerConected.add(playerHandler);

            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (PlayerHandler player : playerConected) {

                cachedPool.submit(player);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    * Send Star Game Message
    * **/
    public void sendStartMessage(Socket connection) {

        try {
            new PrintStream(connection.getOutputStream()).println("Start");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /*
    *   Broadcast
    * */
    public void broadCast(String message) {

        synchronized (playerConected) {

            try {
                for (int i = 0; i < playerConected.size(); i++) {

                    new PrintStream(playerConected.get(i).getConnection().getOutputStream()).println(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /*
    * Remove Player fom the List
    * when player close connection
    * */
    public void playerRemove(PlayerHandler playerHandler) {

        playerConected.remove(playerHandler);
    }
}
