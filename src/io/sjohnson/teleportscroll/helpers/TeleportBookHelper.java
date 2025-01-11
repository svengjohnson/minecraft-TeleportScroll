package io.sjohnson.teleportscroll.helpers;

import com.google.gson.*;
import de.tr7zw.changeme.nbtapi.NBTItem;
import io.sjohnson.teleportscroll.objects.*;
import io.sjohnson.teleportscroll.objects.json.JsonTeleportScroll;
import io.sjohnson.teleportscroll.objects.json.JsonBedTeleportScroll;
import io.sjohnson.teleportscroll.objects.json.JsonLocationTeleportScroll;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.gson.JsonArray;
import java.util.ArrayList;
import java.util.List;

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

        String scrollJson = getTeleportScrollJSON(allTeleports);
        ItemStack newBook = createBookWithAllTheTeleports(teleportBook, player, allTeleports, scrollJson);

        teleportBook.setAmount(0);
        putBookInInventory(player, newBook, slot);
        removeInventoryScrolls(bedScrolls, inventoryScrolls);
    }

    public void removeTeleportScrolls(Player player, ItemStack teleportBook, int slot)
    {
        Inventory inventory = player.getInventory();
        ArrayList<ItemStack> teleportScrolls = getExistingScrolls(teleportBook, false, 0);
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
        NBTItem nbtTeleportBook = new NBTItem(teleportBook);
        String teleportJson = nbtTeleportBook.getString("json");

        JsonArray jsonArray = new JsonParser().parse(teleportJson).getAsJsonArray();

        int id = Integer.parseInt(teleportIdx);

        for (JsonElement teleportElement : jsonArray) {
            JsonObject teleport = teleportElement.getAsJsonObject();
            int index = teleport.get("id").getAsInt();
            int tier = teleport.get("tier").getAsInt();
            boolean toBed = teleport.get("teleport_to_bed").getAsBoolean();

            if (index == id) {
                Location destination = TeleportHelper.getDestinationForTeleportBook(player, teleport, toBed);

                if (destination == null)
                {
                    return;
                }

                if (TeleportHelper.canTeleport(player, destination, tier, true, false)) {
                    TeleportHelper.teleport(player, destination, tier, false, false);

                    if (tier < 3) {
                        consumeTeleport(player, teleportBook, id, slot);
                    }

                    return;
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

        // Then come the exising scrolls, read in from JSON
        teleportScrolls.addAll(getExistingScrolls(teleportBook, false, 0));

        // Finally, add the new inventory scrolls.
        teleportScrolls.addAll(inventoryScrolls);

        return teleportScrolls;
    }

    private ArrayList<ItemStack> getExistingScrolls(ItemStack teleportBook, boolean consumeScroll, int consumeIndex)
    {
        ArrayList<ItemStack> teleportScrolls = new ArrayList<>();

        for (JsonObject jsonScroll : getExistingScrollJsonObjectArray(teleportBook)) {
            System.out.println(jsonScroll);

            int id = jsonScroll.get("id").getAsInt();
            boolean bed = jsonScroll.get("teleport_to_bed").getAsBoolean();
            int count = jsonScroll.get("count").getAsInt();
            int tier = jsonScroll.get("tier").getAsInt();
            String name = ItemHelper.getCustomTeleportScrollName(tier, jsonScroll.get("display_name").getAsString(), true);

            if (consumeScroll && id == consumeIndex && tier < 3) {
                count--;

                if (count < 1) {
                    continue;
                }
            }

            if (bed) {
                teleportScrolls.add(CreateItem.createCustomBedTeleportScroll(count, tier, name));
            } else {
                String world = jsonScroll.get("world").getAsString();
                int x = jsonScroll.get("x").getAsInt();
                int y = jsonScroll.get("y").getAsInt();
                int z = jsonScroll.get("z").getAsInt();
                float yaw = jsonScroll.get("yaw").getAsFloat();

                teleportScrolls.add(CreateItem.createCustomTeleportScroll(count, tier, name, world, x, y, z, yaw));
            }
        }

        return teleportScrolls;
    }

    private ArrayList<JsonObject> getExistingScrollJsonObjectArray(ItemStack teleportBook) {
        ArrayList<JsonObject> existingScrolls = new ArrayList<>();
        NBTItem nbtTeleportBook = new NBTItem(teleportBook);

        if (!nbtTeleportBook.hasKey("json")) {
            return existingScrolls;
        }

        String teleportJson = nbtTeleportBook.getString("json");

        JsonArray jsonArray = new JsonParser().parse(teleportJson).getAsJsonArray();

        for (JsonElement teleportElement : jsonArray) {
            JsonObject teleport = teleportElement.getAsJsonObject();
            existingScrolls.add(teleport);
        }

        return existingScrolls;
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

    private String getTeleportScrollJSON(ArrayList<ItemStack> teleportScrolls) {
        int i = 0;

        List<JsonTeleportScroll> baseTeleportScrollList = new ArrayList<>();

        for (ItemStack teleportScroll : teleportScrolls) {
            if (ItemHelper.isBedTeleportScroll(teleportScroll)) {
                baseTeleportScrollList.add(BedTeleportScroll.getObject(teleportScroll, i));
            } else {
                baseTeleportScrollList.add(LocationTeleportScroll.getObject(teleportScroll, i));
            }

            i++;
        }

        return new Gson().toJson(baseTeleportScrollList);
    }

    private ItemStack createBookWithAllTheTeleports(ItemStack originalBook, Player player, ArrayList<ItemStack> teleportScrolls, String JSON) {
        return TeleportBook.create(originalBook, player, teleportScrolls, JSON);
    }

    private void consumeTeleport(Player player, ItemStack oldBook, int indexToConsume, int slot)
    {
        boolean hasVanishing = oldBook.containsEnchantment(Enchantment.VANISHING_CURSE);
        ArrayList<ItemStack> newScrolls = getExistingScrolls(oldBook, true, indexToConsume);

        if (newScrolls.size() > 0) {
            String scrollJson = getTeleportScrollJSON(newScrolls);
            ItemStack newBook = createBookWithAllTheTeleports(oldBook, player, newScrolls, scrollJson);

            oldBook.setAmount(0);
            putBookInInventory(player, newBook, slot);
        } else {
            oldBook.setAmount(0);
            putBookInInventory(player, CreateItem.createEmptyTeleportBook(hasVanishing), slot);
        }

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
