package io.sjohnson.teleportscroll.listeners;

import de.tr7zw.nbtapi.NBTItem;
import io.sjohnson.teleportscroll.helpers.CreateItem;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RightClickListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) throws InterruptedException {
        Player player = event.getPlayer();
        Action action = event.getAction();

        // The action has to be a right click
        if (!(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        // don't run for off-hand
        if (Objects.equals(event.getHand(), EquipmentSlot.OFF_HAND)) {
            return;
        }

        PlayerInventory inventory = player.getInventory();
        ItemStack stack = inventory.getItemInMainHand();

        if (stack.getType() != Material.AIR) {
            NBTItem nbtItem = new NBTItem(stack);

            if (!nbtItem.hasKey("is_teleport_scroll")) {
                return;
            }

            if (!player.isSneaking()) {
                player.sendMessage(ChatColor.YELLOW + "You must be crouching to use teleport scrolls");
                return;
            }

            if (nbtItem.hasKey("world")) {
                this.teleportPlayer(player, nbtItem, stack);
            }
            else {
                this.storeLocation(player, stack);
            }
        }
    }

    private void teleportPlayer(Player player, NBTItem nbtItem, ItemStack stack) throws InterruptedException {
        int tier = nbtItem.getInteger("tier");
        if (!this.canTeleport(player, nbtItem)) {
            return;
        }

        String world = nbtItem.getString("world");
        int x = nbtItem.getInteger("x");
        int y = nbtItem.getInteger("y");
        int z = nbtItem.getInteger("z");

        World tpWorld = Bukkit.getWorld(world);
        Location location = new Location(tpWorld, x, y, z);

        if (tier < 3) {
            stack.setAmount(stack.getAmount() - 1);
        }

        //display particles
        TeleportParticles(player);

        // play sound
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, (float) 0.5, 2);

        // send message
        player.sendMessage(ChatColor.AQUA + "Teleporting...");

        // timeout for 1 second
        TimeUnit.SECONDS.sleep(1);

        // actually teleport
        player.teleport(location);

        // display particles at the destination
        TeleportParticles(player);
    }

    private boolean canTeleport(Player player, NBTItem nbtItem)
    {
        int tier = nbtItem.getInteger("tier");

        if (this.alreadyThere(player.getLocation(), nbtItem)) {
            player.sendMessage(ChatColor.YELLOW + "You are already here!");
            return false;
        }

        if (!this.sameWorld(player, nbtItem) && tier < 2) {
            player.sendMessage(ChatColor.YELLOW + "Can't teleport to a different realm with this scroll!");
            return false;
        }

        if (!player.isOnGround()) {
            player.sendMessage(ChatColor.YELLOW + "You must be on the ground to teleport");
            return false;
        }

        return true;
    }

    private boolean sameWorld(Player player, NBTItem nbtItem)
    {
        return player.getWorld().getName().equals(nbtItem.getString("world"));
    }

    private boolean alreadyThere(Location currentLocation, NBTItem nbtItem)
    {
        String currentWorld = currentLocation.getWorld().getName();
        int currentX = ((int) currentLocation.getX());
        int currentY = ((int) currentLocation.getY());
        int currentZ = ((int) currentLocation.getZ());

        String world = nbtItem.getString("world");
        int x = nbtItem.getInteger("x");
        int y = nbtItem.getInteger("y");
        int z = nbtItem.getInteger("z");

        return currentWorld.equals(world) && currentX == x && currentY == y && currentZ == z;
    }

    private void storeLocation(Player player, ItemStack stack)
    {
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(ChatColor.RED + "Need at least 1 free inventory space");
            return;
        }

        World world = player.getWorld();
        Location location = player.getLocation();

        String w = world.getName();
        int x = ((int) location.getX());
        int y = ((int) location.getY());
        int z = ((int) location.getZ());

        ItemStack teleportScroll;

        teleportScroll = CreateItem.createTeleportScrollWithCoords(stack, w, x, y, z);

        player.getInventory().addItem(teleportScroll);
        stack.setAmount(stack.getAmount() - 1);
    }

    private void TeleportParticles(Player player) {
        Location location = player.getLocation();
        int count = 50;
        double offsetX = 1;
        double offsetY = 2;
        double offsetZ = 1;

        player.spawnParticle(Particle.SPELL_INSTANT, location, count, offsetX, offsetY, offsetZ);
        player.spawnParticle(Particle.SPELL_WITCH, location, count, offsetX, offsetY, offsetZ);
    }

}
