package org.academiadecodigo.escapefromhell.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Loader {


    static String result = "";
    private String aux = "";

    public String readFile(String file){

        result = "";
        aux = "";

        FileReader reader = null;

        try {

            reader = new FileReader("/"+file+".txt");
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
