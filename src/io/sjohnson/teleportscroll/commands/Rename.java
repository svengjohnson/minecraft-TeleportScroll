package io.sjohnson.teleportscroll.commands;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Rename implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            StringBuilder joinedArgs = new StringBuilder();
            String message;

            for (String arg : args)
            {
                joinedArgs.append(" ").append(arg);
            }

            message = joinedArgs.substring(1);

            if (itemStack.getType() != Material.AIR)
            {
                NBTItem nbtItem = new NBTItem(itemStack);

                if (!nbtItem.hasKey("is_teleport_scroll")) {
                    player.sendMessage(ChatColor.RED + "This command can only be used on teleport scrolls");
                    return false;
                }

                int tier = nbtItem.getInteger("tier");
                String newDisplayName;

                switch (tier) {
                    case 2:
                        newDisplayName = ChatColor.YELLOW + message;
                        break;
                    case 3:
                        newDisplayName = ChatColor.YELLOW + "" + ChatColor.BOLD + message;
                        break;
                    default:
                        newDisplayName = ChatColor.AQUA + message;
                        break;
                }

                ItemMeta meta = itemStack.getItemMeta();
                meta.setDisplayName(newDisplayName);
                itemStack.setItemMeta(meta);
                return true;
            }

        }
        
        return true;
    }
}
