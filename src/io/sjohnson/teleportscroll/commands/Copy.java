package io.sjohnson.teleportscroll.commands;

import de.tr7zw.nbtapi.NBTItem;
import io.sjohnson.teleportscroll.helpers.ItemHelper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Copy implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            Inventory inventory = player.getInventory();
            ItemStack itemStack = player.getInventory().getItemInMainHand();

            if (itemStack.getType() != Material.AIR)
            {
                NBTItem nbtItem = new NBTItem(itemStack);

                if (!ItemHelper.isTeleportScroll(itemStack)) {
                    player.sendMessage(ChatColor.RED + "This command can only be used on Teleport Scrolls");
                    return true;
                }

                if (ItemHelper.isBedTeleportScroll(itemStack)) {
                    player.sendMessage(ChatColor.RED + "This command cannot be used on Bed Teleport Scrolls");
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

        if (ItemHelper.getEmptyInventorySlots(inventory) == 0) {
            player.sendMessage(ChatColor.YELLOW + "You need at least 1 free inventory space for this");
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
                        !inventory.contains(Material.DIAMOND_BLOCK, 1) ||
                        !inventory.contains(Material.CHORUS_FRUIT, 2)
        ) {
            player.sendMessage(ChatColor.RED + "5x Eye of Ender, 2x Chorus Fruit,  1x Diamond Block and 1x Paper required");
            return;
        }

        if (ItemHelper.getEmptyInventorySlots(inventory) == 0) {
            player.sendMessage(ChatColor.YELLOW + "You need at least 1 free inventory space for this");
        }

        paperStack.setAmount(paperStack.getAmount() - 1);
        inventory.removeItem(new ItemStack(Material.ENDER_EYE, 5));
        inventory.removeItem(new ItemStack(Material.DIAMOND_BLOCK, 1));
        inventory.removeItem(new ItemStack(Material.CHORUS_FRUIT, 2));

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

        if (ItemHelper.getEmptyInventorySlots(inventory) == 0) {
            player.sendMessage(ChatColor.YELLOW + "You need at least 1 free inventory space for this");
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
                            !ItemHelper.isTeleportScroll(item) &&
                            item.getAmount() >= required
            ) {
                return item;
            }
        }

        return null;
    }
}
