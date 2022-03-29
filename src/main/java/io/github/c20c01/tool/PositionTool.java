package io.github.c20c01.tool;

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

    public static Position getCurrent(Position delta , Position prev) {
        return new Position((delta.x() / 128 + prev.x() * 32) / 32, (delta.y() / 128 + prev.y() * 32) / 32, (delta.z() / 128 + prev.z() * 32) / 32);
    }
}
