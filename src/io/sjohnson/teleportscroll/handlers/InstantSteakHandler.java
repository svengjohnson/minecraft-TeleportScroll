package io.sjohnson.teleportscroll.handlers;

import de.tr7zw.nbtapi.NBTItem;
import io.sjohnson.teleportscroll.helpers.CreateItem;
import io.sjohnson.teleportscroll.helpers.ItemHelper;
import io.sjohnson.teleportscroll.helpers.ParticleHelper;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class InstantSteakHandler {
    public InstantSteakHandler(Player player, ItemStack item) throws InterruptedException {
        NBTItem nbtItem = new NBTItem(item);

        player.setFoodLevel(40);
        player.setSaturation(20);

    }
}
