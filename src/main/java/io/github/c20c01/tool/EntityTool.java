package io.github.c20c01.tool;

import io.github.c20c01.Main;
import io.github.c20c01.tool.proTool.MinecraftClient;

import java.util.HashMap;

public class EntityTool {
    private final MinecraftClient client;
    private final HashMap<Integer, Entity> entityHashMap = new HashMap<>();
    private final HashMap<Integer, Entity> livingEntityHashMap = new HashMap<>();
    private boolean entityChanged = true;

    public EntityTool(MinecraftClient client) {
        this.client = client;
    }

    public HashMap<Integer, Entity> getEntityHashMap() {
        return entityHashMap;
    }

    public HashMap<Integer, Entity> getLivingEntityHashMap() {
        return livingEntityHashMap;
    }

    public boolean entityChanged() {
        if (entityChanged) {
            entityChanged = false;
            return true;
        } else return false;
    }

    public void resetEntityChanged() {
        entityChanged = true;
    }

    public void addEntity(int id, Entity info) {
        entityChanged = true;
        entityHashMap.put(id, info);
    }

    public void addLivingEntity(int id, Entity info) {
        entityChanged = true;
        livingEntityHashMap.put(id, info);
    }

    public void destroyEntity(int[] IDs) {
        entityChanged = true;
        for (int id : IDs) {
            if (livingEntityHashMap.remove(id) == null) entityHashMap.remove(id);
        }
    }

    public void entityMove(int entityID, Position pos) {
        Entity entity = livingEntityHashMap.get(entityID);
        if (entity == null) {
            entity = entityHashMap.get(entityID);
            if (entity == null) return;
        }
        entity.setPosition(PositionTool.getCurrent(pos, entity.getPosition()));
        entity.setDis(PositionTool.getDis(client.playerPos, entity.getPosition()));
    }

    public void entityTP(int entityID, Position pos) {
        Entity entity = livingEntityHashMap.get(entityID);
        if (entity == null) {
            entity = entityHashMap.get(entityID);
            if (entity == null) return;
        }
        entity.setPosition(pos);
        entity.setDis(PositionTool.getDis(client.playerPos, entity.getPosition()));
    }

    public void showEntity() {
        Main.output(TimeTool.getTime() + "Number of entities: " + (livingEntityHashMap.size() + entityHashMap.size()), true);
        Main.output("======== living entity ========", true);
        if (livingEntityHashMap.isEmpty()) Main.output("null", true);
        else livingEntityHashMap.forEach((ID, entity) -> Main.output("ID: " + ID + ", " + entity.toString(), true));
        Main.output("====== non-living entity ======", true);
        if (entityHashMap.isEmpty()) Main.output("null", true);
        else entityHashMap.forEach((ID, entity) -> Main.output("ID: " + ID + ", " + entity.toString(), true));
    }

    public void close() {
        entityHashMap.clear();
        livingEntityHashMap.clear();
    }

}
