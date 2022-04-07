package io.sjohnson.teleportscroll.helpers;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public class ItemHelper
{
    public static boolean isTeleportScroll(ItemStack item)
    {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        NBTItem nbtItem = new NBTItem(item);

        return nbtItem.getBoolean("is_teleport_scroll");
    }

    public static boolean isTeleportBook(ItemStack item)
    {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        NBTItem nbtItem = new NBTItem(item);

        return nbtItem.getBoolean("is_teleport_book");
    }

    public static boolean isBedTeleportScroll(ItemStack item)
    {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        if (!isTeleportScroll(item)) {
            return false;
        }

        NBTItem nbtItem = new NBTItem(item);

        return nbtItem.getBoolean("teleport_to_bed");
    }

    public static boolean isLifesaver(ItemStack item)
    {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        NBTItem nbtItem = new NBTItem(item);

        return nbtItem.getBoolean("is_lifesaver");
    }

    public static void renameTeleportScroll(ItemStack item, String name)
    {
        if (!isTeleportScroll(item)) {
            return;
        }

        NBTItem nbtItem = new NBTItem(item);

        int tier = nbtItem.getInteger("tier");
        String newDisplayName = switch (tier) {
            case 2 -> ChatColor.AQUA + "" + ChatColor.BOLD + name;
            case 3 -> ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + name;
            default -> ChatColor.YELLOW + "" + ChatColor.BOLD + name;
        };

        setItemName(item, newDisplayName);
    }

    public static void setCustomModel(ItemStack item, int model)
    {
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.setCustomModelData(model);

        item.setItemMeta(meta);
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

    public static int getEmptyInventorySlots(Inventory inventory) {
        int i = 0;

        for (ItemStack item : inventory.getContents()) {
            if (item == null || item.getType() == Material.AIR) {
                i++;
            }
        }

        return i;
    }
}
