package io.sjohnson.teleportscroll.handlers;

import io.sjohnson.teleportscroll.helpers.CreateItem;
import io.sjohnson.teleportscroll.helpers.ItemHelper;
import io.sjohnson.teleportscroll.helpers.TeleportHelper;
import io.sjohnson.teleportscroll.objects.ItemizableTeleportScroll;
import io.sjohnson.teleportscroll.utils.TeleportScrollUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ActivateTeleportScrollHandler {
    public ActivateTeleportScrollHandler(Player player, ItemStack item) throws InterruptedException {
        if (!ItemHelper.isTeleportScroll(item)) {
            return;
        }

        if (!player.isSneaking()) {
            player.sendMessage(ChatColor.YELLOW + "You must be crouching to use teleport scrolls");
            return;
        }

        if (!ItemHelper.isBlankTeleportScroll(item)) {
            ItemizableTeleportScroll teleportScroll = TeleportScrollUtils.createFromItemStack(item);

            Location targetLocation = TeleportHelper.getDestination(player, teleportScroll);

            if (TeleportHelper.canTeleport(player, targetLocation, teleportScroll, false, true)) {
                if (teleportScroll.getTier() < 3) {
                    item.setAmount(item.getAmount() - 1);
                }

                TeleportHelper.teleport(player, targetLocation, teleportScroll, false, true);
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
}
