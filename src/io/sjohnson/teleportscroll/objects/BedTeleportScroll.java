package io.sjohnson.teleportscroll.objects;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.sjohnson.teleportscroll.objects.json.JsonBedTeleportScroll;
import io.sjohnson.teleportscroll.objects.model.CustomModel;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class BedTeleportScroll extends BaseItem implements ItemizableTeleportScroll {
    private int id;
    private int tier;
    private int count;
    private String name;
    private String displayName;

    public BedTeleportScroll(int tier) {
        super(Material.PAPER);

        this.tier = tier;
        this.count = 1;
        this.name = getName();
        this.displayName = getName();

        setModel();
        setLore(getLore());
        setDisplayName(displayName);
        addItemFlags(false);

        item.setAmount(count);
    }

    public BedTeleportScroll(int tier, int count, String name) {
        super(Material.PAPER);

        this.tier = tier;
        this.count = count;
        this.name = getName();
        this.displayName = name;

        setModel();
        setLore(getLore());
        setDisplayName(displayName);
        addItemFlags(false);

        item.setAmount(count);
    }

    public BedTeleportScroll(int id, int tier, int count, String name) {
        super(Material.PAPER);

        this.id = id;
        this.tier = tier;
        this.count = count;
        this.name = getName();
        this.displayName = name;

        setModel();
        setLore(getLore());
        setDisplayName(displayName);
        addItemFlags(false);

        item.setAmount(count);
    }

    public BedTeleportScroll(ItemStack item, int id) {
        super(item);

        NBTItem nbtScroll = new NBTItem(item);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        this.id = id;
        this.tier = nbtScroll.getInteger(NBTFields.TIER);
        this.count = item.getAmount();
        this.name = getName();
        this.displayName = meta.getDisplayName();
    }

    public static ItemStack create(int tier) {
        return new BedTeleportScroll(tier).toItem();
    }

    public static ItemStack create(int tier, int count, String name) {
        return new BedTeleportScroll(tier, count, ChatColor.stripColor(name)).toItem();
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

    public void setCount(int count) {
        this.count = count;
        item.setAmount(count);
    }

    public int getCount() {
        return count;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BaseComponent[] toLink() {
        String name = formatLinkName(tier, displayName, count);
        String tierName = getName();

        String altText = String.format("%s\n%s\n" + ChatColor.WHITE + "Teleports you to your respawn point", displayName, tierName);

        return new ComponentBuilder()
                .append(name)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teleportbook teleportTo " + id))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(altText).create()))
                .append("\n")
                .create();
    }

    public ItemStack toItem() {
        NBTItem nbtItem = new NBTItem(item);

        nbtItem.setBoolean(NBTFields.IS_TELEPORT_SCROLL, true);
        nbtItem.setInteger(NBTFields.TIER, tier);
        nbtItem.setBoolean(NBTFields.TELEPORT_TO_BED, true);

        if (tier == 3) {
            // prevent stacking
            nbtItem.setString(NBTFields.T3_SCROLL_UUID, UUID.randomUUID().toString());
        }

        return nbtItem.getItem();
    }

    public String getName()
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

    public boolean toBed() {
        return true;
    }
}
