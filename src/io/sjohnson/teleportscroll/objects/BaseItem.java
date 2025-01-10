package io.sjohnson.teleportscroll.objects;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class BaseItem extends ItemStack {

    public BaseItem(Material material) {
        super(material);
    }

    public void setDisplayName(String displayName) {
        ItemMeta meta = getItemMeta();

        assert meta != null;
        meta.setDisplayName(displayName);

        setItemMeta(meta);
    }

    public String getDisplayName() {
        ItemMeta meta = getItemMeta();

        assert meta != null;

        return meta.getDisplayName();
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

    private ArrayList<String> formatLore(String lore)
    {
        String[] loreText = lore.split(";");

        return new ArrayList<>(Arrays.asList(loreText));
    }
}
