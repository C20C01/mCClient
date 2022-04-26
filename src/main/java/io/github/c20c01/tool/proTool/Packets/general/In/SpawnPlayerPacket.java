package io.github.c20c01.tool.proTool.Packets.general.In;

import io.github.c20c01.tool.Position;
import io.github.c20c01.tool.proTool.Packets.Packet;
import io.github.c20c01.tool.proTool.VarInputStream;

import java.io.IOException;
import java.util.UUID;

public class SpawnPlayerPacket extends Packet {
    private final int ID;
    private final UUID uuid;
    private final double x, y, z;
    private final byte pitch, yaw;

    public SpawnPlayerPacket(byte[] data) throws IOException {
        super(0x04, data);
        VarInputStream is = getInputStream();
        ID = is.readVarInt();
        uuid = is.readUUID();
        x = is.readDouble();
        y = is.readDouble();
        z = is.readDouble();
        yaw = is.readByte();
        pitch = is.readByte();
        is.close();
        close();
    }

    public int getID() {
        return ID;
    }

    public UUID getUuid() {
        return uuid;
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
}
