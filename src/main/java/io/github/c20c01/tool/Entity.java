package io.github.c20c01.tool;

import java.util.UUID;

public class Entity {
    private int type;
    private UUID uuid;
    private Position position;
    private double dis;

    public Entity(UUID uuid, int type, Position position, double dis) {
        this.uuid = uuid;
        this.type = type;
        this.position = position;
        this.dis = dis;
    }

    public String toString() {
        return "Type: " + this.type+ ", Distance: " + Math.round(dis);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public double getDis() {
        return dis;
    }

    public void setDis(double dis) {
        this.dis = dis;
    }
}
