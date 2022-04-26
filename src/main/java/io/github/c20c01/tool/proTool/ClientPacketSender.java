package io.github.c20c01.tool.proTool;

import io.github.c20c01.tool.Position;
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
        EncryptionResponsePacket packet = new EncryptionResponsePacket(secretKeyByte, verifyTokenByte);
        os.write(packet.getData(false));
    }

    public void EnableEncryption() throws Exception {
        if (needEncryption) os = new CipherOutputStream(os, Tool.getCipher(1, secretkey));
    }

    public void KeepAliveOut(long aliveId) throws IOException {
        KeepAliveOutPacket packet = new KeepAliveOutPacket(aliveId);
        os.write(packet.getData(client.compression));
        System.gc();
        //Main.output("KeepAlivePacket Out!");
    }

    public void Respawn() throws IOException {
        RespawnPacket packet = new RespawnPacket();
        os.write(packet.getData(client.compression));
    }

    public void ChatMessageOut(String message) throws IOException {
        if (message.length() > 256) {
            while (message.length() > 256) {
                ChatMessageOutSend(message.substring(0, 256));
                message = message.substring(256);
            }
        }
        ChatMessageOutSend(message);
    }

    private void ChatMessageOutSend(String message) throws IOException {
        ChatMessageOutPacket packet = new ChatMessageOutPacket(message);
        os.write(packet.getData(client.compression));
    }

    public void TeleportConfirm(int tpID) throws IOException {
        TeleportConfirmPacket packet = new TeleportConfirmPacket(tpID);
        os.write(packet.getData(client.compression));
    }

    public void PlayerRotation(float yaw, float pitch, boolean onGround) throws IOException {
        PlayerRotationPacket packet = new PlayerRotationPacket(yaw, pitch, onGround);
        os.write(packet.getData(client.compression));
    }

    public void Attack(int ID) throws IOException {
        InteractEntity(ID, InteractEntityPacket.Type.Attack, false);
    }

    public void InteractEntity(int ID, InteractEntityPacket.Type type, boolean sneaking) throws IOException {
        InteractEntityPacket packet = new InteractEntityPacket(ID, type, sneaking);
        os.write(packet.getData(client.compression));
    }

    public void PlayerDigging(PlayerDiggingPacket.Status status, Position position, PlayerDiggingPacket.Face face) throws IOException {
        PlayerDiggingPacket packet = new PlayerDiggingPacket(status, position, face);
        os.write(packet.getData(client.compression));
    }

    public void PlayerDigging(PlayerDiggingPacket.Status status, long position, PlayerDiggingPacket.Face face) throws IOException {
        PlayerDiggingPacket packet = new PlayerDiggingPacket(status, position, face);
        os.write(packet.getData(client.compression));
    }

    public void HeldItemChange(int slot) throws IOException {
        HeldItemChangePacket packet = new HeldItemChangePacket(slot);
        os.write(packet.getData(client.compression));
    }

    public void PlayerPosition(double x, double feetY, double z, boolean onGround) throws IOException {
        PlayerPositionPacket packet = new PlayerPositionPacket(x, feetY, z, onGround);
        os.write(packet.getData(client.compression));
    }
}
