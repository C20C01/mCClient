package io.github.c20c01;

import com.google.gson.Gson;
import io.github.c20c01.tool.proTool.MinecraftClient;
import io.github.c20c01.tool.proTool.Packets.encryption.Tool;

import java.io.*;
import java.util.Scanner;

public class Main {
    private static final Gson gson = new Gson();
    private static MinecraftClient client;
    private static String host = "0.0.0.0";
    private static String userName = null;
    private static int port = 25565;
    private static int protocol = 757;
    private static boolean commandMode = true;
    private static boolean setting = false;
    private static boolean offlineFirst = true;
    private static int settingId = -1;
    private static File settingFile;
    private static CommonSetting cs = new CommonSetting();
    private static boolean play = false;

    public static void main(String[] args) throws IOException {
        welcome();
        first();
        input();
    }

    private static void first() throws IOException {
        settingFile = new File(".//mCClient.json");
        if (!settingFile.createNewFile()) getSetting();
        else {
            prLn("First time use? Read the instructions below to get started.");
            help();
            writeSettingJson();
        }

    }

    private static void writeSettingJson() throws IOException {
        cs.host = host;
        cs.port = port;
        cs.protocol = protocol;
        cs.username = userName;
        cs.uuid = Tool.getUuid();
        cs.token = Tool.getAccessToken();
        Writer out = new FileWriter(settingFile);
        gson.toJson(cs, out);
        out.flush();
        out.close();
    }

    private static void getSetting() {
        try {
            Reader in = new FileReader(settingFile);
            cs = gson.fromJson(in, CommonSetting.class);
            in.close();
            if (cs != null) {
                host = cs.host;
                port = cs.port;
                protocol = cs.protocol;
                userName = cs.username;
                Tool.setUuid(cs.uuid);
                Tool.setAccessToken(cs.token);
                prLn("Last setting has been loaded.");
                check();
            } else {
                cs = new CommonSetting();
                prLn("Failed to load last setting.");
                writeSettingJson();
            }
        } catch (Exception ignored) {
        }
    }

    private static void prLn(String s) {
        System.out.println(s);
    }

    private static void pr(String s) {
        System.out.print(s);
    }

    private static void runMinecraftClient() throws IOException {
        client = new MinecraftClient(host, port, protocol);
        if (client.ping()) {
            commandMode(false);
            client.connect(userName);
            play = true;
        }
    }

    private static void commandMode() {
        commandMode(!commandMode);
    }

    private static void commandMode(boolean b) {
        if (b) {
            commandMode = true;
            prLn("\nEnter the command mode.\n");
        } else {
            commandMode = false;
            prLn("\nExit the command mode.\n");
        }
    }

    private static void command(String input) throws IOException {
        if (setting)
            setting(input);
        else
            switch (input) {
                case "set" -> set();
                case "ping" -> ping();
                case "join" -> join();
                case "help" -> help();
                case "check" -> check();
                case "close" -> close();
                default -> prLn("Unknown command! Type \"help\" to get help.");
            }
    }

    private static void check() {
        prLn("===========================================\n" +
                "| host: " + host + "\n" +
                "| port: " + port + "\n" +
                "| protocol: " + protocol + "\n" +
                "|------------------------------------------\n" +
                "| username: " + userName + "\n" +
                "| uuid: " + Tool.getUuid() + "\n" +
                "| token(top 20th): " + getSomeToken() + "\n" +
                "==========================================="
        );
    }

    private static String getSomeToken() {
        String s = null;
        if (Tool.getAccessToken() != null)
            s = Tool.getAccessToken().substring(0, 20);
        return s;
    }

    private static void setting(String input) throws IOException {
        if (settingId == -1)
            switch (input) {
                case "host" -> {
                    settingId = 0;
                    pr("host: ");
                }
                case "port" -> {
                    settingId = 1;
                    pr("port: ");
                }
                case "protocol" -> {
                    settingId = 2;
                    pr("protocol: ");
                }
                case "username" -> {
                    settingId = 3;
                    pr("username: ");
                }
                case "uuid" -> {
                    settingId = 4;
                    pr("uuid: ");
                }
                case "token" -> {
                    settingId = 5;
                    pr("token: ");
                }
                case "cancel" -> {
                    setting = false;
                    prLn("Cancel.");
                }
                default -> prLn("Unknown command! Please enter again.");
            }
        else
            setSomething(input);
    }

    private static void setSomething(String input) throws IOException {
        switch (settingId) {
            case 0 -> host = input;
            case 1 -> port = Integer.parseInt(input);
            case 2 -> protocol = Integer.parseInt(input);
            case 3 -> userName = input;
            case 4 -> Tool.setUuid(input);
            case 5 -> Tool.setAccessToken(input);
        }
        prLn("Complete.");
        writeSettingJson();
        setting = false;
        settingId = -1;
    }

    public static void closeFromClient() {
        play = false;
        commandMode(true);
    }

    private static void close() {
        if (play) {
            client.closeByMain();
            play = false;
        } else {
            prLn("==================================Close==================================");
            System.exit(0);
        }
    }

    private static void ping() {
        client = new MinecraftClient(host, port, protocol);
        client.ping();
    }

    private static void join() throws IOException {
        if (host == null) {
            prLn("Missing host!");
            check();
        } else if (userName == null) {
            prLn("Missing username!");
            check();
        } else if (Tool.getUuid() == null || Tool.getAccessToken() == null) {
            if (offlineFirst) {
                prLn("You can only join the Offline Mode Server! Because of the missing uuid or token.");
                prLn("Please enter \"join\" again to confirm it and try to join the server.");
                offlineFirst = false;
            } else {
                offlineFirst = true;
                runMinecraftClient();
            }
        } else {
            offlineFirst = true;
            runMinecraftClient();
        }

    }

    private static void help() {
        prLn("""
                ====================Help===================
                | help <---Get help.
                | set <----Setting parameters.
                | check <--Show your current settings.
                | ping <---Get server information.
                | join <---Join the server.
                | close <--Exit server or mCClient.
                | /cc <----Enter or Exit the command mode.
                ==========================================="""
        );
    }

    private static void welcome() {
        prLn("""
                ........................................................................
                ................______....______...__..__........................__.....
                .............../......\\../......\\.|..\\|..\\......................|..\\....
                .______.____..|..$$$$$$\\|..$$$$$$\\|.$$.\\$$..______..._______..._|.$$_...
                |......\\....\\.|.$$...\\$$|.$$...\\$$|.$$|..\\./......\\.|.......\\.|...$$.\\..
                |.$$$$$$\\$$$$\\|.$$......|.$$......|.$$|.$$|..$$$$$$\\|.$$$$$$$\\.\\$$$$$$..
                |.$$.|.$$.|.$$|.$$...__.|.$$...__.|.$$|.$$|.$$....$$|.$$..|.$$..|.$$.__.
                |.$$.|.$$.|.$$|.$$__/..\\|.$$__/..\\|.$$|.$$|.$$$$$$$$|.$$..|.$$..|.$$|..\\
                |.$$.|.$$.|.$$.\\$$....$$.\\$$....$$|.$$|.$$.\\$$.....\\|.$$..|.$$...\\$$..$$
                .\\$$..\\$$..\\$$..\\$$$$$$...\\$$$$$$..\\$$.\\$$..\\$$$$$$$.\\$$...\\$$....\\$$$$.
                ........................................................................
                """
        );
        prLn("Welcome to mCClient !");
        prLn("Find more on: https://c20c01.github.io/\n");
    }

    private static void set() {
        setting = true;
        prLn("""
                ====================set====================
                | Available command:
                | > host <--------Server's host
                | > port <--------Server's port
                | > protocol <----Server's protocol
                |                 only supports 757 (1.18)
                | > userName <----Your name
                | > uuid <--------Your UUID
                | > token <-------Your AccessToken
                | > cancel <------Exit this setting mode
                ==========================================="""
        );
        pr("Set: ");
    }

    static class CommonSetting {
        public String host;
        public int port;
        public int protocol;
        public String username;
        public String uuid;
        public String token;
    }

    @SuppressWarnings("InfiniteRecursion")
    private static void input() throws IOException {
        try (Scanner sc = new Scanner(System.in)) {
            String str = sc.nextLine();
            if (str.equals("/cc")) {
                if (client != null && play)
                    commandMode();
                else
                    prLn("Can't do this! You need to join a server first.");
                input();
            }
            if (!commandMode && client.isConnected()) {
                client.getSender().clientChatMessagePacket(str);
            } else
                command(str);
            input();
        }
    }
}