package io.sjohnson.teleportscroll.objects;

import io.sjohnson.teleportscroll.objects.model.CustomModelData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class BaseItem extends ItemStack {

    public BaseItem(Material material) {
        super(material);
    }

    public BaseItem(ItemStack itemStack) {
        super(itemStack);
    }

    public void setDisplayName(String displayName) {
        ItemMeta meta = getItemMeta();

        assert meta != null;
        meta.setDisplayName(displayName);

        setItemMeta(meta);
    }

    public void setCustomModel(int model)
    {
        ItemMeta meta = getItemMeta();

        assert meta != null;

        meta.setCustomModelDataComponent(CustomModelData.fromModelId(model));

        setItemMeta(meta);
    }

    public void setLore(String lore)
    {
        ItemMeta meta = getItemMeta();

        assert meta != null;
        meta.setLore(formatLore(lore));

        setItemMeta(meta);
    }

    public void addItemFlags(boolean withVanishingCurse) {
        ItemMeta Meta = getItemMeta();
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

        setItemMeta(Meta);
    }

    public String getTierFormatting(int tier)
    {
        return getTierFormatting(tier, false);
    }

    public String getTierFormatting(int tier, boolean bold)
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

    public String formatTextWithTierFormatting(int tier, boolean bold, String text) {
        return getTierFormatting(tier, bold) + text;
    }

    private ArrayList<String> formatLore(String lore)
    {
        String[] loreText = lore.split(";");

        return new ArrayList<>(Arrays.asList(loreText));
    }
}
