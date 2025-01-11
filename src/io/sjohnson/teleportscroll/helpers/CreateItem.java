package io.sjohnson.teleportscroll.helpers;

import de.tr7zw.changeme.nbtapi.*;
import io.sjohnson.teleportscroll.objects.BedTeleportScroll;
import io.sjohnson.teleportscroll.objects.BlankLocationTeleportScroll;
import io.sjohnson.teleportscroll.objects.TeleportBook;
import io.sjohnson.teleportscroll.objects.LocationTeleportScroll;
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
        return BlankLocationTeleportScroll.create(tier);
    }

    public static ItemStack createTeleportScrollWithCoords(ItemStack item, String world, double x, double y, double z, float yaw)
    {
        return LocationTeleportScroll.create(item, world, x, y, z, yaw);
    }

    public static ItemStack createCustomTeleportScroll(int count, int tier, String name, String world, double x, double y, double z, float yaw)
    {
        return LocationTeleportScroll.create(tier, world, x, y, z, yaw, name, count);
    }

    public static ItemStack createCustomBedTeleportScroll(int count, int tier, String name)
    {
        return BedTeleportScroll.create(tier, count, name);
    }

    public static ItemStack createBedTeleportScroll(int tier)
    {
        return BedTeleportScroll.create(tier);
    }

    public static ItemStack createEmptyTeleportBook(boolean withVanishingCurse)
    {
        return TeleportBook.create(withVanishingCurse);
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
}
