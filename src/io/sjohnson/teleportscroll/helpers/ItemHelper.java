package io.sjohnson.teleportscroll.helpers;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

public class ItemHelper
{
    public static boolean isTeleportScroll(ItemStack item)
    {
        NBTItem nbtItem = new NBTItem(item);

        return nbtItem.getBoolean("is_teleport_scroll");
    }
}
