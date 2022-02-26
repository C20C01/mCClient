package io.github.c20c01.tool.proTool;

import io.github.c20c01.tool.MessageTool;
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
        if (!play)
            switch (id) {
                case 0x02 -> serverLoginSuccessPacket(data);
                case 0x03 -> serverLoginSetCompressionPacket(data);
                case 0x01 -> serverEncryptionRequestPacket(data);
            }
        else
            switch (id) {
                case 0x3d -> respawn();
                case 0x21 -> serverKeepAlivePacket(data);
                case 0x0f -> serverChatMessagePacket(data);
                case 0x35 -> serverPlayerDiesPacket();
                case 0x59 -> serverTimeUpdatePacket(data);
                case 0x52 -> serverUpdateHealthPacket(data);
            }
    }

    private void respawn() {
        System.out.println();
        TimeTool.printTime();
        System.out.println("Respawn!");
    }

    private void serverEncryptionRequestPacket(byte[] data) throws Exception {
        ServerEncryptionRequestPacket Packet = new ServerEncryptionRequestPacket(data);
        encryption = true;
        sender.clientEncryptionResponsePacket(Packet.getServerId(), Packet.getPublicKey(), Packet.getVerifyToken());
    }

    private static final DecimalFormat sdf = new DecimalFormat("##.#");

    private void serverUpdateHealthPacket(byte[] data) throws IOException {
        ServerUpdateHealthPacket Packet = new ServerUpdateHealthPacket(data);
        TimeTool.printTime();
        System.out.println("Food: " + Packet.getFood() + ", Health: " + sdf.format(Packet.getHealth()));
    }

    private void serverTimeUpdatePacket(byte[] data) throws IOException {
        ServerTimeUpdatePacket Packet = new ServerTimeUpdatePacket(data);
        //System.out.println("Time: " + Packet.getTime());
    }

    private void serverPlayerDiesPacket() throws IOException {
        sender.clientRespawnPacket();
    }

    private void serverChatMessagePacket(byte[] data) throws IOException {
        ServerChatMessagePacket Packet = new ServerChatMessagePacket(data);
        TimeTool.printTime();
        //System.out.println("Message: " + Packet.getMessage());
        System.out.println(MessageTool.readString(Packet.getMessage()));
    }

    private void serverKeepAlivePacket(byte[] data) throws IOException {
        ServerKeepAlivePacket Packet = new ServerKeepAlivePacket(data);
        sender.clientKeepAlivePacket(Packet.getAliveId());
    }

    private void serverLoginSuccessPacket(byte[] data) throws Exception {
        ServerLoginSuccessPacket Packet = new ServerLoginSuccessPacket(data);
        System.out.println("Username: " + Packet.getUsername());
        System.out.println("Uuid: " + Packet.getUuid());
        play = true;
        System.out.println("\nSuccessfully joined the server!\n");
        if (encryption) sender.EnableEncryption();
    }

    private void serverLoginSetCompressionPacket(byte[] data) throws IOException {
        ServerLoginSetCompressionPacket Packet = new ServerLoginSetCompressionPacket(data);
        System.out.println("Maximum size: " + Packet.getThreshold());
        client.compression = true;
    }

}
