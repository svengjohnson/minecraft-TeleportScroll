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
        Material material;

        String name;
        String displayName;
        String lore;
        int customModel;

        switch (tier) {
            case 3 -> {
                material = Material.PAPER;
                name = "Blank Eternal Teleport Scroll";
                displayName = ChatColor.LIGHT_PURPLE + name;
                customModel = 10003;
            }
            case 2 -> {
                material = Material.PAPER;
                name = "Blank Enhanced Teleport Scroll";
                displayName = ChatColor.AQUA + name;
                customModel = 10002;
            }
            default -> {
                material = Material.PAPER;
                name = "Blank Teleport Scroll";
                displayName = ChatColor.YELLOW + name;
                customModel = 10001;
            }
        }

        lore = ChatColor.DARK_GREEN + name;

        ItemStack item = new ItemStack(material);
        ItemHelper.setItemName(item, displayName);
        ItemHelper.setItemLore(item, lore);
        ItemHelper.setCustomModel(item, customModel);

        ItemMeta Meta = item.getItemMeta();
        assert Meta != null;

        Meta.addItemFlags(
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_POTION_EFFECTS
        );
        item.setItemMeta(Meta);
        item.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 1);

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setBoolean("is_teleport_scroll", true);
        nbtItem.setInteger("tier", tier);

        if (tier == 3) {
            // prevent stacking
            nbtItem.setString("t3_scroll_uuid", UUID.randomUUID().toString());
        }

        return nbtItem.getItem();
    }

    public static ItemStack createTeleportScrollWithCoords(ItemStack item, String world, int x, int y, int z, float yaw)
    {

        NBTItem nbtItem = new NBTItem(item.clone());

        int tier = nbtItem.getInteger("tier");
        String name;
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
                name = ChatColor.AQUA + "Enhanced Teleport Scroll";
                customModel = 10005;
            }
            case 3 -> {
                name = ChatColor.LIGHT_PURPLE + "Eternal Teleport Scroll";
                customModel = 10006;
            }
            default -> {
                name = ChatColor.YELLOW + "Teleport Scroll";
                customModel = 10004;
            }
        }

        String lore = String.format(ChatColor.WHITE + "%s X %s Y %s Z %s %s;%s", world, x, y, z, direction, name);
        nbtItem.setString("world", world);
        nbtItem.setInteger("x", x);
        nbtItem.setInteger("y", y);
        nbtItem.setInteger("z", z);
        nbtItem.setFloat("yaw", yaw);

        ItemStack outputItem = nbtItem.getItem();
        ItemHelper.setItemLore(outputItem, lore);
        ItemHelper.setItemName(outputItem, name);
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
        String name;
        int customModel;

        switch (tier) {
            case 2 -> {
                name = ChatColor.AQUA + "Enhanced Bed Teleport Scroll";
                customModel = 10005;
            }
            case 3 -> {
                name = ChatColor.LIGHT_PURPLE + "Eternal Bed Teleport Scroll";
                customModel = 10006;
            }
            default -> {
                name = ChatColor.YELLOW + "Bed Teleport Scroll";
                customModel = 10004;
            }
        }

        ItemHelper.setItemName(bedScroll, name);
        ItemHelper.setItemLore(bedScroll, ChatColor.WHITE + "Teleports you to your respawn point");
        ItemHelper.setCustomModel(bedScroll, customModel);
        bedScroll.removeEnchantment(Enchantment.PROTECTION_EXPLOSIONS);

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
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_POTION_EFFECTS
        );
        lifesaver.setItemMeta(Meta);
        lifesaver.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);

        NBTItem nbtItem = new NBTItem(lifesaver);
        nbtItem.setBoolean("is_lifesaver", true);
        // prevent stacking
        nbtItem.setString("lifesaver_uuid", UUID.randomUUID().toString());
        return nbtItem.getItem();
    }

    public static ItemStack createTeleportBook()
    {
        ItemStack teleportBook = new ItemStack(Material.WRITTEN_BOOK);
        ItemHelper.setItemName(teleportBook, ChatColor.LIGHT_PURPLE + "Empty Teleport Book");

        ItemMeta Meta = teleportBook.getItemMeta();
        assert Meta != null;

        Meta.addItemFlags(
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_POTION_EFFECTS
        );
        teleportBook.setItemMeta(Meta);

        BookMeta bookMeta = (BookMeta) teleportBook.getItemMeta();

        BaseComponent[] basePage = TeleportBookHelper.getBasePage().create();

        bookMeta.spigot().addPage(basePage);
        bookMeta.setTitle("Teleport Book");
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
