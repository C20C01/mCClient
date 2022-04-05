package io.github.c20c01.tool.proTool.Packets.general.In;

import io.github.c20c01.tool.proTool.Packets.Packet;
import io.github.c20c01.tool.proTool.VarInputStream;

import java.io.IOException;

public class LoginSetCompressionPacket extends Packet {

    private final int threshold;

    public LoginSetCompressionPacket(byte[] data) throws IOException {
        super(0x03, data);
        VarInputStream is = getInputStream();
        this.threshold = is.readVarInt();
        is.close();
        close();
    }

    public int getThreshold() {
        return threshold;
    }

}
