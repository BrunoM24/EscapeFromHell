package org.academiadecodigo.escapefromhell.client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by codecadet on 11/11/17.
 */
public class LoadWin {

    static String result = "";
    private String aux = "";

    public String readWin() {

        FileReader reader = null;

        try {

            reader = new FileReader("2.txt");
            BufferedReader br = new BufferedReader(reader);

            while ((aux = br.readLine()) != null) {

                result += (aux + "/");

            }


        } catch (IOException ex) {
            System.out.println("error");
        } finally {
            try {
                reader.close();

            } catch (IOException ex) {

            }
        }
        System.out.println(result);
        return result;
    }

    public String readWinnerNumber(String winnerNumber) {

        FileReader reader = null;
        result ="";
        aux="";

        try {
            reader = new FileReader(winnerNumber + ".txt");
            BufferedReader br = new BufferedReader(reader);
        //     BufferedReader br = new BufferedReader(reader);

        while ((aux = br.readLine()) != null)

        {

            result += (aux + "/");

        }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}



