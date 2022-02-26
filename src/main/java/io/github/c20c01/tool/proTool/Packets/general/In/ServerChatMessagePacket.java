package io.github.c20c01.tool.proTool.Packets.general.In;

import io.github.c20c01.tool.proTool.Packets.Packet;
import io.github.c20c01.tool.proTool.VarInputStream;

import java.io.IOException;

/**
 * Sent by server when client received a chat message
 *
 * @author Defective4
 */
public class ServerChatMessagePacket extends Packet {

    /**
     * Chat message position
     *
     * @author Defective4
     */
    public enum Position {
        /**
         * Message position is in chat box
         */
        CHAT,
        /**
         * System message
         */
        SYSTEM,
        /**
         * Message is displayed above hotbar
         */
        HOTBAR
    }

    private final String message;
    private final byte position;

    public ServerChatMessagePacket(byte[] data) throws IOException {
        super(0x0F, data);
        VarInputStream is = getInputStream();
        this.message = is.readString();
        this.position = is.readByte();
    }

    /**
     * Get JSON message
     *
     * @return raw JSON message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get message's position
     *
     * @return message's position
     */
    public Position getPosition() {
        switch (position) {
            case 1: {
                return Position.SYSTEM;
            }
            case 2: {
                return Position.HOTBAR;
            }
            case 0:
            default: {
                return Position.CHAT;
            }
        }
    }

}
