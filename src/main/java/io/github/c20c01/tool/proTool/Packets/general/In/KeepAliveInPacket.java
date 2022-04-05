package io.github.c20c01.tool.proTool.Packets.general.In;

import io.github.c20c01.tool.proTool.Packets.Packet;
import io.github.c20c01.tool.proTool.VarInputStream;

import java.io.IOException;

public class KeepAliveInPacket extends Packet {

    private final long aliveId;

    public KeepAliveInPacket(byte[] data) throws IOException {
        super(0x21, data);
        VarInputStream is = getInputStream();
        this.aliveId = is.readLong();
        is.close();
        close();
    }

    public long getAliveId() {
        return aliveId;
    }

}
