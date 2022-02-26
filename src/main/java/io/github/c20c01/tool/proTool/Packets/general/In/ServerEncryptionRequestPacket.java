package io.github.c20c01.tool.proTool.Packets.general.In;

import io.github.c20c01.tool.proTool.Packets.Packet;
import io.github.c20c01.tool.proTool.VarInputStream;

import java.io.IOException;

public class ServerEncryptionRequestPacket extends Packet {

    private final String serverId;
    private final byte[] publicKey;
    private final byte[] verifyToken;

    public ServerEncryptionRequestPacket(byte[] data) throws IOException {
        super(0x01, data);
        VarInputStream is = getInputStream();
        serverId = is.readString();
        int publicKeyLength = is.readVarInt();
        publicKey = is.readNBytes(publicKeyLength);
        int verifyTokenLength = is.readVarInt();
        verifyToken = is.readNBytes(verifyTokenLength);
    }

    public String getServerId() {
        return serverId;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public byte[] getVerifyToken() {
        return verifyToken;
    }

}
