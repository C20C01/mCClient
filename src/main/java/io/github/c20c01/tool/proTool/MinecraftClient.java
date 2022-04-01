package io.github.c20c01.tool.proTool;

import io.github.c20c01.Main;
import io.github.c20c01.tool.*;
import io.github.c20c01.tool.proTool.Packets.encryption.Tool;
import io.github.c20c01.tool.proTool.Packets.general.Out.HandShakePacket;
import io.github.c20c01.tool.proTool.Packets.general.Out.LoginRequestPacket;
import io.github.c20c01.tool.proTool.Packets.general.Out.PlayerDiggingPacket;

import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

public class MinecraftClient {
    private final String host;
    private final int port;
    private final int protocol;
    private final Object lock = new Object();
    private VarInputStream is = null;
    private static Socket soc = null;
    private static OutputStream os = null;
    public boolean compression = false;
    private boolean connected = false;
    public boolean play = false;
    private Thread packetReaderThread = null;
    private ClientPacketSender sender = null;
    private final ArrayList<NearByEntity> nearbyEntities = new ArrayList<>();
    public String DisconnectReason = "";
    public Position playerPos;

    public MinecraftClient(String host, int port, int protocol) {
        this.host = host;
        this.port = port;
        this.protocol = protocol;
    }

    public boolean ping() {
        try {
            soc = new Socket();
            soc.connect(new InetSocketAddress(host, port));
            os = soc.getOutputStream();
            final VarInputStream is = new VarInputStream(soc.getInputStream());
            HandShakePacket handshake = new HandShakePacket(protocol, host, port, 1);
            os.write(handshake.getData(false));
            os.write(0x01);
            os.write(0x00);
            int len = is.readVarInt();
            if (len <= 0) return false;
            int id = is.readVarInt();
            if (id != 0) return false;
            String info = is.readString();
            soc.close();
            os.close();
            is.close();
            Main.output("Found the server...", true);
            ServerInfoTool.readString(info);
            return true;
        } catch (Exception ignored) {
            Main.output("Can't connect with the server! Please check that the server information is correct.", true);
        }
        return false;
    }

    public void connect(String username) throws IOException {
        soc = new Socket();
        soc.connect(new InetSocketAddress(host, port));
        connected = true;
        os = soc.getOutputStream();
        is = new VarInputStream(soc.getInputStream());
        sender = new ClientPacketSender(os, this);
        ClientPacketListener listener = new ClientPacketListener(this);
        HandShakePacket handshake = new HandShakePacket(protocol, host, port, 2);
        os.write(handshake.getData(false));
        LoginRequestPacket login = new LoginRequestPacket(username);
        os.write(login.getData(false));
        packetReaderThread = new Thread(new Runnable() {
            private final Inflater inflater = new Inflater();

            @Override
            public void run() {
                while (connected) try {
                    int len = is.readVarInt();
                    byte[] data = new byte[len];
                    is.readFully(data);
                    VarInputStream packetBuf = new VarInputStream(new ByteArrayInputStream(data));
                    final int id;
                    final byte[] packetData;
                    if (compression) {
                        int dataLen = packetBuf.readVarInt();
                        if (dataLen == 0) {
                            id = packetBuf.readVarInt();
                            packetData = new byte[len - 2];
                            packetBuf.readFully(packetData);
                        } else {
                            byte[] zip = new byte[len - VarOutputStream.checkVarIntSize(dataLen)];
                            packetBuf.readFully(zip);
                            byte[] unzip = new byte[dataLen];
                            inflater.setInput(zip);
                            inflater.inflate(unzip);
                            inflater.reset();
                            packetBuf = new VarInputStream(new ByteArrayInputStream(unzip));
                            id = packetBuf.readVarInt();
                            packetData = new byte[dataLen - 1];
                            packetBuf.readFully(packetData);
                        }
                    } else {
                        id = packetBuf.readVarInt();
                        packetData = new byte[len - 1];
                        packetBuf.readFully(packetData);
                    }
                    if (id != -1) listener.packetReceived(id, packetData);
                } catch (Exception e) {
                    closeByE();
                }
            }
        });
        packetReaderThread.start();
        synchronized (lock) {
            try {
                lock.wait(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void LoginSuccess() {
        Main.LoginSuccess();
    }

    public ClientPacketSender getSender() {
        return sender;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setSecretKey(SecretKey secretKey) throws Exception {
        CipherInputStream cipherIS = new CipherInputStream(soc.getInputStream(), Tool.getCipher(2, secretKey));
        is = new VarInputStream(cipherIS);
    }

    private void closeByE() {
        connected = false;
        Main.closeFromClient();
        Main.output("\n" + TimeTool.getTime() + "Connection closed!\n" + DisconnectReason, true);
        close();
    }

    public void closeFromMain() {
        connected = false;
        Main.output("Closing...", true);
        DisconnectReason = "Disconnect by yourself.";
        close();
    }

    private void close() {
        play = false;
        nearbyEntities.clear();
        if (packetReaderThread != null) packetReaderThread.interrupt();
        try {
            if (soc != null && !soc.isClosed()) soc.close();
        } catch (IOException ignored) {
        }
    }

    public void addEntities(NearByEntity n) {
        nearbyEntities.add(n);
    }

    public void destroyEntities(int[] IDs) {
        for (int id : IDs) nearbyEntities.removeIf(n -> n.getID() == id);
    }

    public void showEntities() {
        Main.output(TimeTool.getTime() + "Number of nearby entity: " + nearbyEntities.size());
        for (NearByEntity n : nearbyEntities) Main.output(n.toString());
        Main.output("");
    }

    public void entityMove(int entityID, Position pos) {
        for (NearByEntity n : nearbyEntities)
            if (n.getID() == entityID) {
                n.setPosition(PositionTool.getCurrent(pos, n.getPosition()));
                n.setDis(PositionTool.getDis(playerPos, n.getPosition()));
                break;
            }
    }

    public void entityTP(int entityID, Position pos) {
        for (NearByEntity n : nearbyEntities)
            if (n.getID() == entityID) {
                n.setPosition(pos);
                n.setDis(PositionTool.getDis(playerPos, n.getPosition()));
                break;
            }
    }

    private boolean attacking = false;
    private Timer timer;

    public boolean isAttacking() {
        return attacking;
    }

    public void attack(boolean attack) {
        final ArrayList<NearByEntity> nearbyLivingEntity = new ArrayList<>();
        attacking = attack;
        if (attack) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    for (NearByEntity n : nearbyEntities) if (n.isLiving()) nearbyLivingEntity.add(n);
                    for (NearByEntity n : nearbyLivingEntity)
                        if (n.getDis() < 8) {
                            try {
                                sender.Attack(n.getID());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    if (nearbyLivingEntity.size() > 0) {
                        NearByEntity nearestEntity = nearbyLivingEntity.get(0);
                        double dis = nearestEntity.getDis();
                        for (NearByEntity e : nearbyLivingEntity)
                            if (e.getDis() < dis) {
                                dis = e.getDis();
                                nearestEntity = e;
                            }

                        float[] rotation = PositionTool.getRotation(nearestEntity.getPosition(), playerPos);
                        try {
                            sender.PlayerRotation(rotation[0], rotation[1], true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        nearbyLivingEntity.clear();

                    }
                }
            }, 1000, 100);
        } else timer.cancel();
    }

    public void dig() throws IOException {
        sender.PlayerDigging(PlayerDiggingPacket.Status.Started, new Position(1, 1, 1), PlayerDiggingPacket.Face.Bottom);
    }
}