package io.github.c20c01.tool.proTool.Packets.general.In;

import io.github.c20c01.tool.proTool.Packets.Packet;

import java.io.IOException;

public class ServerLoginSetCompressionPacket extends Packet {

    private final int threshold;

    public ServerLoginSetCompressionPacket(byte[] data) throws IOException {
        super(0x03, data);
        this.threshold = getInputStream().readVarInt();
    }

    public int getThreshold() {
        return threshold;
    }

}
