package io.sjohnson.teleportscroll.commands;

import io.sjohnson.teleportscroll.helpers.ItemHelper;
import io.sjohnson.teleportscroll.helpers.TeleportBookHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TeleportBook implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (!player.isOp()) {
                return true;
            }

            ItemStack mainHand = player.getInventory().getItemInMainHand();
            ItemStack offHand = player.getInventory().getItemInOffHand();
            ItemStack teleportBook;
            int slot;

            if (ItemHelper.isTeleportBook(mainHand)) {
                teleportBook = mainHand;
                slot = 0;
            } else if (ItemHelper.isTeleportBook(offHand)) {
                teleportBook = offHand;
                slot = 1;
            } else {
                return true;
            }

            String subcommand = args[0];
            TeleportBookHelper teleportBookHelper = new TeleportBookHelper();

            switch (subcommand) {
                case "addScrolls":
                    teleportBookHelper.addTeleportScrolls(player, teleportBook, slot);
                    break;
                case "removeScrolls":
                    teleportBookHelper.removeTeleportScrolls(player, teleportBook, slot);
                    break;
                case "teleportTo":
                    try {
                        teleportBookHelper.teleportTo(player, teleportBook, args[1], slot);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
            }

        }

        return true;
    }
}
