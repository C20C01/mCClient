package io.github.c20c01.tool.proTool.Packets.general.In;

import io.github.c20c01.tool.proTool.Packets.Packet;
import io.github.c20c01.tool.proTool.VarInputStream;

import java.io.IOException;

public class TimeUpdatePacket extends Packet {

    private final long worldAge;
    private final long time;

    public TimeUpdatePacket(byte[] data) throws IOException {
        super(0x59, data);
        VarInputStream is = getInputStream();
        worldAge = is.readLong();
        time = is.readLong();
    }

    public long getWorldAge() {
        return worldAge;
    }

    public long getTime() {
        return time;
    }

}