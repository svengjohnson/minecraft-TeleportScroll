package io.sjohnson.teleportscroll.helpers;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public class ItemHelper
{
    public static boolean isTeleportScroll(ItemStack item)
    {
        NBTItem nbtItem = new NBTItem(item);

        return nbtItem.getBoolean("is_teleport_scroll");
    }

    public static boolean isBedTeleportScroll(ItemStack item)
    {
        if (!isTeleportScroll(item)) {
            return false;
        }

        NBTItem nbtItem = new NBTItem(item);

        return nbtItem.getBoolean("teleport_to_bed");
    }

    public static void renameTeleportScroll(ItemStack item, String name)
    {
        if (!isTeleportScroll(item)) {
            return;
        }

        NBTItem nbtItem = new NBTItem(item);

        int tier = nbtItem.getInteger("tier");
        String newDisplayName = switch (tier) {
            case 2 -> ChatColor.YELLOW + name;
            case 3 -> ChatColor.YELLOW + "" + ChatColor.BOLD + name;
            default -> ChatColor.AQUA + name;
        };

        setItemName(item, newDisplayName);
    }

    public static void setItemLore(ItemStack item, String lore)
    {
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.setLore(formatLore(lore));

        item.setItemMeta(meta);
    }

    public static void setItemName(ItemStack item, String displayName)
    {
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.setDisplayName(displayName);

        item.setItemMeta(meta);
    }

    private static ArrayList<String> formatLore(String lore)
    {
        String[] loreText = lore.split(";");

        return new ArrayList<>(Arrays.asList(loreText));
    }
}
