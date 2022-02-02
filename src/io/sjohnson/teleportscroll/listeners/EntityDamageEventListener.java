package io.sjohnson.teleportscroll.listeners;

import io.sjohnson.teleportscroll.handlers.EndermanDeathHandler;
import io.sjohnson.teleportscroll.handlers.PlayerDamageHandler;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;


public class EntityDamageEventListener implements Listener {
    @EventHandler
    public void entityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player) {
            new PlayerDamageHandler(event, ((Player) entity).getPlayer());
        }
    }
}

