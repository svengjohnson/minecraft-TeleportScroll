package io.sjohnson.teleportscroll.objects;

import io.sjohnson.teleportscroll.objects.model.CustomModelData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class BaseItem {
    protected ItemStack item;

    protected BaseItem(Material material) {
        this.item = new ItemStack(material);
    }

    protected BaseItem(ItemStack itemStack) {
        this.item = itemStack;
    }

    protected void setDisplayName(String displayName) {
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.setDisplayName(displayName);

        item.setItemMeta(meta);
    }

    protected void setCustomModel(int model)
    {
        ItemMeta meta = item.getItemMeta();

        assert meta != null;

        meta.setCustomModelDataComponent(CustomModelData.fromModelId(model));

        item.setItemMeta(meta);
    }

    protected void setLore(String lore)
    {
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.setLore(formatLore(lore));

        item.setItemMeta(meta);
    }

    protected void addItemFlags(boolean withVanishingCurse) {
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

        if (withVanishingCurse) {
            Meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(Meta);
    }

    protected String getTierFormatting(int tier)
    {
        return getTierFormatting(tier, false);
    }

    protected String getTierFormatting(int tier, boolean bold)
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

    private String formatLinkName(int tier, String text)
    {
          switch (tier) {
            case 2 -> {
                return ChatColor.DARK_BLUE + text;
            }
            case 3 -> {
                return ChatColor.DARK_PURPLE + text;
            }
            default -> {
                return ChatColor.GOLD + text;
            }
        }
    }

    protected String formatLinkName(int tier, String displayName, int count)
    {
        String truncatedName = truncate(ChatColor.stripColor(displayName), tier);

        return formatLinkName(tier, String.format("%s (%s)", truncatedName, count));
    }

    private String truncate(String value, int tier) {
        int maxlength;

        if (tier == 3) {
            maxlength = 19;
        } else {
            maxlength = 15;
        }

        if (value.length() > maxlength) {
            return value.substring(0, maxlength - 2) + "...";
        } else {
            return value;
        }
    }

    protected String formatTextWithTierFormatting(int tier, boolean bold, String text) {
        return getTierFormatting(tier, bold) + text;
    }

    private ArrayList<String> formatLore(String lore)
    {
        String[] loreText = lore.split(";");

        return new ArrayList<>(Arrays.asList(loreText));
    }
}
