package io.github.c20c01.tool.proTool;

import io.github.c20c01.tool.proTool.Packets.encryption.Tool;
import io.github.c20c01.tool.proTool.Packets.general.Out.*;

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

    public void TeleportConfirm(int tpID) throws IOException {
        TeleportConfirmPacket Packet = new TeleportConfirmPacket(tpID);
        os.write(Packet.getData(client.compression));
    }

    public void PlayerRotation(float yaw,float pitch,boolean onGround) throws IOException {
        PlayerRotationPacket Packet=new PlayerRotationPacket(yaw, pitch, onGround);
        os.write(Packet.getData(client.compression));
    }

    public void Attack(int ID) throws IOException {
        InteractEntity(ID,1,false);
    }

    public void InteractEntity(int ID, int type, boolean sneaking) throws IOException {
        InteractEntityPacket Packet = new InteractEntityPacket(ID, type, sneaking);
        os.write(Packet.getData(client.compression));
    }

    public void Respawn() throws IOException {
        RespawnPacket Packet = new RespawnPacket();
        os.write(Packet.getData(client.compression));
    }

    public void KeepAliveOut(long aliveId) throws IOException {
        KeepAliveOutPacket Packet = new KeepAliveOutPacket(aliveId);
        os.write(Packet.getData(client.compression));
        //System.out.println("KeepAlivePacket Out!");
    }

    public void ChatMessageOut(String message) throws IOException {
        ChatMessageOutPacket Packet = new ChatMessageOutPacket(message);
        os.write(Packet.getData(client.compression));
    }

    public void EncryptionResponse(String serverId, byte[] publicKey, byte[] verifyToken) throws Exception {
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
        EncryptionResponsePacket Packet = new EncryptionResponsePacket(secretKeyByte, verifyTokenByte);
        os.write(Packet.getData(false));
    }
}
