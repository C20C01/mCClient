package io.github.c20c01.tool;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ServerInfoTool {
    public static void readString(String json) {
        System.out.println("====================Info====================");
        try {
            JsonElement el = JsonParser.parseString(json);
            JsonObject root = el.getAsJsonObject();
            System.out.println("description: " + root.get("description").getAsJsonObject().get("text"));
            JsonObject players = root.get("players").getAsJsonObject();
            System.out.println("players: " + players.get("online") + "/" + players.get("max"));
            if (players.get("online").getAsInt() > 0)
                readPlayersArray(players.get("sample").getAsJsonArray());
            JsonObject version = root.get("version").getAsJsonObject();
            System.out.println("version: " + version.get("name") + "\nprotocol: " + version.get("protocol"));
        } catch (Exception e) {
            System.out.println(json);
        }
        System.out.println("============================================");
    }

    private static void readPlayersArray(JsonArray jsonArray) {
        System.out.print("sample: ");
        for (JsonElement j : jsonArray) {
            System.out.print(j.getAsJsonObject().get("name") + " ");
        }
        System.out.println();
    }
}
