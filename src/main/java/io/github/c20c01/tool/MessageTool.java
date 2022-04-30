package io.github.c20c01.tool;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.c20c01.tool.proTool.ClientPacketSender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessageTool {
    private final List<String> strList = new ArrayList<>();
    private final ClientPacketSender sender;
    private boolean readExtra = false;
    public boolean autoSender = false;
    private String input;
    private String result;
    private int size;

    public MessageTool(ClientPacketSender sender) {
        this.sender = sender;
    }

    public String readString(String json) {
        input = json;
        JsonObject root = JsonParser.parseString(input).getAsJsonObject();
        if (root.has("translate")) {
            JsonElement with = root.get("with");
            readEle(with);
            size = strList.size();
            try {
                switch (root.get("translate").getAsString()) {
                    case "chat.type.text", "chat.type.announcement" -> chat();
                    case "multiplayer.player.joined" -> playerJoin();
                    case "multiplayer.player.left" -> playerLeft();
                    default -> unknownMessage();
                }
            } catch (Exception e) {
                unknownMessage();
            }
        } else if (root.has("extra")) {
            JsonElement extra = root.get("extra");
            readExtra(extra);
            try {
                result = "* ";
                for (String str : strList) //noinspection StringConcatenationInLoop
                    result += str;
            } catch (Exception e) {
                unknownMessage();
            }
        } else
            unknownMessage();
        strList.clear();
        return result;
    }

    private void readExtra(JsonElement el) {
        readExtra = true;
        readEle(el);
        readExtra = false;
    }

    private void readEle(JsonElement el) {
        if (el.isJsonObject()) {
            JsonObject jo = el.getAsJsonObject();
            for (String key : jo.keySet()) {
                if (!(readExtra && !key.equals("text"))) readEle(jo.get(key));
            }
        } else if (el.isJsonArray()) {
            JsonArray ja = el.getAsJsonArray();
            for (JsonElement je : ja) {
                readEle(je);
            }
        } else {
            try {
                String str = el.getAsString();
                strList.add(str);
            } catch (Exception ignore) {
            }
        }
    }

    private void chat() {
        result = "<" + strList.get(size - 2) + "> " + strList.get(size - 1);
    }

    private void playerJoin() {
        String name = strList.get(size - 1);
        result = "* " + name + " joined the game.";
        if (autoSender)
            try {
                sender.ChatMessageOut(TimeTool.getTime() + "Good morning, my dear " + name + " !");
            } catch (IOException ignore) {
            }
    }

    private void playerLeft() {
        result = "* " + strList.get(size - 1) + " left the game.";
    }

    private void unknownMessage() {
        result = ("Message: " + input);
    }
}