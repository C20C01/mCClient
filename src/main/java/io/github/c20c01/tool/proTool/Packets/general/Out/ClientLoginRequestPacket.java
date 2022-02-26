package io.github.c20c01.tool.proTool.Packets.general.Out;

import io.github.c20c01.tool.proTool.Packets.Packet;

public class ClientLoginRequestPacket extends Packet {

    public ClientLoginRequestPacket(String username) {
        super(0x00);
        putString(username);
    }
}
