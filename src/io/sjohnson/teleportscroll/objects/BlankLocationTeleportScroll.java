package io.sjohnson.teleportscroll.objects;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.sjohnson.teleportscroll.objects.model.CustomModel;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class BlankLocationTeleportScroll extends BaseItem {
    private int tier;

    private BlankLocationTeleportScroll(int tier) {
        super(Material.PAPER);

        this.tier = tier;

        setModel();
        setDisplayName(getName(tier));
        setLore(getName(tier));
        addItemFlags(false);

        item.addUnsafeEnchantment(Enchantment.PROTECTION, 1);
    }

    public BlankLocationTeleportScroll(ItemStack itemStack) {
        super(itemStack);

        NBTItem nbtItem = new NBTItem(itemStack);
        this.tier = nbtItem.getInteger("tier");
    }

    public int getTier() {
        return tier;
    }

    public static ItemStack create(int tier) {
        return new BlankLocationTeleportScroll(tier).toItem();
    }

    public ItemStack toItem() {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setBoolean(NBTFields.IS_TELEPORT_SCROLL, true);
        nbtItem.setInteger(NBTFields.TIER, tier);

        return nbtItem.getItem();
    }

    private void setModel() {
        switch (tier) {
            case 1 -> {
                setCustomModel(CustomModel.BLANK_SCROLL_T1);
            }
            case 2 -> {
                setCustomModel(CustomModel.BLANK_SCROLL_T2);
            }
            case 3 -> {
                setCustomModel(CustomModel.BLANK_SCROLL_T3);
            }
        }
    }

    public String getName(int tier)
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
}
