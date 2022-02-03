package io.sjohnson.teleportscroll.helpers;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
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
                displayName = ChatColor.YELLOW + name;
                customModel = 10003;
            }
            case 2 -> {
                material = Material.PAPER;
                name = "Blank Enhanced Teleport Scroll";
                displayName = ChatColor.YELLOW + name;
                customModel = 10002;
            }
            default -> {
                material = Material.PAPER;
                name = "Blank Teleport Scroll";
                displayName = ChatColor.WHITE + name;
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

    public static ItemStack createTeleportScrollWithCoords(ItemStack item, String world, int x, int y, int z)
    {

        NBTItem nbtItem = new NBTItem(item.clone());

        int tier = nbtItem.getInteger("tier");
        String name;
        int customModel;

        switch (tier) {
            case 2 -> {
                name = ChatColor.YELLOW + "Enhanced Teleport Scroll";
                customModel = 10005;
            }
            case 3 -> {
                name = ChatColor.YELLOW + "" + ChatColor.BOLD + "Eternal Teleport Scroll";
                customModel = 10006;
            }
            default -> {
                name = ChatColor.AQUA + "Teleport Scroll";
                customModel = 10004;
            }
        }

        String lore = String.format(ChatColor.WHITE + "%s X %s Y %s Z %s;%s", world, x, y, z, name);
        nbtItem.setString("world", world);
        nbtItem.setInteger("x", x);
        nbtItem.setInteger("y", y);
        nbtItem.setInteger("z", z);

        ItemStack outputItem = nbtItem.getItem();
        ItemHelper.setItemLore(outputItem, lore);
        ItemHelper.setItemName(outputItem, name);
        ItemHelper.setCustomModel(outputItem, customModel);
        outputItem.setAmount(1);

        return outputItem;
    }

    public static ItemStack createBedTeleportScroll(int tier)
    {
        ItemStack bedScroll = createTeleportScroll(tier);
        String name;
        int customModel;

        switch (tier) {
            case 2 -> {
                name = ChatColor.YELLOW + "Enhanced Bed Teleport Scroll";
                customModel = 10005;
            }
            case 3 -> {
                name = ChatColor.YELLOW + "" + ChatColor.BOLD + "Eternal Bed Teleport Scroll";
                customModel = 10006;
            }
            default -> {
                name = ChatColor.AQUA + "Bed Teleport Scroll";
                customModel = 10004;
            }
        }

        ItemHelper.setItemName(bedScroll, name);
        ItemHelper.setItemLore(bedScroll, ChatColor.LIGHT_PURPLE + "Teleports you to your respawn point");
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
}
