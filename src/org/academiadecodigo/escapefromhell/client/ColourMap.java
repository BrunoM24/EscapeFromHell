package org.academiadecodigo.escapefromhell.client;

import com.googlecode.lanterna.terminal.Terminal;

import java.util.HashMap;
import java.util.Map;

public class ColourMap {

    Map<Integer, Terminal.Color> colorMap = new HashMap<>();

    public void init() {

        colorMap.put(1, Terminal.Color.WHITE);
        colorMap.put(2, Terminal.Color.GREEN);
        colorMap.put(3, Terminal.Color.RED);
        colorMap.put(4, Terminal.Color.YELLOW);

    }

    public Terminal.Color getColour(Integer number) {

        return colorMap.get(number);

    }


}

