package io.sjohnson.teleportscroll.helpers;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemHelper
{
    public static boolean isTeleportScroll(ItemStack item)
    {
        NBTItem nbtItem = new NBTItem(item);

        return nbtItem.getBoolean("is_teleport_scroll");
    }

    public static void renameTeleportScroll(ItemStack item, String name)
    {
        NBTItem nbtItem = new NBTItem(item);

        int tier = nbtItem.getInteger("tier");
        String newDisplayName = switch (tier) {
            case 2 -> ChatColor.YELLOW + name;
            case 3 -> ChatColor.YELLOW + "" + ChatColor.BOLD + name;
            default -> ChatColor.AQUA + name;
        };

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(newDisplayName);
        item.setItemMeta(meta);
    }
}
