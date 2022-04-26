package io.github.c20c01.tool;

import io.github.c20c01.Main;

import static java.lang.Math.atan2;

public class PositionTool {
    public static double getDis(Position a, Position b) {
        return Math.sqrt(Math.pow((a.x() - b.x()), 2) + Math.pow((a.y() - b.y()), 2) + Math.pow((a.z() - b.z()), 2));
    }

    public static float[] getRotation(Position p, Position p0) {
        float[] res = new float[2];
        float yaw, pitch;
        yaw = (float) (-atan2((p.x() - p0.x()), p.z() - p0.z()) / Math.PI * 180);
        if (yaw < 0) yaw += 360;
        pitch = (float) (-Math.asin((p.y() - p0.y()) / getDis(p, p0)) / Math.PI * 180);
        res[0] = yaw;
        res[1] = pitch;
        return res;
    }

    public static Position getCurrent(Position delta, Position prev) {
        return new Position((delta.x() / 128 + prev.x() * 32) / 32, (delta.y() / 128 + prev.y() * 32) / 32, (delta.z() / 128 + prev.z() * 32) / 32);
    }

    public static long getPosition(Position position) {
        return (((long) position.x() & 0x3FFFFFF) << 38) | (((long) position.z() & 0x3FFFFFF) << 12) | ((long) position.y() & 0xFFF);
    }

    public static long getPosition(int x, int y, int z) {
        return (((long) x & 0x3FFFFFF) << 38) | (((long) z & 0x3FFFFFF) << 12) | ((long) y & 0xFFF);
    }

    public static Position inputIntPosition(String input) {
        String[] point = input.split(" ");
        if (point.length == 3) {
            return new Position(Integer.parseInt(point[0]), Integer.parseInt(point[1]), Integer.parseInt(point[2]));
        } else {
            Main.output("Wrong format, try again!", true);
        }
        return null;
    }
}
