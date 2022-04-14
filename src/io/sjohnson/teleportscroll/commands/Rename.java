package io.sjohnson.teleportscroll.commands;

import io.sjohnson.teleportscroll.helpers.ItemHelper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Rename implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            StringBuilder joinedArgs = new StringBuilder();
            String message;

            if (args.length < 1) {
                return false;
            }

            if (itemStack.getType() == Material.AIR) {
                return true;
            }

            for (String arg : args) {
                joinedArgs.append(" ").append(arg);
            }

            message = joinedArgs.substring(1);

            if (!ItemHelper.isTeleportScroll(itemStack) && !ItemHelper.isTeleportBook(itemStack)) {
                player.sendMessage(ChatColor.RED + "This command can only be used on Teleport Scrolls or Teleport Books");
                return true;
            }

            ItemHelper.renameTeleportScrollOrBook(itemStack, message);
            return true;
        }

        return true;
    }
}

