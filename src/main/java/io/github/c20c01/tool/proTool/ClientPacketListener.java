package io.github.c20c01.tool.proTool;

import io.github.c20c01.tool.MessageTool;
import io.github.c20c01.tool.NearByEntity;
import io.github.c20c01.tool.PositionTool;
import io.github.c20c01.tool.TimeTool;
import io.github.c20c01.tool.proTool.Packets.general.In.*;

import java.io.IOException;
import java.text.DecimalFormat;

public class ClientPacketListener {
    private final MinecraftClient client;
    private final ClientPacketSender sender;
    private boolean play = false;
    private boolean encryption = false;

    public ClientPacketListener(MinecraftClient client) {
        this.client = client;
        sender = client.getSender();
    }

    public void packetReceived(final int id, byte[] data) throws Exception {
        if (!play) switch (id) {
            case 0x02 -> LoginSuccess(data);
            case 0x03 -> LoginSetCompression(data);
            case 0x01 -> EncryptionRequest(data);
        }
        else switch (id) {
            case 0x3d -> respawn();
            case 0x21 -> KeepAliveIn(data);
            case 0x0f -> ChatMessageIn(data);
            case 0x35 -> PlayerDies();
            case 0x59 -> TimeUpdate(data);
            case 0x52 -> HealthUpdate(data);
            case 0x00 -> SpawnEntity(data);
            case 0x02 -> SpawnLivingEntity(data);
            case 0x3A -> DestroyEntities(data);
            case 0x1A -> Disconnect(data);
            case 0x38 -> PlayerPositionAndLook(data);
            case 0x29 -> EntityPosition(data);
            case 0x2A -> EntityPositionAndRotation(data);
            case 0x62 -> EntityTeleport(data);
        }
    }

    private void EntityTeleport(byte[] data) throws IOException {
        EntityTeleportPacket Packet=new EntityTeleportPacket(data);
        client.entityTP(Packet.getEntityID(), Packet.getPos());
    }

    private void EntityPositionAndRotation(byte[] data) throws IOException {
        EntityPositionAndRotationPacket Packet=new EntityPositionAndRotationPacket(data);
        client.entityMove(Packet.getEntityID(), Packet.getPos());
    }

    private void EntityPosition(byte[] data) throws IOException {
        EntityPositionPacket Packet = new EntityPositionPacket(data);
        client.entityMove(Packet.getEntityID(), Packet.getPos());
    }

    private void PlayerPositionAndLook(byte[] data) throws IOException {
        PlayerPositionAndLookPacket Packet = new PlayerPositionAndLookPacket(data);
        System.out.println(Packet.getPos());
        client.playerPos = Packet.getPos();
        sender.TeleportConfirm(Packet.getTpID());
    }

    private void Disconnect(byte[] data) throws IOException {
        DisconnectPacket Packet = new DisconnectPacket(data);
        client.DisconnectReason = Packet.getReason();
    }

    private void DestroyEntities(byte[] data) throws IOException {
        DestroyEntitiesPacket Packet = new DestroyEntitiesPacket(data);
        client.destroyEntities(Packet.getIDs());
    }

    private void SpawnLivingEntity(byte[] data) throws IOException {
        SpawnLivingEntityPacket Packet = new SpawnLivingEntityPacket(data);
        client.addEntities(new NearByEntity(Packet.getID(), Packet.getUuid(), Packet.getType(),
                Packet.getPos(), true, PositionTool.getDis(client.playerPos, Packet.getPos())));
    }

    private void SpawnEntity(byte[] data) throws IOException {
        SpawnEntityPacket Packet = new SpawnEntityPacket(data);
        client.addEntities(new NearByEntity(Packet.getID(), Packet.getUuid(), Packet.getType(),
                Packet.getPos(), false, PositionTool.getDis(client.playerPos, Packet.getPos())));
    }

    private void respawn() {
        System.out.println();
        TimeTool.printTime();
        System.out.println("Respawn!");
    }

    private void EncryptionRequest(byte[] data) throws Exception {
        EncryptionRequestPacket Packet = new EncryptionRequestPacket(data);
        encryption = true;
        sender.EncryptionResponse(Packet.getServerId(), Packet.getPublicKey(), Packet.getVerifyToken());
    }

    private static final DecimalFormat sdf = new DecimalFormat("##.#");

    private void HealthUpdate(byte[] data) throws IOException {
        HealthUpdatePacket Packet = new HealthUpdatePacket(data);
        TimeTool.printTime();
        System.out.println("Food: " + Packet.getFood() + ", Health: " + sdf.format(Packet.getHealth()));
    }

    private void TimeUpdate(byte[] data) throws IOException {
        TimeUpdatePacket Packet = new TimeUpdatePacket(data); /*System.out.println("Time: " + Packet.getTime());*/
    }

    private void PlayerDies() throws IOException {
        sender.Respawn();
    }

    private void ChatMessageIn(byte[] data) throws IOException {
        ChatMessageInPacket Packet = new ChatMessageInPacket(data);
        TimeTool.printTime();
        System.out.println(MessageTool.readString(Packet.getMessage()));
    }

    private void KeepAliveIn(byte[] data) throws IOException {
        KeepAliveInPacket Packet = new KeepAliveInPacket(data);
        sender.KeepAliveOut(Packet.getAliveId());
    }

    private void LoginSuccess(byte[] data) throws Exception {
        LoginSuccessPacket Packet = new LoginSuccessPacket(data);
        System.out.println("Username: " + Packet.getUsername());
        System.out.println("Uuid: " + Packet.getUuid());
        play = true;
        System.out.println("\nSuccessfully joined the server!\n");
        if (encryption) sender.EnableEncryption();
    }

    private void LoginSetCompression(byte[] data) throws IOException {
        LoginSetCompressionPacket Packet = new LoginSetCompressionPacket(data);
        System.out.println("Maximum size: " + Packet.getThreshold());
        client.compression = true;
    }
}