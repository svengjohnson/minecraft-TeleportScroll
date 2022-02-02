package io.sjohnson.teleportscroll.handlers;

import io.sjohnson.teleportscroll.helpers.ItemHelper;
import io.sjohnson.teleportscroll.helpers.ParticleHelper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerDamageHandler {
    public PlayerDamageHandler(EntityDamageEvent event, Player player) {
        if (player.getHealth() - event.getFinalDamage() <= 0) {
            playerWouldDie(event, player);
        }
    }

    private void playerWouldDie(EntityDamageEvent event, Player player) {
        // proc totem before lifesaver
        if (hasTotem(player)) {
            return;
        }

        ItemStack lifesaver = this.hasLifesaver(player);
        if (lifesaver == null) {
            return;
        }

        savePlayer(lifesaver, player, event);
    }

    private boolean hasTotem(Player player)
    {
        if (player.getInventory().getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING) {
            return true;
        }

        return player.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING;
    }

    private ItemStack hasLifesaver(Player player)
    {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) {
                continue;
            }

            if (ItemHelper.isLifesaver(item)) {
                return item;
            }
        }

        return null;
    }

    private void savePlayer(ItemStack lifesaver, Player player, EntityDamageEvent event)
    {
        Location bedSpawnLocation = player.getBedSpawnLocation();

        if (bedSpawnLocation == null) {
            player.sendMessage(ChatColor.RED + "Your lifesaver would have saved you...");
            player.sendMessage(ChatColor.RED + "..but you do not have a bed spawn set or your bed is obstructed");
            player.sendMessage(ChatColor.RED + "...so GET REKT");
            return;
        }

        event.setCancelled(true);

        // remove lifesaver
        lifesaver.setAmount(0);

        // set health to max
        player.setHealth(20);

        // set food to max too
        player.setFoodLevel(20);

        // remove fire, if the player had any
        player.setFireTicks(0);

        // add regeneration for 30 seconds
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 600, 10));

        // send a message
        player.sendMessage(ChatColor.GREEN + "Your lifesaver saves you and is consumed in the process!");

        //display particles
        ParticleHelper.teleportParticles(player);

        // play sound
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, (float) 0.5, 2);

        // actually teleport
        player.teleport(bedSpawnLocation);

        // play sound
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, (float) 0.5, 2);

        // display particles at the destination
        ParticleHelper.teleportParticles(player);
    }
}
