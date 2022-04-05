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
    private boolean connected = false;
    private Thread packetReaderThread = null;
    public boolean compression = false;
    public boolean play = false;
    public ClientPacketSender sender = null;
    public String DisconnectReason = "";
    public Position playerPos;
    public EntityTool entityTool = null;

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
        entityTool = new EntityTool(this);
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
                    packetBuf.close();
                } catch (Exception e) {
                    try {
                        closeByE();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
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

    public boolean isConnected() {
        return connected;
    }

    public void setSecretKey(SecretKey secretKey) throws Exception {
        CipherInputStream cipherIS = new CipherInputStream(soc.getInputStream(), Tool.getCipher(2, secretKey));
        is = new VarInputStream(cipherIS);
    }

    private void closeByE() throws IOException {
        connected = false;
        Main.closeFromClient();
        Main.output("\n" + TimeTool.getTime() + "Connection closed!\n" + DisconnectReason, true);
        close();
    }

    public void closeFromMain() throws IOException {
        connected = false;
        Main.output("Closing...", true);
        DisconnectReason = "Disconnect by yourself.";
        close();
    }

    private void close() throws IOException {
        play = false;
        is.close();
        os.close();
        entityTool.close();
        if (packetReaderThread != null) packetReaderThread.interrupt();
        try {
            if (soc != null && !soc.isClosed()) soc.close();
        } catch (IOException ignored) {
        }
    }

    private boolean attacking = false;
    private Timer timer;

    public boolean isAttacking() {
        return attacking;
    }

    public void attack(boolean attack) {
        attacking = attack;
        if (attack) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    ArrayList<Entity> livingEList = entityTool.getLivingEList();
                    try {
                        for (Entity n : livingEList)
                            sender.Attack(n.getID());

                        Entity closestE = entityTool.getClosestE(livingEList);
                        if (closestE != null) {
                            lookAt(closestE.getPosition());
                            closestE = null;
                        }
                    } catch (IOException e) {
                        attack(false);
                        Main.output("Stopped attacking!", true);
                    }
                    livingEList.clear();
                }
            }, 1000, 200);
        } else timer.cancel();
    }

    public void lookAt(Position position) {
        float[] rotation = PositionTool.getRotation(position, playerPos);
        try {
            sender.PlayerRotation(rotation[0], rotation[1], true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dig() throws IOException {
        sender.PlayerDigging(PlayerDiggingPacket.Status.Started, new Position(1, 1, 1), PlayerDiggingPacket.Face.Bottom);
    }
}