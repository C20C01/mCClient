package io.github.c20c01.tool.proTool.Packets.general.Out;

import io.github.c20c01.tool.proTool.Packets.Packet;

public class ClientRespawnPacket extends Packet {
    public ClientRespawnPacket() {
        super(0x04);
        putVarInt(0);
    }
}
