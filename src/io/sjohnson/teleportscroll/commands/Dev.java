package io.sjohnson.teleportscroll.commands;

import io.sjohnson.teleportscroll.helpers.CreateItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Dev implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (!player.isOp()) {
                return true;
            }

            player.getInventory().addItem(CreateItem.createEmptyTeleportBook(false));
        }

        return true;
    }
}
