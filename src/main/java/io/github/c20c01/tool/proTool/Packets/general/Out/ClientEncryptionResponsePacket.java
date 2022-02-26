package io.github.c20c01.tool.proTool.Packets.general.Out;

import io.github.c20c01.tool.proTool.Packets.Packet;

public class ClientEncryptionResponsePacket extends Packet {

    public ClientEncryptionResponsePacket(byte[] secretKey, byte[] verifyToken) {
        super(0x01);
        putVarInt(secretKey.length);
        putBytes(secretKey);
        putVarInt(verifyToken.length);
        putBytes(verifyToken);
    }
}
