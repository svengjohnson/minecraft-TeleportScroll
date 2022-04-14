package io.sjohnson.teleportscroll.helpers;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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

    public static boolean isCoordinateTeleportScroll(ItemStack item)
    {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        if (!isTeleportScroll(item)) {
            return false;
        }

        NBTItem nbtItem = new NBTItem(item);

        return nbtItem.hasKey("x");
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

    public static String getBedTeleportScrollName(int tier)
    {
        switch (tier) {
            case 2 -> {
                return ChatColor.AQUA + "Enhanced Bed Teleport Scroll";
            }
            case 3 -> {
                return ChatColor.LIGHT_PURPLE + "Eternal Bed Teleport Scroll";
            }
            default -> {
                return ChatColor.YELLOW + "Bed Teleport Scroll";
            }
        }
    }

    public static String getDefaultTeleportScrollName(int tier)
    {
        return getDefaultTeleportScrollName(tier, false);
    }


    public static String getDefaultTeleportScrollName(int tier, boolean bold)
    {
        switch (tier) {
            case 2 -> {
                return getTierFormatting(tier, bold) + "Enhanced Teleport Scroll";
            }
            case 3 -> {
                return getTierFormatting(tier, bold) + "Eternal Teleport Scroll";
            }
            default -> {
                return getTierFormatting(tier, bold) + "Teleport Scroll";
            }
        }
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

    public static String getCustomTeleportScrollName(int tier, String name, boolean bold)
    {
        return getTierFormatting(tier, bold) + ChatColor.stripColor(name);
    }

    public static String getDefaultBlankTeleportScrollName(int tier)
    {
        switch (tier) {
            case 2 -> {
                return getTierFormatting(tier) + "Blank Enhanced Teleport Scroll";
            }
            case 3 -> {
                return getTierFormatting(tier) + "Blank Eternal Teleport Scroll";
            }
            default -> {
                return getTierFormatting(tier) + "Blank Teleport Scroll";
            }
        }
    }

    public static String getDefaultEmptyTeleportBookName()
    {
        return ChatColor.YELLOW + "Empty Teleport Book";
    }

    public static String getDefaultTeleportBookName(Player player)
    {
        String playerName = ChatColor.stripColor(player.getDisplayName());
        return ChatColor.GOLD + "" + ChatColor.BOLD + playerName + "'s Teleport Book";
    }

    public static String getTierFormatting(int tier)
    {
        return getTierFormatting(tier, false);
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

    public static String getCardinalDirection(int yaw)
    {
        if (yaw == 180) {
            return "N";
        } else if (yaw == -90) {
            return "E";
        } else if (yaw == 90) {
            return "W";
        } else {
            return "S";
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
