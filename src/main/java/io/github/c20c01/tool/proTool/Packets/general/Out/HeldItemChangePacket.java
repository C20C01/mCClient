package io.github.c20c01.tool.proTool.Packets.general.Out;

import io.github.c20c01.tool.proTool.Packets.Packet;

import java.io.IOException;

public class HeldItemChangePacket extends Packet {
    public HeldItemChangePacket(int slot) throws IOException {
        super(0x25);
        putShort(slot);
        close();
    }
}
