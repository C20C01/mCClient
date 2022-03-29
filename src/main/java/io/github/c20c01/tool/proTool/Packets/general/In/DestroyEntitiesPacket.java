package io.github.c20c01.tool.proTool.Packets.general.In;

import io.github.c20c01.tool.proTool.Packets.Packet;
import io.github.c20c01.tool.proTool.VarInputStream;

import java.io.IOException;
public class DestroyEntitiesPacket extends Packet {
    private final int[] IDs;

    public DestroyEntitiesPacket(byte[] data) throws IOException {
        super(0x3A, data);
        VarInputStream is = getInputStream();
        int num = is.readVarInt();
        IDs = new int[num];
        for (int i = 0; i < num; i++) {
            IDs[i]=is.readVarInt();
        }
    }

    public int[] getIDs() {
        return IDs;
    }
}
