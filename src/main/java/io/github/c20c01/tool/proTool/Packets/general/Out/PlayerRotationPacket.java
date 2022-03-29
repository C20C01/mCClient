package io.github.c20c01.tool.proTool.Packets.general.Out;

import io.github.c20c01.tool.proTool.Packets.Packet;

public class PlayerRotationPacket extends Packet {
    public PlayerRotationPacket(float yaw, float pitch, boolean onGround) {
        super(0x13);
        putFloat(yaw);
        putFloat(pitch);
        putBoolean(onGround);
    }
}
