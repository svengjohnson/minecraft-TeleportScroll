package io.sjohnson.teleportscroll.objects.json;

public class JsonBedTeleportScroll extends JsonTeleportScroll {

    public JsonBedTeleportScroll(int id, int count, int tier, String name, String displayName) {
        this.id = id;
        this.count = count;
        this.tier = tier;
        this.name = name;
        this.display_name = displayName;
        this.teleport_to_bed = true;
    }
}
