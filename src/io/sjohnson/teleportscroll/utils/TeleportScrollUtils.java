package io.sjohnson.teleportscroll.utils;

import com.google.gson.JsonObject;
import io.sjohnson.teleportscroll.helpers.ItemHelper;
import io.sjohnson.teleportscroll.objects.BedTeleportScroll;
import io.sjohnson.teleportscroll.objects.ItemizableTeleportScroll;
import io.sjohnson.teleportscroll.objects.LocationTeleportScroll;
import io.sjohnson.teleportscroll.objects.NBTFields;
import org.bukkit.inventory.ItemStack;

public class TeleportScrollUtils {
    public static boolean isBedTeleportScroll(JsonObject json) {
        return json.get(NBTFields.TELEPORT_TO_BED).getAsBoolean();
    }

    public static ItemizableTeleportScroll createFromJson(JsonObject json) {
        if (isBedTeleportScroll(json)) {
            return createBedTeleportScroll(json);
        }

        return createLocationTeleportScroll(json);
    }

    public static  ItemizableTeleportScroll createFromItemStack(ItemStack itemStack) {
        return TeleportScrollUtils.createFromItemStack(itemStack, 0);
    }

    public static ItemizableTeleportScroll createFromItemStack(ItemStack itemStack, int id) {
        if (ItemHelper.isBedTeleportScroll(itemStack)) {
            return new BedTeleportScroll(itemStack, id);
        }

        return new LocationTeleportScroll(itemStack, id);
    }

    public static BedTeleportScroll createBedTeleportScroll(JsonObject json) {
        int id = json.get(NBTFields.ID).getAsInt();
        int count = json.get(NBTFields.COUNT).getAsInt();
        int tier = json.get(NBTFields.TIER).getAsInt();
        String name = json.get(NBTFields.DISPLAY_NAME).getAsString();

        return new BedTeleportScroll(id, tier, count, name);
    }

    public static LocationTeleportScroll createLocationTeleportScroll(JsonObject json) {
        int id = json.get(NBTFields.ID).getAsInt();
        int count = json.get(NBTFields.COUNT).getAsInt();
        int tier = json.get(NBTFields.TIER).getAsInt();
        String name = json.get(NBTFields.DISPLAY_NAME).getAsString();
        String world = json.get(NBTFields.WORLD).getAsString();
        int x = json.get(NBTFields.X).getAsInt();
        int y = json.get(NBTFields.Y).getAsInt();
        int z = json.get(NBTFields.Z).getAsInt();
        float yaw = json.get(NBTFields.YAW).getAsFloat();

        return new LocationTeleportScroll(id, tier, world, x, y, z, yaw, name, count);
    }
}
