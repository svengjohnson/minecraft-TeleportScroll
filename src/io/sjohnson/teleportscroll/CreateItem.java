package io.sjohnson.teleportscroll;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("DuplicatedCode")
public class CreateItem {
    public static ItemStack teleportScroll(String name, String lore)
    {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta Meta = item.getItemMeta();
        assert Meta != null;
        Meta.setDisplayName(name);
        Meta.setLore(CreateItem.formatLore(lore));

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
        nbtItem.setInteger("tier", 1);

        return nbtItem.getItem();
    }

    public static ItemStack enhancedTeleportScroll(String name, String lore)
    {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta Meta = item.getItemMeta();
        assert Meta != null;
        Meta.setDisplayName(name);
        Meta.setLore(CreateItem.formatLore(lore));

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
        nbtItem.setInteger("tier", 2);

        return nbtItem.getItem();
    }

    public static ItemStack eternalTeleportScroll(String name, String lore)
    {
        ItemStack item = new ItemStack(Material.GLOBE_BANNER_PATTERN);
        ItemMeta Meta = item.getItemMeta();
        assert Meta != null;
        Meta.setDisplayName(name);
        Meta.setLore(CreateItem.formatLore(lore));

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
        nbtItem.setInteger("tier", 3);

        return nbtItem.getItem();
    }

    private static ArrayList<String> formatLore(String lore)
    {
        String[] loreText = lore.split(";");
        ArrayList<String> Lore = new ArrayList<>();

        Lore.addAll(Arrays.asList(loreText));

        return Lore;
    }
}
