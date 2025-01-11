package io.sjohnson.teleportscroll.helpers;

import io.sjohnson.teleportscroll.objects.ItemizableTeleportScroll;
import io.sjohnson.teleportscroll.objects.LocationTeleportScroll;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;

public class TeleportHelper {
    public static Location getDestination(Player player, ItemizableTeleportScroll teleport) {
        if (teleport.toBed()) {
            return TeleportHelper.getBedSpawnLocation(player);
        }

        LocationTeleportScroll locationTeleport = (LocationTeleportScroll) teleport;

        World world = Bukkit.getWorld(locationTeleport.getWorld());

        Location targetLocation = new Location(world,
                locationTeleport.getX() + 0.5,
                locationTeleport.getY(),
                locationTeleport.getZ() + 0.5);

        targetLocation.setYaw(locationTeleport.getYaw());

        return targetLocation;
    }

    public static Location getBedSpawnLocation(Player player) {
        Location bedSpawnLocation = player.getBedSpawnLocation();

        if (bedSpawnLocation == null) {
            player.sendMessage(ChatColor.YELLOW + "You do not have a bed spawn set or your bed is obstructed");
            return null;
        } else {
            return bedSpawnLocation;
        }
    }

    public static boolean teleport(Player player,
                                   Location destination,
                                   ItemizableTeleportScroll teleportScroll,
                                   boolean showAlreadyThere,
                                   boolean mustBeOnTheGround)
            throws InterruptedException {
        if (!canTeleport(player, destination, teleportScroll, showAlreadyThere, mustBeOnTheGround)) {
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

    public static boolean canTeleport(Player player,
                                      Location targetLocation,
                                      ItemizableTeleportScroll teleportScroll,
                                      boolean showAlreadyThere,
                                      boolean mustBeOnTheGround) {
        if (isNull(targetLocation)) {
            return false;
        }

        if (alreadyThere(player.getLocation(), targetLocation)) {
            if (showAlreadyThere) {
                player.sendMessage(ChatColor.YELLOW + "You are already here!");
            }
            return false;
        }

        if (!sameWorld(player, targetLocation) && teleportScroll.getTier() < 2) {
            player.sendMessage(ChatColor.YELLOW + "Can't teleport to a different dimension with this scroll!");
            return false;
        }

        if (!player.isOnGround() && mustBeOnTheGround) {
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
