package io.github.c20c01.tool.proTool;

import io.github.c20c01.Main;
import io.github.c20c01.tool.*;
import io.github.c20c01.tool.musicBoxTool.MusicBoxTool;
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
import java.util.HashSet;
import java.util.Set;
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
    public ClientPacketListener listener = null;
    public String DisconnectReason = "";
    public Position playerPos;
    public EntityTool entityTool = null;
    public MusicBoxTool musicBox = new MusicBoxTool(this);

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
        listener = new ClientPacketListener(this);
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
        Main.output(TimeTool.getTime() + "Closing...", true);
        DisconnectReason = "Disconnect by yourself.";
        close();
    }

    private void close() throws IOException {
        play = false;
        is.close();
        os.close();
        entityTool.close();
        attack(false);
        if (packetReaderThread != null) packetReaderThread.interrupt();
        try {
            if (soc != null && !soc.isClosed()) soc.close();
        } catch (IOException ignored) {
        }
    }

    private boolean attacking = false;
    private Timer attackTimer;
    private int attackTime = 200;
    private int attackID = -1;
    private TimerTask attackTask;

    public void setAttackTime(int time) {
        if (time < 200) {
            Main.output("Too fast, period has been set to 200ms.", true);
            attackTime = 200;
        } else attackTime = time;
    }

    public void setAttackID(int id) {
        if (id > 0) attackID = id;
    }

    public void setAttackTask() {
        if (attackID < 0) {
            Main.output("Entity ID not specified. Attacking any entity!", true);
            attackTask = new TimerTask() {
                Set<Integer> attackIDs;

                public void run() {
                    try {
                        if (entityTool.entityChanged()) {
                            attackIDs = new HashSet<>();
                            attackIDs.addAll(entityTool.getLivingEntityHashMap().keySet());
                        }
                        for (int id : attackIDs) {
                            sender.Attack(id);
                        }
                    } catch (Exception e) {
                        attack(false);
                        Main.output("Stopped attacking!", true);
                    }
                }
            };
        } else {
            Main.output("Entity ID specified: " + attackID, true);
            attackTask = new TimerTask() {
                public void run() {
                    try {
                        if (entityTool.entityChanged() && !entityTool.getLivingEntityHashMap().containsKey(attackID)) {
                            attack(false);
                            Main.output("Non-existent entity. Attack is over!", true);
                        } else sender.Attack(attackID);
                    } catch (Exception e) {
                        attack(false);
                        Main.output("Stopped attacking!", true);
                    }
                }
            };
        }
    }

    public void checkAttack() {
        Main.output(
                "============================" +
                        "| Attacking: " + attacking + "\n" +
                        "| Attacking period: " + attackTime + "\n" +
                        "| Attacking ID: " + ((attackID < 0) ? "null" : attackID) + "\n" +
                        "============================"
                , true);
    }

    public void attack(boolean attack) {
        attacking = attack;
        if (attack) {
            attackTimer = new Timer();
            setAttackTask();
            entityTool.resetEntityChanged();
            attackTimer.schedule(attackTask, 1000, attackTime);
        } else {
            if (attackTimer != null) attackTimer.cancel();
            attackID = -1;
        }
    }

    public void lookAt(Position position) {
        float[] rotation = PositionTool.getRotation(position, playerPos);
        try {
            sender.PlayerRotation(rotation[0], rotation[1], true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clickBlock(long position) throws IOException {
        sender.PlayerDigging(PlayerDiggingPacket.Status.Started, position, PlayerDiggingPacket.Face.Bottom);
        sender.PlayerDigging(PlayerDiggingPacket.Status.Cancelled, position, PlayerDiggingPacket.Face.Bottom);
    }

    public void changeHeldItem(int slot) throws IOException {
        sender.HeldItemChange(slot);
    }

    public void playerPosition(Position position, boolean onGround) throws IOException {
        sender.PlayerPosition(position.x(), position.y(), position.z(), onGround);
    }

}