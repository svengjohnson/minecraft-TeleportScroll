package io.sjohnson.teleportscroll.objects;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.sjohnson.teleportscroll.objects.json.JsonBedTeleportScroll;
import io.sjohnson.teleportscroll.objects.model.CustomModel;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class BedTeleportScroll extends BaseItem {
    private int id;
    private int tier;
    private int count;
    private String name;
    private String displayName;

    private BedTeleportScroll(int tier) {
        super(Material.PAPER);

        this.tier = tier;
        this.count = 1;
        this.name = getName(tier);
        this.displayName = getName(tier);

        setModel();
        setLore(getLore());
        setDisplayName(displayName);
        setAmount(count);
        addItemFlags(false);
    }

    private BedTeleportScroll(int tier, int count, String name) {
        super(Material.PAPER);

        this.tier = tier;
        this.count = count;
        this.name = getName(tier);
        this.displayName = name;

        setModel();
        setLore(getLore());
        setDisplayName(displayName);
        setAmount(count);
        addItemFlags(false);
    }

    public BedTeleportScroll(ItemStack item, int id) {
        super(item);

        NBTItem nbtScroll = new NBTItem(item);
        ItemMeta meta = getItemMeta();
        assert meta != null;

        this.id = id;
        this.tier = nbtScroll.getInteger(NBTFields.TIER);
        this.count = getAmount();
        this.name = getName(tier);
        this.displayName = meta.getDisplayName();
    }

    public static ItemStack create(int tier) {
        return new BedTeleportScroll(tier).toItem();
    }

    public static ItemStack create(int tier, int count, String name) {
        return new BedTeleportScroll(tier, count, name).toItem();
    }

    public static JsonBedTeleportScroll getObject(ItemStack item, int id) {
        BedTeleportScroll bedTeleportScroll = new BedTeleportScroll(item, id);

        return new JsonBedTeleportScroll(
                bedTeleportScroll.getId(),
                bedTeleportScroll.getCount(),
                bedTeleportScroll.getTier(),
                bedTeleportScroll.getName(),
                bedTeleportScroll.getDisplayName()
        );
    }

    private String getLore() {
        return String.format("%s;" + ChatColor.WHITE + "Teleports you to your respawn point", name);
    }

    public void setModel() {
        switch (tier) {
            case 1 -> {
                setCustomModel(CustomModel.BED_SCROLL_T1);
            }
            case 2 -> {
                setCustomModel(CustomModel.BED_SCROLL_T2);
            }
            case 3 ->{
                setCustomModel(CustomModel.BED_SCROLL_T3);
            }
        }
    }

    public int getId() {
        return id;
    }

    public int getTier() {
        return tier;
    }

    public int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    private ItemStack toItem() {
        NBTItem nbtItem = new NBTItem(this);

        nbtItem.setBoolean(NBTFields.IS_TELEPORT_SCROLL, true);
        nbtItem.setInteger(NBTFields.TIER, tier);
        nbtItem.setBoolean(NBTFields.TELEPORT_TO_BED, true);

        if (tier == 3) {
            // prevent stacking
            nbtItem.setString(NBTFields.T3_SCROLL_UUID, UUID.randomUUID().toString());
        }

        return nbtItem.getItem();
    }

    public String getName(int tier)
    {
        switch (tier) {
            case 2 -> {
                return getTierFormatting(tier) + "Enhanced Bed Teleport Scroll";
            }
            case 3 -> {
                return getTierFormatting(tier) + "Eternal Bed Teleport Scroll";
            }
            default -> {
                return getTierFormatting(tier) + "Bed Teleport Scroll";
            }
        }
    }

    private static class NBTFields {
        private NBTFields() {}
        public static final String IS_TELEPORT_SCROLL = "is_teleport_scroll";
        public static final String TIER = "tier";
        public static final String T3_SCROLL_UUID = "t3_scroll_uuid";
        public static final String TELEPORT_TO_BED = "teleport_to_bed";
    }
}
