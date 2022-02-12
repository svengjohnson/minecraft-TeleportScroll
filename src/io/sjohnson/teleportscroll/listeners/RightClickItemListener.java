package io.sjohnson.teleportscroll.listeners;

import io.sjohnson.teleportscroll.handlers.ActivateTeleportScrollHandler;
import io.sjohnson.teleportscroll.helpers.ItemHelper;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.EnderChest;
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
        Block clickedBlock = event.getClickedBlock();
        assert clickedBlock != null;

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (!ItemHelper.isTeleportScroll(mainHand)) {
            return;
        }

        // Don't run for off-hand
        if (Objects.equals(event.getHand(), EquipmentSlot.OFF_HAND)) {
            return;
        }

        // The action has to be a right click
        if (!(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        // If the target block is a Container, (e.g. Chest), do nothing
        if (clickedBlock.getState() instanceof Container) {
            return;
        }

        // Do nothing for Ender Chest either (which apparently is not a Container)
        if (clickedBlock.getState() instanceof EnderChest) {
            return;
        }

        // Do nothing for Crafting Table too
        if (clickedBlock.getType() == Material.CRAFTING_TABLE) {
            return;
        }

        new ActivateTeleportScrollHandler(player, mainHand);
        event.setCancelled(true);
    }
}

