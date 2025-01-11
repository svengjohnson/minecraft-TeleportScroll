package io.sjohnson.teleportscroll.objects;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.sjohnson.teleportscroll.objects.json.JsonLocationTeleportScroll;
import io.sjohnson.teleportscroll.objects.model.CustomModel;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class LocationTeleportScroll extends BaseItem {
    private int id;
    private int tier;
    private String name;
    private String displayName;
    private String world;
    private int x;
    private int y;
    private int z;
    private float yaw;
    private int count;

    private LocationTeleportScroll(ItemStack blankScroll, String world, double x, double y, double z, float yaw) {
        super(Material.PAPER);

        BlankLocationTeleportScroll blank = new BlankLocationTeleportScroll(blankScroll);

        this.world = world;
        this.x = calculateX(x);
        this.y = calculateY(y);
        this.z = calculateZ(z);
        this.yaw = calculateYaw(yaw);
        this.tier = blank.getTier();
        this.displayName = getName(true);
        this.name = getName(false);

        setModel();
        setLore(getLore());
        setDisplayName(displayName);
        addUnsafeEnchantment(Enchantment.PROTECTION, 1);
        addItemFlags(false);
        setAmount(1);
    }

    private LocationTeleportScroll(int tier, String world, double x, double y, double z, float yaw, String name, int count) {
        super(Material.PAPER);

        this.world = world;
        this.x = calculateX(x);
        this.y = calculateY(y);
        this.z = calculateZ(z);
        this.yaw = calculateYaw(yaw);
        this.tier = tier;
        this.displayName = getTierFormatting(tier, true) + name;
        this.count = count;
        this.name = getName(false);

        setModel();
        setLore(getLore());
        setDisplayName(displayName);
        addUnsafeEnchantment(Enchantment.PROTECTION, 1);
        addItemFlags(false);
        setAmount(count);
    }

    public LocationTeleportScroll(ItemStack item, int id) {
        super(item);

        NBTItem nbtScroll = new NBTItem(item);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        this.id = id;
        this.tier = nbtScroll.getInteger(NBTFields.TIER);
        this.name = nbtScroll.getString(NBTFields.NAME);
        this.displayName = meta.getDisplayName();
        this.world = nbtScroll.getString(NBTFields.WORLD);
        this.x = nbtScroll.getInteger(NBTFields.X);
        this.y = nbtScroll.getInteger(NBTFields.Y);
        this.z = nbtScroll.getInteger(NBTFields.Z);
        this.yaw = nbtScroll.getFloat(NBTFields.YAW);
        this.count = item.getAmount();
    }

    public static ItemStack create(ItemStack blankScroll, String world, double x, double y, double z, float yaw) {
        return new LocationTeleportScroll(blankScroll, world, x, y, z, yaw).toItem();
    }

    public static ItemStack create(int tier, String world, double x, double y, double z, float yaw, String name, int count) {
        return new LocationTeleportScroll(tier, world, x, y, z, yaw, name, count).toItem();
    }

    public static JsonLocationTeleportScroll getObject(ItemStack item, int id) {
        LocationTeleportScroll locationScroll = new LocationTeleportScroll(item, id);

        return new JsonLocationTeleportScroll(
                locationScroll.getId(),
                locationScroll.getCount(),
                locationScroll.getTier(),
                locationScroll.getName(),
                locationScroll.getDisplayName(),
                locationScroll.getWorld(),
                locationScroll.getX(),
                locationScroll.getY(),
                locationScroll.getZ(),
                locationScroll.getYaw()
        );
    }

    public int getId() {
        return id;
    }

    public int getTier() {
        return tier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getName() {
        return name;
    }

    public String getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public int getCount() {
        return count;
    }

    private float calculateYaw(float trueYaw)
    {
        if (trueYaw >= 135 || trueYaw <= -135) {
            // facing north
            return 180;
        }

        if (trueYaw >= -135 && trueYaw <= - 45) {
            // facing east
            return -90;
        }

        if (trueYaw > -45 && trueYaw <= 45) {
            // facing south
            return 0;
        }

        // facing west
        return 90;
    }

    private int calculateX(double x)
    {
        return (int) x;
    }

    private int calculateY(double y) {
        return (int) Math.round(y);
    }

    private int calculateZ(double z)
    {
        return (int) z;
    }

    private void setModel() {
        switch (tier) {
            case 1 -> {
                setCustomModel(CustomModel.SCROLL_T1);
            }
            case 2 -> {
                setCustomModel(CustomModel.SCROLL_T2);
            }
            case 3 -> {
                setCustomModel(CustomModel.SCROLL_T3);
            }
        }
    }

    private ItemStack toItem() {
        NBTItem nbtItem = new NBTItem(this);

        nbtItem.setBoolean(NBTFields.IS_TELEPORT_SCROLL, true);
        nbtItem.setInteger(NBTFields.TIER, tier);

        nbtItem.setString(NBTFields.WORLD, world);
        nbtItem.setInteger(NBTFields.X, x);
        nbtItem.setInteger(NBTFields.Y, y);
        nbtItem.setInteger(NBTFields.Z, z);
        nbtItem.setFloat(NBTFields.YAW, yaw);
        nbtItem.setString(NBTFields.NAME, name);
        nbtItem.setBoolean(NBTFields.TELEPORT_TO_BED, false);

        if (tier == 3) {
            // prevent stacking
            nbtItem.setString(NBTFields.T3_SCROLL_UUID, UUID.randomUUID().toString());
        }

        return nbtItem.getItem();
    }

    private String getLore() {
        return String.format("%s;" + ChatColor.WHITE + "%s X %s Y %s Z %s %s", name, world, x, y, z, getDirection());
    }

    private String getDirection() {
        if (yaw == 180) {
            return Direction.N;
        } else if (yaw == -90) {
            return Direction.E;
        } else if (yaw == 90) {
            return Direction.W;
        } else {
            return Direction.S;
        }
    }

    private String getName(boolean bold)
    {
        switch (tier) {
            case 2 -> {
                return formatTextWithTierFormatting(tier, bold, "Enhanced Teleport Scroll");
            }
            case 3 -> {
                return formatTextWithTierFormatting(tier, bold, "Eternal Teleport Scroll");}
            default -> {
                return formatTextWithTierFormatting(tier, bold, "Teleport Scroll");
            }
        }
    }

    private static class NBTFields {
        private NBTFields() {}
        public static final String IS_TELEPORT_SCROLL = "is_teleport_scroll";
        public static final String TIER = "tier";
        public static final String WORLD = "world";
        public static final String X = "x";
        public static final String Y = "y";
        public static final String Z = "z";
        public static final String YAW = "yaw";
        public static final String T3_SCROLL_UUID = "t3_scroll_uuid";
        public static final String NAME = "name";
        public static final String TELEPORT_TO_BED = "teleport_to_bed";
    }

    private static class Direction {
        private Direction() {}
        public static final String N = "N";
        public static final String E = "E";
        public static final String W = "W";
        public static final String S = "S";
    }
}
