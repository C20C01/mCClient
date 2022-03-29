package io.github.c20c01.tool.proTool.Packets.general.In;

import io.github.c20c01.tool.Position;
import io.github.c20c01.tool.proTool.Packets.Packet;
import io.github.c20c01.tool.proTool.VarInputStream;

import java.io.IOException;

public class EntityPositionPacket extends Packet {
    private final int entityID;
    private final short mx, my, mz;
    private final boolean onGround;

    public EntityPositionPacket(byte[] data) throws IOException {
        super(0x29, data);
        VarInputStream is = getInputStream();
        entityID = is.readVarInt();
        mx = is.readShort();
        my = is.readShort();
        mz = is.readShort();
        onGround = is.readBoolean();
    }

    public int getEntityID() {
        return entityID;
    }

    public Position getPos() {
        return new Position(mx, my, mz);
    }

    public boolean isOnGround() {
        return onGround;
    }
}
