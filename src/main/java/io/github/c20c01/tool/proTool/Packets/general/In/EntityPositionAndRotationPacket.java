package io.github.c20c01.tool.proTool.Packets.general.In;

import io.github.c20c01.tool.Position;
import io.github.c20c01.tool.proTool.Packets.Packet;
import io.github.c20c01.tool.proTool.VarInputStream;

import java.io.IOException;

public class EntityPositionAndRotationPacket extends Packet {
    private final int entityID;
    private final short mx, my, mz;
    private final byte yaw, pitch;
    private final boolean onGround;

    public EntityPositionAndRotationPacket(byte[] data) throws IOException {
        super(0x2A, data);
        VarInputStream is = getInputStream();
        entityID = is.readVarInt();
        mx = is.readShort();
        my = is.readShort();
        mz = is.readShort();
        yaw = is.readByte();
        pitch = is.readByte();
        onGround = is.readBoolean();
    }

    public int getEntityID() {
        return entityID;
    }

    public Position getPos() {
        return new Position(mx, my, mz);
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
