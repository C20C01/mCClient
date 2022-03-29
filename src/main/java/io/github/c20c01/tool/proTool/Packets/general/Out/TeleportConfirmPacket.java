package io.github.c20c01.tool.proTool.Packets.general.Out;

import io.github.c20c01.tool.proTool.Packets.Packet;

public class TeleportConfirmPacket extends Packet {
    public TeleportConfirmPacket(int tpID) {
        super(0x00);
        putVarInt(tpID);
    }
}
