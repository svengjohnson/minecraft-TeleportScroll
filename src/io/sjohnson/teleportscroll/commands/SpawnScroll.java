package io.sjohnson.teleportscroll.commands;

import io.sjohnson.teleportscroll.helpers.CreateItem;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpawnScroll implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (!player.isOp()) {
                return true;
            }
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Syntax: /spawnscroll tier quantity");
                return true;
            }
            
            int tier = Integer.parseInt(args[0]);
            int quantity = Math.min(Integer.parseInt(args[1]), 64);

            ItemStack item;

            item = CreateItem.createTeleportScroll(tier);

            item.setAmount(quantity);
            player.getInventory().addItem(item);
        }
        return true;
    }
}
