package io.sjohnson.teleportscroll;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
            String name;
            String displayName;
            String lore;

            switch (tier) {
                case 3:
                    name = "Blank Eternal Teleport Scroll";
                    displayName = ChatColor.YELLOW + name;
                    lore = ChatColor.DARK_GREEN + name;
                    item = CreateItem.eternalTeleportScroll(displayName, lore);
                    break;
                case 2:
                    name = "Blank Enhanced Teleport Scroll";
                    displayName = ChatColor.YELLOW + name;
                    lore = ChatColor.DARK_GREEN + name;
                    item = CreateItem.enhancedTeleportScroll(displayName, lore);
                    break;
                default:
                    name = "Blank Teleport Scroll";
                    displayName = ChatColor.WHITE + name;
                    lore = ChatColor.DARK_GREEN + name;
                    item = CreateItem.teleportScroll(displayName, lore);
            }

            item.setAmount(quantity);
            player.getInventory().addItem(item);
        }
        return true;
    }
}
