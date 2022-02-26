package io.github.c20c01.tool.proTool;

import io.github.c20c01.tool.proTool.Packets.encryption.Tool;
import io.github.c20c01.tool.proTool.Packets.general.Out.ClientChatMessagePacket;
import io.github.c20c01.tool.proTool.Packets.general.Out.ClientEncryptionResponsePacket;
import io.github.c20c01.tool.proTool.Packets.general.Out.ClientKeepAlivePacket;
import io.github.c20c01.tool.proTool.Packets.general.Out.ClientRespawnPacket;

import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.PublicKey;

public class ClientPacketSender {
    private OutputStream os;
    private final MinecraftClient client;
    private boolean needEncryption = false;
    private SecretKey secretkey;

    public ClientPacketSender(OutputStream os, MinecraftClient client) {
        this.os = os;
        this.client = client;
    }

    public void EnableEncryption() throws Exception {
        if (needEncryption) os = new CipherOutputStream(os, Tool.getCipher(1, secretkey));
    }

    public void clientRespawnPacket() throws IOException {
        ClientRespawnPacket Packet = new ClientRespawnPacket();
        os.write(Packet.getData(client.compression));
    }

    public void clientKeepAlivePacket(long aliveId) throws IOException {
        ClientKeepAlivePacket Packet = new ClientKeepAlivePacket(aliveId);
        os.write(Packet.getData(client.compression));
        //System.out.println("KeepAlivePacket Out!");
    }

    public void clientChatMessagePacket(String message) throws IOException {
        ClientChatMessagePacket Packet = new ClientChatMessagePacket(message);
        os.write(Packet.getData(client.compression));
    }

    public void clientEncryptionResponsePacket(String serverId, byte[] publicKey, byte[] verifyToken) throws Exception {
        secretkey = Tool.generateSecretKey();
        client.setSecretKey(secretkey);
        needEncryption = true;
        PublicKey publickey = Tool.byteToPublicKey(publicKey);
        assert publickey != null;
        assert secretkey != null;
        String serverID = (new BigInteger(Tool.digestData(serverId, publickey, secretkey))).toString(16);
        Tool.login(serverID);
        byte[] secretKeyByte = Tool.encryptUsingKey(publickey, secretkey.getEncoded());
        byte[] verifyTokenByte = Tool.encryptUsingKey(publickey, verifyToken);
        ClientEncryptionResponsePacket Packet = new ClientEncryptionResponsePacket(secretKeyByte, verifyTokenByte);
        os.write(Packet.getData(false));
    }
}
