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

    public String readWine() {

        FileReader reader = null;

        try {

            reader = new FileReader("1.txt");
            BufferedReader brWin = new BufferedReader(reader);

            while ((aux = brWin.readLine()) != null) {

                result += (aux + "/");

            }
        }
        catch (IOException ex){
            System.out.println("error");
        }
        finally {
            try {
                reader.close();

            }catch (IOException ex){

            }
        }
        System.out.println(result);
        return result;
    }
}



