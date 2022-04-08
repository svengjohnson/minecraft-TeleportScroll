package io.sjohnson.teleportscroll.commands;

import io.sjohnson.teleportscroll.helpers.ItemHelper;
import io.sjohnson.teleportscroll.helpers.TeleportBookHelper;
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
            if (!ItemHelper.isTeleportBook(mainHand)) {
                return true;
            }

            String subcommand = args[0];
            TeleportBookHelper teleportBookHelper = new TeleportBookHelper();

            switch (subcommand) {
                case "addScrolls":
                    teleportBookHelper.addTeleportScrolls(player, mainHand);
                    break;
                case "removeScrolls":
                    player.sendMessage("not implemented yet");
                    break;
                case "teleportTo":
                    try {
                        teleportBookHelper.teleportTo(player, mainHand, args[1]);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    player.sendMessage("es pisu tavu fateri");
            }

        }

        return true;
    }
}
