package io.github.c20c01.tool.proTool.Packets.general.Out;

import io.github.c20c01.tool.proTool.Packets.Packet;

/**
 * Sent by client when it's trying to send a chat message
 *
 * @author Defective4
 */
public class ChatMessageOutPacket extends Packet {

    public ChatMessageOutPacket(String message) {
        super(0x03);
        putString(message);
    }

}
