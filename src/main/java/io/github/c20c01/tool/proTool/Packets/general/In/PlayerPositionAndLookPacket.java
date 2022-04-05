package io.github.c20c01.tool.proTool.Packets.general.In;

import io.github.c20c01.tool.Position;
import io.github.c20c01.tool.proTool.Packets.Packet;
import io.github.c20c01.tool.proTool.VarInputStream;

import java.io.IOException;

public class PlayerPositionAndLookPacket extends Packet {
    private final double x, y, z;
    private final float yaw, pitch;
    private final byte flags;
    private final int tpID;
    private final boolean dismountVehicle;

    public PlayerPositionAndLookPacket(byte[] data) throws IOException {
        super(0x38, data);
        VarInputStream is = getInputStream();
        x = is.readDouble();
        y = is.readDouble();
        z = is.readDouble();
        yaw = is.readFloat();
        pitch = is.readFloat();
        flags = is.readByte();
        tpID = is.readVarInt();
        dismountVehicle = is.readBoolean();
        is.close();
        close();
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

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public byte getFlags() {
        return flags;
    }

    public int getTpID() {
        return tpID;
    }

    public boolean isDismountVehicle() {
        return dismountVehicle;
    }
}
