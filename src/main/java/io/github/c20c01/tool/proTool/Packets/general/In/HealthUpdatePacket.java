package io.github.c20c01.tool.proTool.Packets.general.In;

import io.github.c20c01.tool.proTool.Packets.Packet;
import io.github.c20c01.tool.proTool.VarInputStream;

import java.io.IOException;

public class HealthUpdatePacket extends Packet {

    private final float health;
    private final int food;
    private final float saturation;

    public HealthUpdatePacket(byte[] data) throws IOException {
        super(0x52, data);
        VarInputStream is = getInputStream();
        health = is.readFloat();
        food = is.readVarInt();
        saturation = is.readFloat();
    }

    public float getHealth() {
        return health;
    }

    public int getFood() {
        return food;
    }

    public float getSaturation() {
        return saturation;
    }

}
