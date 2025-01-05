package io.sjohnson.teleportscroll.helpers;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class ParticleHelper {
    public static void teleportParticles(Player player)
    {
        Location location = player.getLocation();
        int count = 50;
        double offsetX = 1;
        double offsetY = 2;
        double offsetZ = 1;

        player.spawnParticle(Particle.FIREWORK, location, count, offsetX, offsetY, offsetZ);
    }
}
