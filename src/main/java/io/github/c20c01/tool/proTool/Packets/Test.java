/*

***Test function, no need to read***

package io.github.c20c01.tool.proTool.Packets;

import io.github.c20c01.tool.proTool.Packets.general.Out.ClientLoginRequestPacket;
import io.github.c20c01.tool.proTool.Packets.general.Out.HandShakePacket;
import io.github.c20c01.tool.proTool.VarInputStream;
import io.github.c20c01.tool.proTool.VarOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.zip.Inflater;

public class Test {
    public static final String host = "0.0.0.0";
    public static final int port = 25565;
    public static final int protocol = 757;
    public static Socket soc = null;
    public static OutputStream os = null;
    public static VarInputStream is;
    private static Thread packetReaderThread = null;

    public static void main(String[] args) throws IOException {
        start();
        handShake(2);
        login("cc");

    }

    public static void start() throws IOException {
        Test.soc = new Socket();
        soc.connect(new InetSocketAddress(host, port));
        Test.os = soc.getOutputStream();
        is = new VarInputStream(soc.getInputStream());
    }

    public static void handShake(int state) throws IOException {

        HandShakePacket handshake = new HandShakePacket(protocol, host, port, state);
        os.write(handshake.getData(false));
        if (state == 1) {
            os.write(0x01);
            os.write(0x00);
            int len = is.readVarInt();
            if (len <= 0)
                throw new IOException("Invalid packet length received: " + Integer.toString(len));
            int id = is.readVarInt();
            if (id != 0x00)
                throw new IOException("Invalid packet ID received: 0x" + Integer.toHexString(id));

            String inf = is.readString();
            soc.close();
            System.out.println(inf);
        }

    }

    public static void login(String userName) throws IOException {
        ClientLoginRequestPacket clientLoginRequestPacket = new ClientLoginRequestPacket(userName);
        os.write(clientLoginRequestPacket.getData(false));

        int len = is.readVarInt();
        int id = is.readVarInt();
        System.out.println("Len:" + len + ",id:" + id);
        System.out.println(is.readVarInt());

        len = is.readVarInt();
        is.readVarInt();
        id = is.readVarInt();
        System.out.println("Len:" + len + ",id:" + id);
        System.out.println(is.readUUID());
        System.out.println(is.readString());

        packetReaderThread = new Thread(new Runnable() {

            private final Inflater inflater = new Inflater();

            @Override
            public void run() {
                try {
                    while (true) {
                        int len = is.readVarInt();
                        byte[] data = new byte[len];
                        is.readFully(data);

                        VarInputStream packetbuf = new VarInputStream(new ByteArrayInputStream(data));
                        final int id;
                        final byte[] packetData;

                        if (true) {
                            int dlen = packetbuf.readVarInt();
                            if (dlen == 0) {
                                id = packetbuf.readVarInt();
                                packetData = new byte[len - VarOutputStream.checkVarIntSize(dlen) - 1];
                                packetbuf.readFully(packetData);
                            } else {
                                byte[] toProcess = new byte[len - VarOutputStream.checkVarIntSize(dlen)];
                                packetbuf.readFully(toProcess);

                                byte[] inflated = new byte[dlen];
                                inflater.setInput(toProcess);
                                inflater.inflate(inflated);
                                inflater.reset();

                                packetbuf = new VarInputStream(new ByteArrayInputStream(inflated));
                                id = packetbuf.readVarInt();

                                packetData = new byte[dlen - 1];
                                packetbuf.readFully(packetData);

                            }
                        } else {
                            id = packetbuf.readVarInt();
                            packetData = new byte[len - 1];
                            packetbuf.readFully(packetData);
                        }

                        if (id != -1) {
                            //System.out.println(id);
                            if (id == 0x21) {
                                alive(packetData);
                            }
                        }
                    }
                } catch (Exception e) {
//
                }
            }
        });
        packetReaderThread.start();


    }

    public static void alive(byte[] packetData) throws IOException {
        VarOutputStream packetbuf = new VarOutputStream(new ByteArrayOutputStream());
        packetbuf.writeVarInt(0x0f);
        packetbuf.write(packetData);
        System.out.println("size:" + packetbuf.size());

        os.write(packetbuf.size()+1);
        os.write(0);
        os.write(0x0f);
        os.write(packetData);
        System.out.println("ok");
    }
}
*/