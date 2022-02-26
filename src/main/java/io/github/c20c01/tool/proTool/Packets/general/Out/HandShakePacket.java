package io.github.c20c01.tool.proTool.Packets.general.Out;

import io.github.c20c01.tool.proTool.Packets.Packet;

public class HandShakePacket extends Packet {

    public HandShakePacket(int protocol, String host, int port, int state) {
        super(0x00);
        putVarInt(protocol);
        putString(host);
        putShort(port);
        putVarInt(state);
    }

}
