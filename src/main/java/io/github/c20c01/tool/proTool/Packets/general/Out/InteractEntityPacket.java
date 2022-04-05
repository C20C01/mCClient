package io.github.c20c01.tool.proTool.Packets.general.Out;

import io.github.c20c01.tool.proTool.Packets.Packet;

import java.io.IOException;

public class InteractEntityPacket extends Packet {
    public InteractEntityPacket(int id, Type type, boolean sneaking) {
        super(0x0D);
        putVarInt(id);
        putVarInt(type.ordinal());
        putBoolean(sneaking);
    }

    public InteractEntityPacket(int id, Type type, float tX, float tY, float tZ, Hand hand, boolean sneaking) throws IOException {
        super(0x0D);
        putVarInt(id);
        putVarInt(type.ordinal());
        if (type == Type.InteractAt) {
            putFloat(tX);
            putFloat(tY);
            putFloat(tZ);
            putVarInt(hand.ordinal());
        }
        if (type == Type.Interact) putVarInt(hand.ordinal());
        putBoolean(sneaking);
        close();
    }

    public enum Type {Interact, Attack, InteractAt}

    public enum Hand {MainHand, OffHand}
}
