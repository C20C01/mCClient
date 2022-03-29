package io.github.c20c01.tool.proTool.Packets.general.In;

import io.github.c20c01.tool.Position;
import io.github.c20c01.tool.proTool.Packets.Packet;
import io.github.c20c01.tool.proTool.VarInputStream;

import java.io.IOException;

public class EntityTeleportPacket extends Packet {
    private final int entityID;
    private final double x, y, z;
    private final byte yaw, pitch;
    private final boolean onGround;

    public EntityTeleportPacket(byte[] data) throws IOException {
        super(0x62, data);
        VarInputStream is = getInputStream();
        entityID = is.readVarInt();
        x = is.readDouble();
        y = is.readDouble();
        z = is.readDouble();
        yaw = is.readByte();
        pitch = is.readByte();
        onGround = is.readBoolean();
    }

    public int getEntityID() {
        return entityID;
    }

    public Position getPos() {
        return new Position(x, y, z);
    }

    public byte getYaw() {
        return yaw;
    }

    public byte getPitch() {
        return pitch;
    }

    public boolean isOnGround() {
        return onGround;
    }
}
