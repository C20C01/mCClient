package io.github.c20c01.tool.proTool.Packets.general.Out;

import io.github.c20c01.tool.proTool.Packets.Packet;

import java.io.IOException;

public class KeepAliveOutPacket extends Packet {

    public KeepAliveOutPacket(Long aliveId) throws IOException {
        super(0x0f);
        putLong(aliveId);
        close();
    }

}
