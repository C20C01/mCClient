package io.github.c20c01.tool.proTool.Packets.general.In;

import io.github.c20c01.tool.proTool.Packets.Packet;
import io.github.c20c01.tool.proTool.VarInputStream;

import java.io.IOException;

public class DisconnectPacket extends Packet {
    private final String reason;

    public DisconnectPacket(byte[] data) throws IOException {
        super(0x1A, data);
        VarInputStream is = getInputStream();
        reason = is.readString();
    }

    public String getReason() {
        return reason;
    }
}
