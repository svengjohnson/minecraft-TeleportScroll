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

        switch (tier) {
            case 3 -> {
                material = Material.FLOWER_BANNER_PATTERN;
                name = "Blank Eternal Teleport Scroll";
                displayName = ChatColor.YELLOW + name;
            }
            case 2 -> {
                material = Material.PAPER;
                name = "Blank Enhanced Teleport Scroll";
                displayName = ChatColor.YELLOW + name;
            }
            default -> {
                material = Material.PAPER;
                name = "Blank Teleport Scroll";
                displayName = ChatColor.WHITE + name;
            }
        }

        lore = ChatColor.DARK_GREEN + name;

        ItemStack item = new ItemStack(material);
        ItemHelper.setItemName(item, displayName);
        ItemHelper.setItemLore(item, lore);

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

        return nbtItem.getItem();
    }

    public static ItemStack createTeleportScrollWithCoords(ItemStack item, String world, int x, int y, int z)
    {

        NBTItem nbtItem = new NBTItem(item.clone());

        int tier = nbtItem.getInteger("tier");
        String name = switch (tier) {
            case 2 -> ChatColor.YELLOW + "Enhanced Teleport Scroll";
            case 3 -> ChatColor.YELLOW + "" + ChatColor.BOLD + "Eternal Teleport Scroll";
            default -> ChatColor.AQUA + "Teleport Scroll";
        };

        String lore = String.format(ChatColor.WHITE + "%s X %s Y %s Z %s;%s", world, x, y, z, name);
        nbtItem.setString("world", world);
        nbtItem.setInteger("x", x);
        nbtItem.setInteger("y", y);
        nbtItem.setInteger("z", z);

        ItemStack outputItem = nbtItem.getItem();
        ItemHelper.setItemLore(outputItem, lore);
        ItemHelper.setItemName(outputItem, name);
        outputItem.setAmount(1);

        return outputItem;
    }

    public static ItemStack createBedTeleportScroll(int tier)
    {
        ItemStack bedScroll = createTeleportScroll(tier);

        String name = switch (tier) {
            case 2 -> ChatColor.YELLOW + "Enhanced Bed Teleport Scroll";
            case 3 -> ChatColor.YELLOW + "" + ChatColor.BOLD + "Eternal Bed Teleport Scroll";
            default -> ChatColor.AQUA + "Bed Teleport Scroll";
        };

        ItemHelper.setItemName(bedScroll, name);
        ItemHelper.setItemLore(bedScroll, ChatColor.LIGHT_PURPLE + "Teleports you to your respawn point");

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
        nbtItem.setString("lifesaver_uuid", UUID.randomUUID().toString()); // this should make the item non-stackable
        return nbtItem.getItem();
    }
}
