package io.github.c20c01.tool;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeTool {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");

    public static void printTime() {
        System.out.print(getTime() + " ");
    }

    public static void printTime(String head) {
        System.out.print(head + getTime() + " ");
    }

    private static String getTime() {
        Date date = new Date();
        return sdf.format(date);
    }

}
