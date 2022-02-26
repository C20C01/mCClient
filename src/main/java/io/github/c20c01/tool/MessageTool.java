package io.github.c20c01.tool;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class MessageTool {
    private static String input;
    private static String result;
    private static final List<String> strList = new ArrayList<>();

    public static String readString(String json) {
        input = json;
        JsonElement el = JsonParser.parseString(input);
        JsonObject root = el.getAsJsonObject();
        if (root.has("translate")) {
            switch (root.get("translate").getAsString()) {
                case "chat.type.text", "chat.type.announcement" -> read(root.get("with"));
                default -> unknownMessage();
            }
        } else {
            unknownMessage();
        }
        return result;
    }

    private static void unknownMessage() {
        result = ("Message: " + input);
    }

    private static void read(JsonElement ob) {
        if (ob.isJsonArray()) {
            readJsonArray(ob);
        } else if (ob.isJsonObject()) {
            readJson(ob);
        }
        if (strList.size() >= 2) {
            String name = strList.get(0);
            String message = strList.get(1);
            strList.clear();
            result = ("<" + name + "> " + message);
        } else {
            unknownMessage();
        }

    }

    private static void readJson(JsonElement el) {
        if (el.isJsonObject()) {
            JsonObject jo = el.getAsJsonObject();
            if (jo.has("text")) {
                strList.add(jo.get("text").getAsString());
            }
        } else if (el.isJsonArray()) {
            readJsonArray(el);
        } else {
            strList.add(el.getAsString());
        }
    }

    private static void readJsonArray(JsonElement ob) {
        for (JsonElement el : ob.getAsJsonArray()) {
            readJson(el);
        }
    }
}