package io.sjohnson.teleportscroll.helpers;

import com.google.gson.JsonObject;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class TeleportHelper {
    public static Location getDestinationForTeleportScroll(Player player, NBTItem nbtItem, ItemStack item)
    {
        Location targetLocation = null;

        if (ItemHelper.isBedTeleportScroll(item)) {
            Location bedSpawnLocation = player.getBedSpawnLocation();

            if (bedSpawnLocation == null) {
                player.sendMessage(ChatColor.YELLOW + "You do not have a bed spawn set or your bed is obstructed");
                return null;
            }

            targetLocation = bedSpawnLocation;

        } else if (ItemHelper.isTeleportScroll(item)) {
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

            targetLocation = location;
        }

        return targetLocation;
    }

    public static Location getDestinationForTeleportBook(Player player, JsonObject teleport, boolean toBed)
    {
        if (toBed) {
            Location bedSpawnLocation = player.getBedSpawnLocation();

            if (bedSpawnLocation == null) {
                player.sendMessage(ChatColor.YELLOW + "You do not have a bed spawn set or your bed is obstructed");
                return null;
            }
        }

        String world = teleport.get("world").getAsString();
        double x = teleport.get("x").getAsInt() + 0.5;
        int y = teleport.get("y").getAsInt();
        double z = teleport.get("z").getAsInt() + 0.5;
        float yaw = teleport.get("yaw").getAsInt();


        World tpWorld = Bukkit.getWorld(world);
        Location targetLocation = new Location(tpWorld, x, y, z);
        targetLocation.setYaw(yaw);

        return targetLocation;
    }

    public static boolean teleport(Player player, Location destination, int tier, boolean showAlreadyThere) throws InterruptedException {
        if (!canTeleport(player, destination, tier, showAlreadyThere)) {
            return false;
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
        player.teleport(destination);

        // display particles at the destination
        ParticleHelper.teleportParticles(player);

        return true;
    }

    public static boolean canTeleport(Player player, Location targetLocation, int tier, boolean showAlreadyThere) {

        if (alreadyThere(player.getLocation(), targetLocation)) {
            if (showAlreadyThere) {
                player.sendMessage(ChatColor.YELLOW + "You are already here!");
            }
            return false;
        }

        if (!sameWorld(player, targetLocation) && tier < 2) {
            player.sendMessage(ChatColor.YELLOW + "Can't teleport to a different dimension with this scroll!");
            return false;
        }

        if (!player.isOnGround()) {
            player.sendMessage(ChatColor.YELLOW + "You must be on the ground to teleport");
            return false;
        }

        return true;
    }

    private static boolean sameWorld(Player player, Location targetLocation)
    {
        return player.getWorld().getName().equals(targetLocation.getWorld().getName());
    }

    public static boolean alreadyThere(Location currentLocation, Location targetLocation) {

        return currentLocation.getBlock().equals(targetLocation.getBlock());
    }
}
