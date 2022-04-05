package io.github.c20c01.tool.proTool;

import io.github.c20c01.Main;
import io.github.c20c01.tool.Entity;
import io.github.c20c01.tool.MessageTool;
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
        sender = client.sender;
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

    private void LoginSuccess(byte[] data) throws Exception {
        LoginSuccessPacket packet = new LoginSuccessPacket(data);
        Main.output("Username: " + packet.getUsername(), true);
        Main.output("Uuid: " + packet.getUuid(), true);
        play = true;
        Main.output("\nSuccessfully joined the server!\n", true);
        client.LoginSuccess();
        if (encryption) sender.EnableEncryption();
        packet=null;
    }

    private void LoginSetCompression(byte[] data) throws IOException {
        LoginSetCompressionPacket packet = new LoginSetCompressionPacket(data);
        Main.output("Maximum size: " + packet.getThreshold(), true);
        client.compression = true;
        packet=null;
    }

    private void EncryptionRequest(byte[] data) throws Exception {
        EncryptionRequestPacket packet = new EncryptionRequestPacket(data);
        encryption = true;
        sender.EncryptionResponse(packet.getServerId(), packet.getPublicKey(), packet.getVerifyToken());
        packet=null;
    }

    private void respawn() {
        Main.output("\n" + TimeTool.getTime() + "Respawn!");
    }

    private void KeepAliveIn(byte[] data) throws IOException {
        KeepAliveInPacket packet = new KeepAliveInPacket(data);
        sender.KeepAliveOut(packet.getAliveId());
        packet=null;
    }

    private void ChatMessageIn(byte[] data) throws IOException {
        ChatMessageInPacket packet = new ChatMessageInPacket(data);
        Main.output(TimeTool.getTime() + MessageTool.readString(packet.getMessage()));
        packet=null;

    }

    private void PlayerDies() throws IOException {
        sender.Respawn();
    }

    private void TimeUpdate(byte[] data) throws IOException {
        TimeUpdatePacket packet = new TimeUpdatePacket(data); /*Main.output("Time: " + packet.getTime());*/
        packet=null;
    }

    private static final DecimalFormat sdf = new DecimalFormat("##.#");

    private void HealthUpdate(byte[] data) throws IOException {
        HealthUpdatePacket packet = new HealthUpdatePacket(data);
        Main.output(TimeTool.getTime() + "Food: " + packet.getFood() + ", Health: " + sdf.format(packet.getHealth()));
        packet=null;
    }

    private void SpawnEntity(byte[] data) throws IOException {
        SpawnEntityPacket packet = new SpawnEntityPacket(data);
        client.entityTool.addEntity(new Entity(packet.getID(), packet.getUuid(), packet.getType(),
                packet.getPos(), false, PositionTool.getDis(client.playerPos, packet.getPos())));
        packet=null;
    }

    private void SpawnLivingEntity(byte[] data) throws IOException {
        SpawnLivingEntityPacket packet = new SpawnLivingEntityPacket(data);
        client.entityTool.addEntity(new Entity(packet.getID(), packet.getUuid(), packet.getType(),
                packet.getPos(), true, PositionTool.getDis(client.playerPos, packet.getPos())));
        packet=null;
    }

    private void DestroyEntities(byte[] data) throws IOException {
        DestroyEntitiesPacket packet = new DestroyEntitiesPacket(data);
        client.entityTool.destroyEntity(packet.getIDs());
        packet=null;
    }

    private void Disconnect(byte[] data) throws IOException {
        DisconnectPacket packet = new DisconnectPacket(data);
        client.DisconnectReason = packet.getReason();
        packet=null;
    }

    private void PlayerPositionAndLook(byte[] data) throws IOException {
        PlayerPositionAndLookPacket packet = new PlayerPositionAndLookPacket(data);
        Main.output(packet.getPos().toString());
        client.playerPos = packet.getPos();
        sender.TeleportConfirm(packet.getTpID());
        packet=null;
    }

    private void EntityPosition(byte[] data) throws IOException {
        EntityPositionPacket packet = new EntityPositionPacket(data);
        client.entityTool.entityMove(packet.getEntityID(), packet.getPos());
        packet=null;
    }

    private void EntityPositionAndRotation(byte[] data) throws IOException {
        EntityPositionAndRotationPacket packet = new EntityPositionAndRotationPacket(data);
        client.entityTool.entityMove(packet.getEntityID(), packet.getPos());
        packet=null;
    }

    private void EntityTeleport(byte[] data) throws IOException {
        EntityTeleportPacket packet = new EntityTeleportPacket(data);
        client.entityTool.entityTP(packet.getEntityID(), packet.getPos());
        packet=null;
    }
}