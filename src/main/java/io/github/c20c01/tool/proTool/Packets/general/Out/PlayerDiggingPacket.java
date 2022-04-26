package io.github.c20c01.tool.proTool.Packets.general.Out;

import io.github.c20c01.tool.Position;
import io.github.c20c01.tool.PositionTool;
import io.github.c20c01.tool.proTool.Packets.Packet;

import java.io.IOException;

public class PlayerDiggingPacket extends Packet {
    public PlayerDiggingPacket(Status status, Position position, Face face) throws IOException {
        super(0x1A);
        putVarInt(status.ordinal());
        putLong(PositionTool.getPosition(position));
        putByte(face.ordinal());
        close();
    }

    public PlayerDiggingPacket(Status status, Long position, Face face) throws IOException {
        super(0x1A);
        putVarInt(status.ordinal());
        putLong(position);
        putByte(face.ordinal());
        close();
    }

    public enum Status {Started, Cancelled, Finished, DropItemStack, DropItem, ShootOrEat, SwapItemInHand}

    public enum Face {Bottom, Top, North, South, West, East}
}
