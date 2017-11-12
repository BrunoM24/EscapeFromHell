package org.academiadecodigo.escapefromhell.sound;

public enum SoundType {
    START("theme.wav");

    private String path;

    SoundType(String path) {
        this.path = "/resources/" + path;
    }

    public String getPath() {
        return path;
    }

}