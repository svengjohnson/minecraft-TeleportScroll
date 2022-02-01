package io.sjohnson.teleportscroll.commands;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Copy implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            Inventory inventory = player.getInventory();
            ItemStack itemStack = player.getInventory().getItemInMainHand();

            if (itemStack.getType() != Material.AIR)
            {
                NBTItem nbtItem = new NBTItem(itemStack);

                if (!nbtItem.hasKey("is_teleport_scroll")) {
                    player.sendMessage(ChatColor.RED + "This command can only be used on teleport scrolls");
                    return true;
                }

                int tier = nbtItem.getInteger("tier");

                switch (tier) {
                    case 2:
                        this.dupeTier2(player, inventory, itemStack);
                        break;
                    case 3:
                        this.dupeTier3(player, inventory, itemStack);
                        break;
                    default:
                        this.dupeTier1(player, inventory, itemStack);
                        break;
                }

                return true;
            }

        }
        
        return true;
    }

    private void dupeTier1(Player player, Inventory inventory, ItemStack item)
    {
        ItemStack paperStack = this.getPaperStack(inventory, 8);

        if (
                paperStack == null ||
                !inventory.contains(Material.ENDER_PEARL, 1)
        ) {
            player.sendMessage(ChatColor.RED + "1x Ender Pearl and 8x Paper required");
            return;
        }

        paperStack.setAmount(paperStack.getAmount() - 8);
        inventory.removeItem(new ItemStack(Material.ENDER_PEARL, 1));

        ItemStack newItem = item.clone();
        newItem.setAmount(8);
        inventory.addItem(newItem);
    }

    private void dupeTier3(Player player, Inventory inventory, ItemStack item)
    {
        ItemStack paperStack = this.getPaperStack(inventory, 1);

        if (
                paperStack == null ||
                        !inventory.contains(Material.ENDER_EYE, 5) ||
                        !inventory.contains(Material.DIAMOND, 3)
        ) {
            player.sendMessage(ChatColor.RED + "5x Eye of Ender, 3x Diamond and 1x Paper required");
            return;
        }

        paperStack.setAmount(paperStack.getAmount() - 1);
        inventory.removeItem(new ItemStack(Material.ENDER_EYE, 5));
        inventory.removeItem(new ItemStack(Material.DIAMOND, 3));

        ItemStack newItem = item.clone();
        newItem.setAmount(1);
        inventory.addItem(newItem);
    }

    private void dupeTier2(Player player, Inventory inventory, ItemStack item)
    {
        ItemStack paperStack = this.getPaperStack(inventory, 7);

        if (
                paperStack == null ||
                !inventory.contains(Material.ENDER_EYE, 1) ||
                !inventory.contains(Material.DIAMOND, 1)
        ) {
            player.sendMessage(ChatColor.RED + "1x Eye of Ender, 1x Diamond and 7x Paper required");
            return;
        }

        paperStack.setAmount(paperStack.getAmount() - 7);
        inventory.removeItem(new ItemStack(Material.ENDER_EYE, 1));
        inventory.removeItem(new ItemStack(Material.DIAMOND, 1));

        ItemStack newItem = item.clone();
        newItem.setAmount(8);
        inventory.addItem(newItem);
    }

    private ItemStack getPaperStack(Inventory inventory, int required)
    {
        for (ItemStack item : inventory.getContents()) {
            if (item == null) {
                continue;
            }

            if (
                    item.getType() == Material.PAPER &&
                            !item.containsEnchantment(Enchantment.PROTECTION_EXPLOSIONS) &&
                            item.getAmount() >= required
            ) {
                return item;
            }
        }

        return null;
    }
}
