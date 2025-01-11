package io.sjohnson.teleportscroll.handlers;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.sjohnson.teleportscroll.helpers.CreateItem;
import io.sjohnson.teleportscroll.helpers.ItemHelper;
import io.sjohnson.teleportscroll.helpers.TeleportHelper;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ActivateTeleportScrollHandler {
    public ActivateTeleportScrollHandler(Player player, ItemStack item) throws InterruptedException {
        NBTItem nbtItem = new NBTItem(item);

        if (!player.isSneaking()) {
            player.sendMessage(ChatColor.YELLOW + "You must be crouching to use teleport scrolls");
            return;
        }

        if (ItemHelper.isBedTeleportScroll(item) || nbtItem.hasKey("world")) {
            int tier = nbtItem.getInteger("tier");

            Location targetLocation = TeleportHelper.getDestinationForTeleportScroll(player, nbtItem, item);

            if (TeleportHelper.canTeleport(player, targetLocation, tier, false, true)) {
                if (tier < 3) {
                    item.setAmount(item.getAmount() - 1);
                }

                TeleportHelper.teleport(player, targetLocation, tier, false, true);
            }
        } else {
            this.storeLocation(player, item);
        }
    }

    private void storeLocation(Player player, ItemStack stack) {
        if (ItemHelper.getEmptyInventorySlots(player.getInventory()) == 0) {
            player.sendMessage(ChatColor.RED + "Need at least 1 free inventory space");
            return;
        }

        World world = player.getWorld();
        Location location = player.getLocation();

        String w = world.getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();

        ItemStack teleportScroll;

        teleportScroll = CreateItem.createTeleportScrollWithCoords(stack, w, x, y, z, yaw);

        player.getInventory().addItem(teleportScroll);
        stack.setAmount(stack.getAmount() - 1);
    }

    private int getX(int x)
    {
        if (x < 0) {
            return x - 1;
        } else {
            return x;
        }
    }

    private int getZ(int z)
    {
        if (z < 0) {
            return z - 1;
        } else {
            return z;
        }
    }
}
