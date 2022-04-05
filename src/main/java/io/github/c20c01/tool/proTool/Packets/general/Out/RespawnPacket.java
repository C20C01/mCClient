package io.github.c20c01.tool.proTool.Packets.general.Out;

import io.github.c20c01.tool.proTool.Packets.Packet;

import java.io.IOException;

public class RespawnPacket extends Packet {
    public RespawnPacket() throws IOException {
        super(0x04);
        putVarInt(0);
        close();
    }
}
