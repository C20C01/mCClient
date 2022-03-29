package io.github.c20c01.tool.proTool.Packets.general.In;

import io.github.c20c01.tool.proTool.Packets.Packet;
import io.github.c20c01.tool.proTool.VarInputStream;

import java.io.IOException;

public class LoginSuccessPacket extends Packet {

    private final String uuid;
    private final String username;

    public LoginSuccessPacket(byte[] data) throws IOException {
        super(0x02,data);
        VarInputStream is = getInputStream();
        uuid = is.readUUID().toString();
        username = is.readString();
    }

    /**
     * Get player's UUID
     *
     * @return players's UUID
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Get player's username
     *
     * @return player's username
     */
    public String getUsername() {
        return username;
    }

}
