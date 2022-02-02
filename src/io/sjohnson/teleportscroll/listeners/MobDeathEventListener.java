package io.sjohnson.teleportscroll.listeners;

import io.sjohnson.teleportscroll.handlers.EndermanDeathHandler;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;


public class MobDeathEventListener implements Listener {
    @EventHandler
    public void mobDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Enderman) {
            new EndermanDeathHandler(entity);
        }
    }
}

