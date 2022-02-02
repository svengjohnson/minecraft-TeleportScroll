package io.sjohnson.teleportscroll.listeners;

import io.sjohnson.teleportscroll.handlers.ActivateTeleportScrollHandler;
import io.sjohnson.teleportscroll.helpers.ItemHelper;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class RightClickItemListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) throws InterruptedException {
        Player player = event.getPlayer();
        Action action = event.getAction();

        // The action has to be a right click
        if (!(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        // Don't run for off-hand
        if (Objects.equals(event.getHand(), EquipmentSlot.OFF_HAND)) {
            return;
        }

        ItemStack mainHand = player.getInventory().getItemInMainHand();

        // Don't run anything for air;
        if (mainHand.getType() == Material.AIR) {
            return;
        }

        if (ItemHelper.isTeleportScroll(mainHand)) {
            new ActivateTeleportScrollHandler(player, mainHand);
            event.setCancelled(true);
        }
    }
}

