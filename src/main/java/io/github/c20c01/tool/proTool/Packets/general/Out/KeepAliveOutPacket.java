package io.github.c20c01.tool.proTool.Packets.general.Out;

import io.github.c20c01.tool.proTool.Packets.Packet;

public class KeepAliveOutPacket extends Packet {

    public KeepAliveOutPacket(Long aliveId) {
        super(0x0f);
        putLong(aliveId);
    }

}
