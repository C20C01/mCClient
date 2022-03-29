package io.github.c20c01.tool.proTool.Packets.general.Out;

import io.github.c20c01.tool.proTool.Packets.Packet;

import java.io.IOException;

public class InteractEntityPacket extends Packet {
    public InteractEntityPacket(int id, int type, boolean sneaking){
        super(0x0D);
        putVarInt(id);
        putVarInt(type);
        putBoolean(sneaking);
    }

    public InteractEntityPacket(int id, int type, float tX, float tY, float tZ, int hand, boolean sneaking){
        super(0x0D);
        putVarInt(id);
        putVarInt(type);
        if (type == 2) {
            putFloat(tX);
            putFloat(tY);
            putFloat(tZ);
            putVarInt(hand);
        }
        if (type == 0) putVarInt(hand);
    }

}
