package io.sjohnson.teleportscroll.helpers;

import com.google.gson.*;
import io.sjohnson.teleportscroll.objects.*;
import io.sjohnson.teleportscroll.utils.TeleportScrollUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class TeleportBookHelper {
    public void addVanishingCurse(Player player, ItemStack teleportBook, int slot) {
        ItemStack vanishingBook = this.getVanishingBook(player.getInventory());

        if (vanishingBook == null) {
            player.sendMessage(ChatColor.RED + "You don't have an Enchanted Book containing the Curse of Vanishing enchantment in your inventory!");
            return;
        }

        if (teleportBook.containsEnchantment(Enchantment.VANISHING_CURSE)) {
            player.sendMessage(ChatColor.RED + "This teleport book already has the Curse of Vanishing enchantment");
            return;
        }

        vanishingBook.setAmount(0);
        putBookInInventory(player, TeleportBook.addVanishingCurse(teleportBook, player), slot);
        teleportBook.setAmount(0);
    }

    public void addTeleportScrolls(Player player, ItemStack teleportBook, int slot) {
        if (!ItemHelper.isTeleportBook(teleportBook)) {
            player.sendMessage(ChatColor.RED + "An unknown error occurred");
            return;
        }

        ArrayList<ItemStack> inventoryScrolls = getInventoryScrolls(player.getInventory());
        ArrayList<ItemStack> bedScrolls = getInventoryBedScrolls(player.getInventory());

        if (inventoryScrolls.size() + bedScrolls.size() == 0) {
            player.sendMessage(ChatColor.YELLOW + "You do not have any teleport scrolls in your inventory");
            return;
        }

        ArrayList<ItemStack> allTeleports = getExistingAndInventoryTeleportScrolls(teleportBook, bedScrolls, inventoryScrolls);

        if (allTeleports.size() > 100) {
            player.sendMessage(ChatColor.YELLOW + "There's a maximum of 100 different teleports, perhaps you should make another book");
            return;
        }

        ItemStack newBook = TeleportBook.create(teleportBook, player, allTeleports);

        teleportBook.setAmount(0);
        putBookInInventory(player, newBook, slot);
        removeInventoryScrolls(bedScrolls, inventoryScrolls);
    }

    public void removeTeleportScrolls(Player player, ItemStack teleportBook, int slot)
    {
        Inventory inventory = player.getInventory();
        List<ItemStack> teleportScrolls = TeleportBook.getTeleportScrolls(teleportBook);
        int emptySlots = ItemHelper.getEmptyInventorySlots(inventory);

        for (ItemStack teleportScroll : teleportScrolls) {
            if (emptySlots > 0) {
                inventory.addItem(teleportScroll);
                emptySlots--;
            } else {
                ItemHelper.dropItem(player, teleportScroll);
            }
        }

        boolean withVanishingCurse = teleportBook.containsEnchantment(Enchantment.VANISHING_CURSE);

        teleportBook.setAmount(0);
        putBookInInventory(player, CreateItem.createEmptyTeleportBook(withVanishingCurse), slot);
    }

    public void teleportTo(Player player, ItemStack teleportBook, String teleportIdx, int slot) throws InterruptedException {
        TeleportBook newBook = new TeleportBook(teleportBook);

        ItemizableTeleportScroll teleportScroll = JsonParser.parseString(newBook.getJson()).getAsJsonArray()
                .asList()
                .stream()
                .map(JsonElement::getAsJsonObject)
                .map(TeleportScrollUtils::createFromJson)
                .filter(t -> t.getId() == Integer.parseInt(teleportIdx))
                .findFirst()
                .orElse(null);

        if (nonNull(teleportScroll)) {
            Location destination = TeleportHelper.getDestination(player, teleportScroll);

            if (isNull(destination)) {
                return;
            }

            if (TeleportHelper.canTeleport(player, destination, teleportScroll, true, false)) {
                TeleportHelper.teleport(player, destination, teleportScroll, false, false);

                if (teleportScroll.getTier() < 3) {
                    teleportBook.setAmount(0);

                    newBook.consumeTeleport(teleportScroll.getId());
                    putBookInInventory(player, newBook.toItem(), slot);
                }
            }
        }
    }

    private void removeInventoryScrolls(ArrayList<ItemStack> bedScrolls, ArrayList<ItemStack> scrolls) {
        ArrayList<ItemStack> allScrolls = new ArrayList<>();
        allScrolls.addAll(bedScrolls);
        allScrolls.addAll(scrolls);

        for (ItemStack item : allScrolls) {
            item.setAmount(0);
        }
    }

    private ArrayList<ItemStack> getExistingAndInventoryTeleportScrolls(ItemStack teleportBook, ArrayList<ItemStack> bedScrolls, ArrayList<ItemStack> inventoryScrolls) {
        ArrayList<ItemStack> teleportScrolls = new ArrayList<>();

        // Bed scrolls always go in first.
        teleportScrolls.addAll(bedScrolls);

        // Then come the exising scrolls
        teleportScrolls.addAll(TeleportBook.getTeleportScrolls(teleportBook));

        // Finally, add the new inventory scrolls.
        teleportScrolls.addAll(inventoryScrolls);

        return teleportScrolls;
    }

    private ArrayList<ItemStack> getInventoryScrolls(Inventory inventory) {
        ArrayList<ItemStack> teleportScrolls = new ArrayList<>();

        for (ItemStack item : inventory.getContents()) {
            if (ItemHelper.isCoordinateTeleportScroll(item)) {
                teleportScrolls.add(item);
            }
        }

        return teleportScrolls;
    }

    private ArrayList<ItemStack> getInventoryBedScrolls(Inventory inventory) {
        ArrayList<ItemStack> teleportScrolls = new ArrayList<>();

        for (ItemStack item : inventory.getContents()) {
            if (ItemHelper.isBedTeleportScroll(item)) {
                teleportScrolls.add(item);
            }
        }

        return teleportScrolls;
    }

    private ItemStack getVanishingBook(Inventory inventory) {
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == Material.ENCHANTED_BOOK) {
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta)item.getItemMeta();
                assert meta != null;
                if (meta.hasStoredEnchant(Enchantment.VANISHING_CURSE)) {
                    return item;
                }
            }
        }

        return null;
    }

    private void putBookInInventory(Player player, ItemStack teleportBook, int slot)
    {
        if (slot == 0) {
            player.getInventory().setItemInMainHand(teleportBook);
        } else {
            player.getInventory().setItemInOffHand(teleportBook);
        }
    }
}
