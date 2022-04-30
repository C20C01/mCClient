package io.github.c20c01;

import com.google.gson.Gson;
import io.github.c20c01.tool.Position;
import io.github.c20c01.tool.PositionTool;
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
    private static File[] nbsFiles = null;
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
            getHelp("1");
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
                stringWriter = null;
            } catch (IOException ignored) {
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
                case 1 -> getHelp(input);
                case 2 -> sending(input);
                case 3 -> setAttack(input);
                case 4 -> moveTo(input);
                case 5 -> changeHeldItem(input);
                case 6 -> setMusic(input);
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
                case "item" -> item();
                case "move" -> move();
                case "music" -> music();
                case "auto" -> auto();
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
                | > back <--------Back to previous
                ============================================="""
        );
        pr("set: ");
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
                case "back" -> {
                    prLn("Back to main page.");
                    finishWaiting();
                }
                default -> prLn("Unknown command! Please enter again.");
            }
        else {
            if (!input.equals("back")) {
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
            }
            pr("set:");
            waitingID[1] = -1;
        }

    }

    private static void ping() {
        new MinecraftClient(host, port, protocol).ping();
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

    private static void help() {
        prLn("""
                =====================help=====================
                | Enter a number to get help page.
                | > <page> <------Select the number of pages
                | > back <--------Back to previous
                =============================================="""
        );
        pr("help: ");
        waitingForMore(1);
    }

    private static void getHelp(String page) {
        if (!page.equals("back")) {
            switch (page) {
                case "1" -> prLn("""
                        ==================Help 1/3=================
                        | help <---Get help.
                        | set <----Setting parameters.
                        | check <--Show your current settings.
                        | ping <---Get server information.
                        | join <---Join the server.
                        | close <--Exit server or mCClient.
                        | /cc <----Enter or Exit the command mode.
                        ==========================================="""
                );
                case "2" -> prLn("""
                        ==================Help 2/3=================
                        | send <---Send a message to server.
                        | attack <-Attack specified entity.
                        | showe <--Show all entities around.
                        | dig <----Dig the specified block.
                        | item <---Switch your inventory.
                        | move <---Move to the specified position.
                        ==========================================="""
                );
                case "3" -> prLn("""
                        ==================Help 3/3=================
                        | music <--Play the note block.
                        | auto <---Automatic chat.
                        ==========================================="""
                );
                default -> prLn("Wrong page, try again.");
            }
            if (waitingID[0] == 1)
                pr("help: ");
        } else {
            prLn("Back to main page.");
            finishWaiting();
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
            waitingForMore(2);
        }
    }

    private static void sending(String s) throws IOException {
        if (client.isConnected()) {
            client.sender.ChatMessageOut(s);
            prLn("Done!");
        } else prLn("Error!");
        finishWaiting();
    }

    private static void attack() {
        if (checkPlay()) {
            prLn("""
                    ====================attack====================
                    | Available command:
                    | > start <-------Start attacking
                    | > stop <--------Stop attacking
                    | > check <-------Check if you are attacking
                    | > set <---------Set related parameters
                    | > back <--------Back to previous
                    ============================================="""
            );
            pr("attack: ");
            waitingForMore(3);
        }
    }

    private static void setAttack(String input) {
        if (waitingID[1] == -1) {
            switch (input) {
                case "start" -> {
                    client.attack(true);
                    prLn("Start!");
                }
                case "stop" -> {
                    client.attack(false);
                    prLn("Stop!");
                }
                case "set" -> {
                    prLn("""
                            ==================attack set==================
                            | Available command:
                            | > time <--------Attack interval
                            | > id <----------ID of the attacked entity
                            | > back <--------Back to previous
                            =============================================="""
                    );
                    pr("set: ");
                    waitingID[1] = 0;
                    return;
                }
                case "check" -> client.checkAttack();
                case "back" -> prLn("Back.");
                default -> {
                    prLn("Unknown command! Please enter again.");
                    pr("attack: ");
                    return;
                }
            }
            finishWaiting();
        } else {
            if (waitingID[2] == -1) {
                switch (input) {
                    case "time" -> {
                        waitingID[2] = 0;
                        pr("time (ms): ");
                    }
                    case "id" -> {
                        waitingID[2] = 1;
                        pr("id: ");
                    }
                    case "back" -> {
                        waitingID[1] = -1;
                        prLn("attack: ");
                    }
                    default -> {
                        prLn("Unknown command! Please enter again.");
                        pr("set: ");
                    }
                }
            } else {
                if (input.equals("back")) {
                    waitingID[1] = -1;
                    pr("attack: ");
                } else {
                    switch (waitingID[2]) {
                        case 0 -> client.setAttackTime(Integer.parseInt(input));
                        case 1 -> client.setAttackID(Integer.parseInt(input));
                    }
                    prLn("Complete.");
                    pr("set: ");
                }
                waitingID[2] = -1;
            }
        }
    }

    private static void showe() {
        if (checkPlay())
            client.entityTool.showEntity();
    }

    private static void dig() {
        if (checkPlay())
            prLn("not developed!");
    }

    private static void item() {
        if (checkPlay()) {
            prLn("""
                    =====================item=====================
                    | Enter 1~9 to select first to ninth inventory.
                    | > <slot> <------Select the item slot
                    | > back <--------Back to previous
                    =============================================="""
            );
            pr("item: ");
            waitingForMore(5);
        }
    }

    private static void changeHeldItem(String input) throws IOException {
        if (input.equals("back")) {
            prLn("Back to main page.");
            finishWaiting();
        } else {
            int slot = Integer.parseInt(input);
            if (slot < 10 && slot > 0) {
                client.changeHeldItem(slot);
                prLn("Complete.");
                finishWaiting();
            } else {
                prLn("Slot must be 1~9, try again!");
                pr("item: ");
            }
        }
    }

    private static void move() {
        if (checkPlay()) {
            prLn("""
                    ======================move======================
                    | Move to the specified position.
                    | > <x> <y> <z> <----Move to point(x,y,z)
                    |                    Not "/tp", if you move too
                    |                    far, you will be kicked
                    |                    out by the server.
                    | > back <-----------Back to previous
                    ==============================================="""
            );
            pr("move: ");
            waitingForMore(4);
        }
    }

    private static void moveTo(String input) throws IOException {
        if (input.equals("back")) {
            prLn("Back to main page.");
            finishWaiting();
        } else {
            Position position = PositionTool.inputIntPosition(input);
            if (position != null) {
                client.playerPosition(position, true);
                prLn("Complete.");
                finishWaiting();
            } else {
                pr("move: ");
            }
        }
    }


    private static void music() {
        if (checkPlay()) {
            prLn("""
                    =====================music=====================
                    | Available command:
                    | > load <--------Load the specified song(.nbs)
                    | > play <--------Start/Resume playing
                    | > stop <--------Pause playing
                    | > cancel <------Cancel playback
                    | > check <-------View playback status
                    | > back <--------Back to previous
                    ==============================================="""
            );
            try {
                if (new File("songs").mkdir())
                    prLn("A folder named \"songs\" has been created,\n" +
                            "please put your \".nbs\" files into it.");
            } catch (Exception e) {
                prLn("Failed to create a folder named \"songs\"!");
            }
            pr("music: ");
            waitingForMore(6);
        }
    }

    private static void setMusic(String input) {

        if (waitingID[1] == -1) {
            switch (input) {
                case "load" -> {
                    prLn("""
                            =====================music load=====================
                            | Load the specified song.
                            | > <number> <--Load songs by the number below
                            |               Only files in .nbs format placed in
                            |               the "songs" folder will be loaded
                            | > back <------Back to previous
                            ===================================================="""
                    );
                    nbsFiles = new File("songs").listFiles(
                            f -> f.getName().substring(f.getName().lastIndexOf(".")).equals(".nbs"));
                    if (nbsFiles != null) {
                        for (int i = 0; i < nbsFiles.length; i++) {
                            File file = nbsFiles[i];
                            pr("| " + i + " ");
                            for (int j = 0; j < 10 - ("" + i).length(); j++) {
                                pr("-");
                            }
                            prLn(" " + file.getName());
                        }
                        prLn("====================================================");
                        pr("load: ");
                        waitingID[1] = 0;
                        return;
                    } else {
                        prLn("No \".nbs\" files found!");
                        waitingID[1] = -1;
                    }
                }
                case "back" -> {
                    prLn("Back to main page.");
                    finishWaiting();
                    return;
                }
                case "cancel" -> client.musicBox.stopSong();

                case "stop" -> client.musicBox.pauseSong();

                case "play" -> {
                    if (client.musicBox.startSong()) prLn("Start!");
                }
                case "check" -> client.musicBox.checkPlaying();
                default -> prLn("Unknown command! Please enter again.");
            }
            pr("music: ");
        } else {
            if (!input.equals("back")) {
                try {
                    File file = nbsFiles[Integer.parseInt(input)];
                    client.musicBox.loadSong(file);
                } catch (Exception e) {
                    prLn("Failed to load! Please enter the correct number.");
                    pr("load: ");
                    return;
                }
            }
            nbsFiles = null;
            waitingID[1] = -1;
            pr("music: ");
        }
    }

    private static void auto() {
        client.listener.messageTool.autoSender = !client.listener.messageTool.autoSender;
        if (client.listener.messageTool.autoSender)
            prLn("Automatic chat: ON.");
        else
            prLn("Automatic chat: OFF.");
    }

}