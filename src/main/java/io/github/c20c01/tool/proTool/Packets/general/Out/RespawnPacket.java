package io.github.c20c01.tool.proTool.Packets.general.Out;

import io.github.c20c01.tool.proTool.Packets.Packet;

public class RespawnPacket extends Packet {
    public RespawnPacket() {
        super(0x04);
        putVarInt(0);
    }
}
