package io.github.c20c01.tool.proTool;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class VarOutputStream extends DataOutputStream {

    public VarOutputStream(OutputStream out) {
        super(out);
    }

    public void writeString(String v) throws IOException {
        byte[] sBytes = v.getBytes(StandardCharsets.UTF_8);
        writeVarInt(sBytes.length);
        write(sBytes);
    }

    public void writeVarInt(int value) throws IOException {
        do {
            byte temp = (byte) (value & 0b01111111);
            value >>>= 7;
            if (value != 0)
                temp |= 0b10000000;
            writeByte(temp);
        } while (value != 0);
    }

    public static int checkVarIntSize(int v) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        VarOutputStream vos = new VarOutputStream(bos);
        try {
            vos.writeVarInt(v);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return bos.size();
    }
}
