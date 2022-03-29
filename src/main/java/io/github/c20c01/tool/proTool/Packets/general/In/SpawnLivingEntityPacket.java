package io.github.c20c01.tool.proTool.Packets.general.In;

import io.github.c20c01.tool.Position;
import io.github.c20c01.tool.proTool.Packets.Packet;
import io.github.c20c01.tool.proTool.VarInputStream;

import java.io.IOException;
import java.util.UUID;

public class SpawnLivingEntityPacket extends Packet {
    private final int ID;
    private final UUID uuid;
    private final int type;
    private final double x, y, z;
    private final byte pitch, yaw, headYaw;
    private final short vX, vY, vZ;

    public SpawnLivingEntityPacket(byte[] data) throws IOException {
        super(0x02, data);
        VarInputStream is = getInputStream();
        ID = is.readVarInt();
        uuid = is.readUUID();
        type = is.readVarInt();
        x = is.readDouble();
        y = is.readDouble();
        z = is.readDouble();
        pitch = is.readByte();
        yaw = is.readByte();
        headYaw = is.readByte();
        vX = is.readShort();
        vY = is.readShort();
        vZ = is.readShort();
    }

    public int getID() {
        return ID;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getType() {
        return type;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Position getPos() {
        return new Position(x, y, z);
    }

    public byte getPitch() {
        return pitch;
    }

    public byte getYaw() {
        return yaw;
    }

    public byte getHeadYaw() {
        return headYaw;
    }

    public short getvX() {
        return vX;
    }

    public short getvY() {
        return vY;
    }

    public short getvZ() {
        return vZ;
    }
}
