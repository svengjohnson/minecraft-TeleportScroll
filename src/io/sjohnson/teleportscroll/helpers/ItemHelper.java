package io.sjohnson.teleportscroll.helpers;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.sjohnson.teleportscroll.objects.NBTFields;
import io.sjohnson.teleportscroll.objects.model.CustomModelData;
import org.bukkit.*;
import org.bukkit.entity.Player;
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

        return nbtItem.getBoolean(NBTFields.IS_TELEPORT_SCROLL);
    }

    public static boolean isTeleportBook(ItemStack item)
    {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        NBTItem nbtItem = new NBTItem(item);

        return nbtItem.getBoolean(NBTFields.IS_TELEPORT_BOOK);
    }

    public static boolean isBedTeleportScroll(ItemStack item)
    {
        if (!isTeleportScroll(item)) {
            return false;
        }

        NBTItem nbtItem = new NBTItem(item);

        return nbtItem.getBoolean(NBTFields.TELEPORT_TO_BED);
    }

    public static boolean isBlankTeleportScroll(ItemStack item) {
        return !ItemHelper.isBedTeleportScroll(item) && !ItemHelper.isCoordinateTeleportScroll(item);
    }

    public static boolean isCoordinateTeleportScroll(ItemStack item)
    {
        if (!isTeleportScroll(item)) {
            return false;
        }

        NBTItem nbtItem = new NBTItem(item);

        return nbtItem.hasKey(NBTFields.X);
    }

    public static boolean isLifesaver(ItemStack item)
    {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        NBTItem nbtItem = new NBTItem(item);

        return nbtItem.getBoolean("is_lifesaver");
    }

    public static void renameTeleportScrollOrBook(ItemStack item, String name)
    {
        if (!isTeleportScroll(item) && !isTeleportBook(item)) {
            return;
        }

        String newDisplayName;

        if (isTeleportScroll(item)) {
            newDisplayName = getCustomTeleportScrollOrBookName(item, name, true);
        } else {
            newDisplayName = ChatColor.GOLD + "" + ChatColor.BOLD + name;
        }

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

    public static int getEmptyInventorySlots(Inventory inventory) {
        int i = 0;

        for (ItemStack item : inventory.getContents()) {
            if (item == null || item.getType() == Material.AIR) {
                i++;
            }
        }

        return i;
    }

    public static String getCustomTeleportScrollOrBookName(ItemStack item, String name, boolean bold)
    {
        if (!isTeleportScroll(item) && !isTeleportBook(item)) {
            return null;
        }

        NBTItem nbtItem = new NBTItem(item);
        String newDisplayName;

        if (isTeleportScroll(item)) {
            int tier = nbtItem.getInteger("tier");
            newDisplayName = getTierFormatting(tier, bold) + name;
        } else {
            newDisplayName = ChatColor.GOLD + "" + ChatColor.BOLD + name;
        }

        return newDisplayName;
    }

    public static String getTierFormatting(int tier, boolean bold)
    {
        String suffix = "";

        if (bold) {
            suffix = "" + ChatColor.BOLD;
        }

        switch (tier) {
            case 2 -> {
                return ChatColor.AQUA + suffix;
            }
            case 3 -> {
                return ChatColor.LIGHT_PURPLE + suffix;
            }
            default -> {
                return ChatColor.YELLOW + suffix;
            }
        }
    }

    public static void dropItem(Player player, ItemStack itemStack)
    {
        Location location = player.getLocation();
        World world = location.getWorld();

        assert world != null;
        world.dropItem(location, itemStack);
    }
}
