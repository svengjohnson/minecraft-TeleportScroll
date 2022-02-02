package io.sjohnson.teleportscroll.handlers;

import de.tr7zw.nbtapi.NBTItem;
import io.sjohnson.teleportscroll.helpers.CreateItem;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class ActivateTeleportScrollHandler {
    public ActivateTeleportScrollHandler(Player player, ItemStack item) throws InterruptedException {
        NBTItem nbtItem = new NBTItem(item);

        if (!player.isSneaking()) {
            player.sendMessage(ChatColor.YELLOW + "You must be crouching to use teleport scrolls");
            return;
        }

        if (nbtItem.hasKey("world")) {
            this.teleportPlayer(player, nbtItem, item);
        }
        else {
            this.storeLocation(player, item);
        }
    }

    private void teleportPlayer(Player player, NBTItem nbtItem, ItemStack item) throws InterruptedException {
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
            item.setAmount(item.getAmount() - 1);
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