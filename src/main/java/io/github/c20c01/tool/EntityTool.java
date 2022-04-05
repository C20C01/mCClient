package io.github.c20c01.tool;

import io.github.c20c01.Main;
import io.github.c20c01.tool.proTool.MinecraftClient;

import java.util.ArrayList;

public class EntityTool {
    private final MinecraftClient client;
    private final ArrayList<Entity> entityList = new ArrayList<>();

    public EntityTool(MinecraftClient client) {
        this.client = client;
    }

    public ArrayList<Entity> getEntityList() {
        return entityList;
    }

    public void addEntity(Entity n) {
        entityList.add(n);
    }

    public void destroyEntity(int[] IDs) {
        for (int ID : IDs) {
            entityList.removeIf(entity -> entity.getID() == ID);
        }
    }

    public void entityMove(int entityID, Position pos) {
        for (Entity e : entityList)
            if (e.getID() == entityID) {
                e.setPosition(PositionTool.getCurrent(pos, e.getPosition()));
                e.setDis(PositionTool.getDis(client.playerPos, e.getPosition()));
                break;
            }
    }

    public void entityTP(int entityID, Position pos) {
        for (Entity e : entityList)
            if (e.getID() == entityID) {
                e.setPosition(pos);
                e.setDis(PositionTool.getDis(client.playerPos, e.getPosition()));
                break;
            }
    }

    public void showEntity() {
        Main.output(TimeTool.getTime() + "Number of entities: " + entityList.size(), true);
        for (Entity e : entityList) {
            Main.output(e.toString(), true);
        }
    }

    private final ArrayList<Entity> livingEList = new ArrayList<>();

    public ArrayList<Entity> getLivingEList() {
        livingEList.clear();
        for (Entity e : entityList) {
            if (e.isLiving()) livingEList.add(e);
        }
        return livingEList;
    }

    public Entity getClosestE(ArrayList<Entity> list) {
        if (list.size() > 0) {
            Entity closestE = list.get(0);
            double dis = closestE.getDis();
            for (Entity e : list) {
                if (e.getDis() < dis) {
                    dis = e.getDis();
                    closestE = e;
                }
            }
            list.clear();
            return closestE;
        }
        return null;
    }

    public void close() {

    }
}
