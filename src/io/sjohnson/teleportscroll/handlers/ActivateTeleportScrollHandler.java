package io.sjohnson.teleportscroll.handlers;

import de.tr7zw.nbtapi.NBTItem;
import io.sjohnson.teleportscroll.helpers.CreateItem;
import io.sjohnson.teleportscroll.helpers.ItemHelper;
import io.sjohnson.teleportscroll.helpers.ParticleHelper;
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

        if (ItemHelper.isBedTeleportScroll(item)) {
            this.teleportToBed(player, nbtItem, item);
            return;
        }

        if (nbtItem.hasKey("world")) {
            this.teleportToDestination(player, nbtItem, item);
        } else {
            this.storeLocation(player, item);
        }
    }

    private void teleportToBed(Player player, NBTItem nbtItem, ItemStack item) throws InterruptedException {
        Location bedSpawnLocation = player.getBedSpawnLocation();

        if (bedSpawnLocation == null) {
            player.sendMessage(ChatColor.YELLOW + "You do not have a bed spawn set or your bed is obstructed");
            return;
        }

        // If player is not there already
        if (bedSpawnLocation.getBlock().equals(player.getLocation().getBlock())) {
            return;
        }

        this.teleport(player, bedSpawnLocation, nbtItem, item);
    }

    private void teleportToDestination(Player player, NBTItem nbtItem, ItemStack item) throws InterruptedException {
        String world = nbtItem.getString("world");
        double x = nbtItem.getInteger("x") + 0.5;
        int y = nbtItem.getInteger("y");
        double z = nbtItem.getInteger("z") + 0.5;
        float yaw;

        if (nbtItem.hasKey("yaw")) {
            yaw = nbtItem.getFloat("yaw");
        } else {
            yaw = 0;
        }

        World tpWorld = Bukkit.getWorld(world);
        Location location = new Location(tpWorld, x, y, z);
        location.setYaw(yaw);

        this.teleport(player,location, nbtItem, item);
    }

    private void teleport(Player player, Location location, NBTItem nbtItem, ItemStack item) throws InterruptedException {
        int tier = nbtItem.getInteger("tier");
        if (!this.canTeleport(player, nbtItem)) {
            return;
        }

        if (tier < 3) {
            item.setAmount(item.getAmount() - 1);
        }

        //display particles
        ParticleHelper.teleportParticles(player);

        // play sound
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, (float) 0.5, 2);

        // send message
        player.sendMessage(ChatColor.AQUA + "Teleporting...");

        // timeout for 1 second
        TimeUnit.SECONDS.sleep(1);

        // actually teleport
        player.teleport(location);

        // display particles at the destination
        ParticleHelper.teleportParticles(player);
    }

    private boolean canTeleport(Player player, NBTItem nbtItem) {
        int tier = nbtItem.getInteger("tier");

        if (this.alreadyThere(player.getLocation(), nbtItem)) {
            //player.sendMessage(ChatColor.YELLOW + "You are already here!");
            return false;
        }

        if (!this.sameWorldCheck(player, nbtItem) && tier < 2) {
            player.sendMessage(ChatColor.YELLOW + "Can't teleport to a different dimension with this scroll!");
            return false;
        }

        if (!player.isOnGround()) {
            player.sendMessage(ChatColor.YELLOW + "You must be on the ground to teleport");
            return false;
        }

        return true;
    }

    private boolean sameWorldCheck(Player player, NBTItem nbtItem)
    {
        if (ItemHelper.isBedTeleportScroll(nbtItem.getItem())) {
            return sameWorldBed(player);
        }

        return sameWorld(player, nbtItem);
    }

    private boolean sameWorld(Player player, NBTItem nbtItem) {
        return player.getWorld().getName().equals(nbtItem.getString("world"));
    }

    private boolean sameWorldBed(Player player)
    {
        return player.getWorld().getName().equals(player.getBedSpawnLocation().getWorld().getName());
    }

    private boolean alreadyThere(Location currentLocation, NBTItem nbtItem) {
        String currentWorld = currentLocation.getWorld().getName();
        int currentX = ((int) currentLocation.getX() - 1);
        int currentY = ((int) Math.round(currentLocation.getY()));
        int currentZ = ((int) currentLocation.getZ() - 1);

        String world = nbtItem.getString("world");
        int x = nbtItem.getInteger("x");
        int y = nbtItem.getInteger("y");
        int z = nbtItem.getInteger("z");

        return currentWorld.equals(world) && currentX == x && currentY == y && currentZ == z;
    }

    private void storeLocation(Player player, ItemStack stack) {
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(ChatColor.RED + "Need at least 1 free inventory space");
            return;
        }

        World world = player.getWorld();
        Location location = player.getLocation();

        String w = world.getName();
        int x = ((int) location.getX()) - 1;
        int y = ((int) Math.round(location.getY()));
        int z = ((int) location.getZ()) - 1;
        float yaw = location.getYaw();

        ItemStack teleportScroll;

        teleportScroll = CreateItem.createTeleportScrollWithCoords(stack, w, x, y, z, yaw);

        player.getInventory().addItem(teleportScroll);
        stack.setAmount(stack.getAmount() - 1);
    }
}
