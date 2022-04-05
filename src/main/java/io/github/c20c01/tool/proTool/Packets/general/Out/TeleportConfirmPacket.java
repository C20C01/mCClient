package io.github.c20c01.tool.proTool.Packets.general.Out;

import io.github.c20c01.tool.proTool.Packets.Packet;

import java.io.IOException;

public class TeleportConfirmPacket extends Packet {
    public TeleportConfirmPacket(int tpID) throws IOException {
        super(0x00);
        putVarInt(tpID);
        close();
    }
}
