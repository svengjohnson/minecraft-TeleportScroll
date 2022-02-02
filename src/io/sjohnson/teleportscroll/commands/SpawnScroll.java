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
            if (args.length < 3) {
                player.sendMessage(ChatColor.RED + "Syntax: /spawnscroll type tier quantity");
                player.sendMessage(ChatColor.RED + "Types: 0 - blank, 1 - bed");
                return true;
            }
            
            int type = Integer.parseInt(args[0]);
            int tier = Integer.parseInt(args[1]);
            int quantity = Math.min(Integer.parseInt(args[2]), 64);

            ItemStack item;

            if (type == 0) {
                item = CreateItem.createTeleportScroll(tier);
            } else {
                item = CreateItem.createBedTeleportScroll(tier);
            }

            item.setAmount(quantity);
            player.getInventory().addItem(item);
        }
        return true;
    }
}
