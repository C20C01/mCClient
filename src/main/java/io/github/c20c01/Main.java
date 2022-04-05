package io.github.c20c01;

import com.google.gson.Gson;
import io.github.c20c01.tool.proTool.MinecraftClient;
import io.github.c20c01.tool.proTool.Packets.encryption.Tool;

import java.io.*;
import java.util.Scanner;

public class Main {
    private static final Gson gson = new Gson();
    private static final Scanner sc = new Scanner(System.in);
    private static MinecraftClient client;
    private static String host = "0.0.0.0";
    private static String userName = null;
    private static int port = 25565;
    private static int protocol = 758;
    private static boolean commandMode = true;
    private static boolean offlineFirst = true;
    private static File settingFile;
    private static CommonSetting cs = new CommonSetting();
    private static boolean play = false;
    private static StringWriter stringWriter;
    private static boolean waitingForMore = false;
    private static int[] waitingID = {-1, -1, -1};

    public static void main(String[] args) throws IOException {
        welcome();
        loadSetting();
        input();
    }

    private static void prLn(String s) {
        output(s, true, true);
    }

    private static void pr(String s) {
        output(s, true, false);
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

    private static void loadSetting() throws IOException {
        settingFile = new File(".//mCClient.json");
        if (!settingFile.createNewFile()) getSetting();
        else {
            prLn("First time use? Read the instructions below to get started.");
            help();
            writeSettingJson();
        }

    }

    static class CommonSetting {
        public String host;
        public int port;
        public int protocol;
        public String username;
        public String uuid;
        public String token;

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

    private static boolean checkPlay() {
        if (client != null && play)
            return true;
        else {
            prLn("Can't do this! You need to join a server first.");
            return false;
        }
    }

    @SuppressWarnings("InfiniteRecursion")
    private static void input() throws IOException {
        try {
            String str = sc.nextLine();
            if (str.equals("/cc")) {
                if (checkPlay()) commandMode();
            } else if (!commandMode && client.isConnected()) client.sender.ChatMessageOut(str);
            else command(str);
            input();
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            play = false;
            close();
        }
    }

    private static void commandMode() {
        commandMode(!commandMode);
    }

    private static void commandMode(boolean b) {
        if (b) {
            commandMode = true;
            if (client.isConnected()) stringWriter = new StringWriter();
            prLn("\nEnter the command mode.\n");
        } else {
            commandMode = false;
            printSW();
            prLn("\nExit the command mode.\n");
        }
    }

    private static void printSW() {
        if (stringWriter != null) {
            pr(stringWriter.toString());
            try {
                stringWriter.close();
            } catch (IOException e) {
                stringWriter = null;
            }
        }
    }

    private static void waitingForMore(int id) {
        waitingForMore = true;
        waitingID = new int[]{id, -1, -1};
    }

    private static void finishWaiting() {
        waitingForMore = false;
        waitingID = new int[]{-1, -1, -1};
    }

    public static void output(String s) {
        output(s, false, true);
    }

    public static void output(String s, boolean important) {
        output(s, important, true);
    }

    public static void output(String s, boolean important, boolean ln) {
        if (commandMode && !important) {
            if (ln) stringWriter.write(s + "\n");
            else stringWriter.write(s);
        } else {
            if (ln) System.out.println(s);
            else System.out.print(s);
        }
    }

    private static void command(String input) throws IOException {
        if (waitingForMore) {
            switch (waitingID[0]) {
                case 0 -> setting(input);
                case 1 -> sending(input);
                case 2 -> setAttack(input);
            }
        } else
            switch (input) {
                case "set" -> set();
                case "ping" -> ping();
                case "join" -> join();
                case "help" -> help();
                case "check" -> check();
                case "close" -> close();
                case "send" -> send();
                case "attack" -> attack();
                case "showe" -> showe();
                case "dig" -> dig();
                default -> prLn("Unknown command! Type \"help\" to get help.");
            }
    }

    private static void set() {
        waitingForMore(0);
        prLn("""
                =====================set=====================
                | Available command:
                | > host <--------Server's host
                | > port <--------Server's port
                | > protocol <----Server's protocol
                |                 only supports >=757 (1.18+)
                | > username <----Your name
                | > uuid <--------Your UUID
                | > token <-------Your AccessToken
                | > cancel <------Exit this setting mode
                ============================================="""
        );
        pr("Set: ");
    }

    private static void setting(String input) throws IOException {
        if (waitingID[1] == -1)
            switch (input) {
                case "host" -> {
                    waitingID[1] = 0;
                    pr("host: ");
                }
                case "port" -> {
                    waitingID[1] = 1;
                    pr("port: ");
                }
                case "protocol" -> {
                    waitingID[1] = 2;
                    pr("protocol: ");
                }
                case "username" -> {
                    waitingID[1] = 3;
                    pr("username: ");
                }
                case "uuid" -> {
                    waitingID[1] = 4;
                    pr("uuid: ");
                }
                case "token" -> {
                    waitingID[1] = 5;
                    pr("token: ");
                }
                case "cancel" -> {
                    prLn("Cancel.");
                    finishWaiting();
                }
                default -> prLn("Unknown command! Please enter again.");
            }
        else {
            switch (waitingID[1]) {
                case 0 -> host = input;
                case 1 -> port = Integer.parseInt(input);
                case 2 -> protocol = Integer.parseInt(input);
                case 3 -> userName = input;
                case 4 -> Tool.setUuid(input);
                case 5 -> Tool.setAccessToken(input);
            }
            prLn("Complete.");
            writeSettingJson();
            finishWaiting();
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

    private static void runMinecraftClient() throws IOException {
        if (client == null || !client.isConnected()) {
            client = new MinecraftClient(host, port, protocol);
            if (client.ping()) client.connect(userName);
        } else prLn("Multi-role control is not currently supported!");
    }

    public static void LoginSuccess() {
        commandMode(false);
        play = true;
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

    public static void closeFromClient() {
        play = false;
        printSW();
        if (!commandMode) commandMode(true);
    }

    private static void close() throws IOException {
        if (play) {
            client.closeFromMain();
            play = false;
        } else {
            prLn("==================================Close==================================");
            System.exit(0);
        }
    }

    private static void send() {
        if (checkPlay()) {
            pr("send: ");
            waitingForMore(1);
        }
    }

    private static void sending(String s) throws IOException {
        if (client.isConnected()) {
            client.sender.ChatMessageOut(s);
            prLn("Done!");
        } else prLn("Wrong!");
        finishWaiting();
    }

    private static void attack() {
        if (checkPlay()) {
            prLn("~~commands~~");
            pr("attack: ");
            waitingForMore(2);
        }
    }

    private static void setAttack(String input) {
        if (waitingID[1] == -1) {
            switch (input) {
                case "check" -> prLn("Attacking: " + (client.isAttacking() ? "Yes" : "No"));
                case "cancel" -> prLn("Cancel.");
                case "stop" -> {
                    client.attack(false);
                    prLn("Stop.");
                }
                case "start" -> {
                    client.attack(true);
                    prLn("Start.");
                }
            }
            finishWaiting();
        }
    }

    private static void showe() {
        if (checkPlay())
            client.entityTool.showEntity();
    }

    private static void dig() throws IOException {
        if (checkPlay())
            client.dig();
    }
}