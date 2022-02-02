package io.sjohnson.teleportscroll.commands;

import io.sjohnson.teleportscroll.helpers.CreateItem;
import io.sjohnson.teleportscroll.helpers.ItemHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Lifesaver implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (!player.isOp()) {
                return true;
            }

            int quantity;
            int max = ItemHelper.getEmptyInventorySlots(player.getInventory());

            if (args.length > 0) {
                quantity = Math.min(Integer.parseInt(args[0]), max);
            } else {
                quantity = 1;
            }

            for (int i = 1; i <= quantity; i++) {
                player.getInventory().addItem(CreateItem.createLifesaver());
            }
        }
        return true;
    }
}
