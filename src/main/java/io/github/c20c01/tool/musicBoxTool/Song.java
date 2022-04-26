package io.github.c20c01.tool.musicBoxTool;

import java.io.File;
import java.util.HashMap;

/**
 * @author : xxmicloxx
 * @see "<a href="https://github.com/xxmicloxx/NoteBlockAPI">Github</a>"
 */

public class Song {

    private final HashMap<Integer, Layer> layerHashMap;
    private final short songHeight;
    private final short length;
    private final String title;
    private final File path;
    private final String author;
    private final String description;
    private final float speed;
    private final float delay;

    public Song(float speed, HashMap<Integer, Layer> layerHashMap,
                short songHeight, final short length, String title, String author,
                String description, File path) {
        this.speed = speed;
        delay = 20 / speed;
        this.layerHashMap = layerHashMap;
        this.songHeight = songHeight;
        this.length = length;
        if (title.equals("")) title = path.getName();
        this.title = title;
        if (author.equals("")) author = "Unknown";
        this.author = author;
        this.description = description;
        this.path = path;
    }

    public HashMap<Integer, Layer> getLayerHashMap() {
        return layerHashMap;
    }

    public short getSongHeight() {
        return songHeight;
    }

    public short getLength() {
        return length;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public File getPath() {
        return path;
    }

    public String getDescription() {
        return description;
    }

    public float getSpeed() {
        return speed;
    }

    public float getDelay() {
        return delay;
    }
}
