package io.github.c20c01.tool.proTool.Packets.general.Out;

import io.github.c20c01.tool.proTool.Packets.Packet;

import java.io.IOException;

public class EncryptionResponsePacket extends Packet {

    public EncryptionResponsePacket(byte[] secretKey, byte[] verifyToken) throws IOException {
        super(0x01);
        putVarInt(secretKey.length);
        putBytes(secretKey);
        putVarInt(verifyToken.length);
        putBytes(verifyToken);
        close();
    }
}
