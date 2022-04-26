package io.github.c20c01.tool.proTool.Packets.general.Out;

import io.github.c20c01.tool.proTool.Packets.Packet;

import java.io.IOException;

public class PlayerPositionPacket extends Packet {
    public PlayerPositionPacket(double x, double feetY, double z, boolean onGround) throws IOException {
        super(0x11);
        putDouble(x);
        putDouble(feetY);
        putDouble(z);
        putBoolean(onGround);
        close();
    }
}
