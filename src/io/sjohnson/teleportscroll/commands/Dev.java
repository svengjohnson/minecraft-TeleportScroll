package io.sjohnson.teleportscroll.commands;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.sjohnson.teleportscroll.helpers.CreateItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Dev implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (!player.isOp()) {
                return true;
            }

            player.getInventory().addItem(CreateItem.createTeleportBook());

            //ItemStack mainhand = player.getInventory().getItemInMainHand();
            //NBTItem mainhandN = new NBTItem(mainhand);
            //System.out.println(mainhandN);

        }

        return true;
    }
}
