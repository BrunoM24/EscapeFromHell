package org.academiadecodigo.escapefromhell.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by codecadet on 10/11/17.
 */
public class Loadmenu {

    static String result = "";
    private String aux = "";

    public String readFile(){

        FileReader reader = null;


        try {

            reader = new FileReader("Menu.txt");
            BufferedReader br = new BufferedReader(reader);

            while ((aux = br.readLine()) != null) {

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
        return result;
    }
}
