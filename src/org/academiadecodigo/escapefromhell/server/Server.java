package org.academiadecodigo.escapefromhell.server;

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
    private LoadLevel loadLevel = new LoadLevel();


    //private String level;


    public Server(int port) {

        this.port = port;
        playerConected = new CopyOnWriteArrayList<>();
        //level = loadLevel.readFile();
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

            while (playerConected.size() < 3) {

                Socket connection = server.accept();
                PlayerHandler playerHandler = new PlayerHandler(connection, this);
                playerConected.add(playerHandler);

            }

            for (PlayerHandler player : playerConected) {
                System.out.println("submiting player");
                cachedPool.submit(player);
            }

            System.out.println("all players submitted. game is starting");


        while (true) {
            Socket connection = server.accept();
            PlayerHandler playerHandler = new PlayerHandler(connection, this);
            playerConected.add(playerHandler);
            cachedPool.submit(playerHandler);
        }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    * Broadcast message for all clients
    * connected with the player
    * */
    public void sendMap(Socket connection) {

        System.out.println("try");
        try {
            new PrintStream(connection.getOutputStream()).println(loadLevel.readFile());
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        System.out.println("end");

    }

    /*
    *
    * */

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

    public int checkNumberOfPlayers() {
        return playerConected.size();
    }

    /*
    * Remove Player fom the List
    * when player close connection
    * with the server
    * */

    public void playerRemove(PlayerHandler playerHandler) {

        playerConected.remove(playerHandler);
    }

}
