package io.sjohnson.teleportscroll.objects;

public class BedTeleportScroll extends BaseTeleportScroll {

    public BedTeleportScroll(int id, int count, int tier, String name) {
        this.id = id;
        this.count = count;
        this.tier = tier;
        this.name = name;
        this.teleport_to_bed = true;
    }
}
