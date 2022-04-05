package io.github.c20c01.tool;

import java.util.UUID;

public class Entity {
    private int ID, type;
    private UUID uuid;
    private Position position;
    private boolean living;
    private double dis;


    public Entity(int ID, UUID uuid, int type, Position position, boolean living, double dis) {
        this.ID = ID;
        this.uuid = uuid;
        this.type = type;
        this.position = position;
        this.living = living;
        this.dis = dis;
    }

    public String toString() {
        return "Type: " + this.type + ", ID: " + this.ID + ", Distance: " + Math.round(dis);
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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

    public boolean isLiving() {
        return living;
    }

    public void setLiving(boolean living) {
        this.living = living;
    }

    public double getDis() {
        return dis;
    }

    public void setDis(double dis) {
        this.dis = dis;
    }
}
