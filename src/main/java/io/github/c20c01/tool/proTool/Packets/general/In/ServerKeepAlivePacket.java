package io.github.c20c01.tool.proTool.Packets.general.In;

import io.github.c20c01.tool.proTool.Packets.Packet;

import java.io.IOException;

public class ServerKeepAlivePacket extends Packet {

    private final long aliveId;

    public ServerKeepAlivePacket(byte[] data) throws IOException {
        super(0x21, data);
        this.aliveId = getInputStream().readLong();
    }

    public long getAliveId() {
        return aliveId;
    }

}
