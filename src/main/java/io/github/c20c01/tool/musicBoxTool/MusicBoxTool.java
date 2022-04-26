package io.github.c20c01.tool.musicBoxTool;

import io.github.c20c01.Main;
import io.github.c20c01.tool.PositionTool;
import io.github.c20c01.tool.proTool.MinecraftClient;

import java.io.File;
import java.io.IOException;

public class MusicBoxTool {
    protected final MinecraftClient client;
    protected Song song;
    protected Thread playerThread;
    protected boolean playing = false;
    protected boolean destroyed = false;
    protected short tick = -1;
    protected long[][] pos;

    public MusicBoxTool(MinecraftClient client) {
        this.client = client;
    }

    public void loadSong(File songFile) {
        if (playerThread != null && playerThread.isAlive()) {
            destroy();
            Main.output("The current playing has been canceled.\n", true);
        }
        song = NBSDecoder.parse(songFile);
        if (song != null) {
            Main.output("============================================\n" +
                    "| title: " + song.getTitle() + "\n" +
                    "| author: " + song.getAuthor() + "\n" +
                    "|------------------------------------------\n" +
                    "| length: " + song.getLength() + "\n" +
                    "| speed: " + song.getSpeed() + "T/s\n" +
                    "============================================", true);
        }
    }

    public boolean startSong() {
        if (song == null) {
            Main.output("Can't play! No song loaded.", true);
            return false;
        } else {
            if (!(playerThread != null && playerThread.isAlive())) {
                createPos();
                createThread();
            }
            playing = true;
        }
        return true;
    }

    public void pauseSong() {
        playing = false;
    }

    public void stopSong() {
        if (playerThread != null && playerThread.isAlive()) destroy();
    }

    public void checkPlaying() {
        if (song != null) {
            Main.output("============================================\n" +
                    "| Song: " + song.getTitle() + "\n" +
                    "| ", true, false);
            int per = Math.round((float) tick / song.getLength() * 100);
            int done = (int) (per * 0.4);
            for (int i = 0; i < done; i++) Main.output("#", true, false);
            int undone = 40 - done;
            for (int i = 0; i < undone; i++) Main.output("-", true, false);
            Main.output("\n| " + per + "% (" + tick + "/" + song.getLength() + ")\n" +
                    "| Playing: " + (playing ? "yes" : "no") + "\n" +
                    "============================================", true);
        }

    }

    protected void createPos() {
        int bx = (int) Math.floor(client.playerPos.x()) - 4;
        int py = (int) Math.floor(client.playerPos.y());
        int pz = (int) Math.floor(client.playerPos.z());
        int[] y = {py - 3, py, py + 3};
        int[] z = {pz - 4, pz - 1, pz + 2};
        pos = new long[8][25];
        int by, bz;
        for (int i = 0; i < 8; i++) {
            switch (i) {
                case 1 -> {
                    bz = z[2];
                    by = y[1];
                }
                case 2 -> {
                    bz = z[0];
                    by = y[0];
                }
                case 3 -> {
                    bz = z[2];
                    by = y[0];
                }
                case 4 -> {
                    bz = z[1];
                    by = y[0];
                }
                case 5 -> {
                    bz = z[0];
                    by = y[1];
                }
                case 6 -> {
                    bz = z[0];
                    by = y[2];
                }
                case 7 -> {
                    bz = z[2];
                    by = y[2];
                }
                default -> {
                    bz = z[1];
                    by = y[2];
                }
            }
            for (int j = 0; j < 25; j++) {
                int xx = j % 9;
                int zz = j / 9;
                pos[i][j] = PositionTool.getPosition(bx + xx, by, bz + zz);
            }
        }
    }

    protected void createThread() {
        playerThread = new Thread(() -> {
            while (!destroyed) {
                long startTime = System.currentTimeMillis();
                synchronized (MusicBoxTool.this) {
                    if (playing) {
                        tick++;
                        if (tick > song.getLength()) {
                            destroy();
                        }
                        playTick(tick);
                    }
                }
                long duration = System.currentTimeMillis() - startTime;
                float delayMillis = song.getDelay() * 50;
                if (duration < delayMillis) {
                    try {
                        //noinspection BusyWait
                        Thread.sleep((long) (delayMillis - duration));
                    } catch (InterruptedException ignore) {
                    }
                }
            }
            destroyed = false;
        });
        playerThread.setPriority(Thread.MAX_PRIORITY);
        playerThread.start();
    }

    protected void playTick(short tick) {
        for (Layer l : song.getLayerHashMap().values()) {
            Note note = l.getNote(tick);
            if (note != null) {
                try {
                    client.clickBlock(pos[note.getInstrument()][note.getKey()]);
                } catch (IOException e) {
                    Main.output(e.getMessage(), true);
                    destroy();
                }
            }
        }
    }

    protected void destroy() {
        playing = false;
        tick = -1;
        destroyed = true;
        pos = null;
    }
}
