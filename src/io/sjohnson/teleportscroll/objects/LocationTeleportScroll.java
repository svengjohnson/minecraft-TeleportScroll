package io.sjohnson.teleportscroll.objects;

public class LocationTeleportScroll extends BaseTeleportScroll {
    public LocationTeleportScroll(int id, int count, int tier, String name, String world, int x, int y, int z, float yaw) {
        this.id = id;
        this.count = count;
        this.tier = tier;
        this.name = name;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.teleport_to_bed = false;
    }
}
