package io.sjohnson.teleportscroll.helpers;

import de.tr7zw.changeme.nbtapi.*;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.UUID;

public class CreateItem {
    public static ItemStack createTeleportScroll(int tier)
    {
        Material material = Material.PAPER;
        String displayName = ItemHelper.getDefaultBlankTeleportScrollName(tier);
        int customModel;

        switch (tier) {
            case 3 -> {
                customModel = 10003;
            }
            case 2 -> {
                customModel = 10002;
            }
            default -> {
                customModel = 10001;
            }
        }

        ItemStack item = new ItemStack(material);
        ItemHelper.setItemName(item, displayName);
        ItemHelper.setItemLore(item, displayName);
        ItemHelper.setCustomModel(item, customModel);

        ItemMeta Meta = item.getItemMeta();
        assert Meta != null;

        Meta.addItemFlags(
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_ADDITIONAL_TOOLTIP,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_ARMOR_TRIM
        );
        item.setItemMeta(Meta);
        item.addUnsafeEnchantment(Enchantment.PROTECTION, 1);

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setBoolean("is_teleport_scroll", true);
        nbtItem.setInteger("tier", tier);

        return nbtItem.getItem();
    }

    public static ItemStack createTeleportScrollWithCoords(ItemStack item, String world, int x, int y, int z, float yaw)
    {

        NBTItem nbtItem = new NBTItem(item.clone());

        int tier = nbtItem.getInteger("tier");
        String displayName = ItemHelper.getDefaultTeleportScrollName(tier, true);
        String name = ItemHelper.getDefaultTeleportScrollName(tier, false);
        String direction;
        int customModel;

        if (yaw == 180) {
            direction = "N";
        } else if (yaw == -90) {
            direction = "E";
        } else if (yaw == 90) {
            direction = "W";
        } else {
            direction = "S";
        }

        switch (tier) {
            case 2 -> {
                customModel = 10005;
            }
            case 3 -> {
                customModel = 10006;
            }
            default -> {
                customModel = 10004;
            }
        }

        String lore = String.format("%s;" + ChatColor.WHITE + "%s X %s Y %s Z %s %s", name, world, x, y, z, direction);
        nbtItem.setString("world", world);
        nbtItem.setInteger("x", x);
        nbtItem.setInteger("y", y);
        nbtItem.setInteger("z", z);
        nbtItem.setFloat("yaw", yaw);

        if (tier == 3) {
            // prevent stacking
            nbtItem.setString("t3_scroll_uuid", UUID.randomUUID().toString());
        }

        ItemStack outputItem = nbtItem.getItem();
        ItemHelper.setItemLore(outputItem, lore);
        ItemHelper.setItemName(outputItem, displayName);
        ItemHelper.setCustomModel(outputItem, customModel);
        outputItem.setAmount(1);

        return outputItem;
    }

    public static ItemStack createCustomTeleportScroll(int count, int tier, String name, String world, int x, int y, int z, float yaw)
    {
        ItemStack baseScroll = createTeleportScroll(tier);
        ItemStack outputScroll = createTeleportScrollWithCoords(baseScroll, world, x, y, z, yaw);

        ItemMeta outputScrollMeta = outputScroll.getItemMeta();
        assert outputScrollMeta != null;

        outputScrollMeta.setDisplayName(name);
        outputScroll.setItemMeta(outputScrollMeta);
        outputScroll.setAmount(count);

        return outputScroll;
    }

    public static ItemStack createCustomBedTeleportScroll(int count, int tier, String name)
    {
        ItemStack outputScroll = createBedTeleportScroll(tier);

        ItemMeta outputScrollMeta = outputScroll.getItemMeta();
        assert outputScrollMeta != null;

        outputScrollMeta.setDisplayName(name);
        outputScroll.setItemMeta(outputScrollMeta);
        outputScroll.setAmount(count);

        return outputScroll;
    }

    public static ItemStack createBedTeleportScroll(int tier)
    {
        ItemStack bedScroll = createTeleportScroll(tier);
        String name = ItemHelper.getBedTeleportScrollName(tier);
        int customModel;

        switch (tier) {
            case 2 -> {
                customModel = 10008;
            }
            case 3 -> {
                customModel = 10009;
            }
            default -> {
                customModel = 10007;
            }
        }

        ItemHelper.setItemName(bedScroll, name);
        ItemHelper.setItemLore(bedScroll, ChatColor.WHITE + "Teleports you to your respawn point");
        ItemHelper.setCustomModel(bedScroll, customModel);
        bedScroll.removeEnchantment(Enchantment.PROTECTION);

        NBTItem nbtItem = new NBTItem(bedScroll);
        nbtItem.setBoolean("teleport_to_bed", true);

        return nbtItem.getItem();
    }

    public static ItemStack createLifesaver()
    {
        ItemStack lifesaver = new ItemStack(Material.EMERALD);
        ItemHelper.setItemName(lifesaver, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Lifesaver");
        ItemHelper.setItemLore(lifesaver, ChatColor.GOLD + "This item will prevent you from;" + ChatColor.GOLD + "taking fatal damage by teleporting;" + ChatColor.GOLD + "you to your bed");

        ItemMeta Meta = lifesaver.getItemMeta();
        assert Meta != null;

        Meta.addItemFlags(
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_ADDITIONAL_TOOLTIP,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_ARMOR_TRIM
        );
        lifesaver.setItemMeta(Meta);
        lifesaver.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);

        NBTItem nbtItem = new NBTItem(lifesaver);
        nbtItem.setBoolean("is_lifesaver", true);
        // prevent stacking
        nbtItem.setString("lifesaver_uuid", UUID.randomUUID().toString());
        return nbtItem.getItem();
    }

    public static ItemStack createEmptyTeleportBook(boolean withVanishingCurse)
    {
        ItemStack teleportBook = new ItemStack(Material.WRITTEN_BOOK);

        if (withVanishingCurse) {
            teleportBook.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
        }

        ItemHelper.setItemName(teleportBook, ItemHelper.getDefaultEmptyTeleportBookName());
        ItemHelper.setCustomModel(teleportBook, 10012);

        ItemMeta Meta = teleportBook.getItemMeta();
        assert Meta != null;

        Meta.addItemFlags(
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_ADDITIONAL_TOOLTIP,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_ARMOR_TRIM
        );

        if (withVanishingCurse) {
            Meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        teleportBook.setItemMeta(Meta);

        BookMeta bookMeta = (BookMeta) teleportBook.getItemMeta();

        BaseComponent[] basePage = TeleportBookHelper.getBasePage(withVanishingCurse).create();

        bookMeta.spigot().addPage(basePage);
        bookMeta.setTitle("");
        bookMeta.setAuthor("");
        teleportBook.setItemMeta(bookMeta);


        NBTItem nbtItem = new NBTItem(teleportBook);
        nbtItem.setBoolean("is_teleport_book", true);
        nbtItem.setBoolean("empty_teleport_book", true);
        nbtItem.setInteger("generation", 3);
        // prevent stacking
        nbtItem.setString("teleport_book_uuid", UUID.randomUUID().toString());

        return nbtItem.getItem();
    }
}
