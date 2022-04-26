package io.github.c20c01.tool.musicBoxTool;

/**
 * @author : xxmicloxx
 * @see "<a href="https://github.com/xxmicloxx/NoteBlockAPI">Github</a>"
 */

public class Note {

    private byte instrument;
    private byte key;

    public Note(byte instrument, byte key) {
        this.instrument = instrument;
        this.key = changeNote(key);
    }

    private byte changeNote(byte input) {
        byte key = (byte) (input - 33);
        if (key < 0) {
            while (key < 0) key += 12;
        } else if (key > 24) {
            while (key > 24) key -= 12;
        }
        return key;
    }

    public byte getInstrument() {
        return instrument;
    }

    public void setInstrument(byte instrument) {
        this.instrument = instrument;
    }

    public byte getKey() {
        return key;
    }

    public void setKey(byte key) {
        this.key = key;
    }
}
