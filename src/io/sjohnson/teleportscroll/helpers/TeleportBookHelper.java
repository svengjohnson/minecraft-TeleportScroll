package io.sjohnson.teleportscroll.helpers;

import com.google.gson.*;
import de.tr7zw.changeme.nbtapi.NBTItem;
import io.sjohnson.teleportscroll.objects.*;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
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
    public static ComponentBuilder getBasePage(boolean hasVanishingCurse) {
        ComponentBuilder basePage = new ComponentBuilder();

        basePage.append(ChatColor.BLACK + "[add all scrolls]")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teleportbook addScrolls"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Adds all of the teleport scrolls from your inventory").create()))
                .append("\n")
                .append(ChatColor.BLACK + "[remove all scrolls]")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teleportbook removeScrolls"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Removes all of the scrolls and puts them in your inventory\nor on the ground, if you don't have room in your inventory").create()))
                .append("\n");

        if (!hasVanishingCurse) {
            basePage.append(ChatColor.BLACK + "[add vanishing curse]")
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teleportbook addVanishingCurse"))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Adds Curse of Vanishing to your Teleport Book\n(Requires a Curse of Vanishing book in your inventory!)").create()))
                    .append("\n");
        }

        basePage.append("\n");

        return basePage;
    }

    public void recreateBook(Player player, ItemStack teleportBook, int slot) {
        if (!ItemHelper.isTeleportBook(teleportBook)) {
            player.sendMessage(ChatColor.RED + "An unknown error occurred");
            return;
        }

        ArrayList<ItemStack> inventoryScrolls = getInventoryScrolls(player.getInventory());
        ArrayList<ItemStack> bedScrolls = getInventoryBedScrolls(player.getInventory());

        ArrayList<ItemStack> allTeleports = getExistingAndInventoryTeleportScrolls(teleportBook, bedScrolls, inventoryScrolls);

        String scrollJson = getTeleportScrollJSON(allTeleports);
        ItemStack newBook = createBookWithAllTheTeleports(teleportBook, player, allTeleports, scrollJson);

        teleportBook.setAmount(0);
        putBookInInventory(player, newBook, slot);
        removeInventoryScrolls(bedScrolls, inventoryScrolls);
    }

    public void addVanishingCurse(Player player, ItemStack teleportBook, int slot) {
        ItemStack vanishingBook = this.getVanishingBook(player.getInventory());

        if (teleportBook.containsEnchantment(Enchantment.VANISHING_CURSE)) {
            return;
        }

        NBTItem nbtItem = new NBTItem(teleportBook);

        if (vanishingBook == null) {
            player.sendMessage(ChatColor.RED + "You don't have an Enchanted Book containing the Curse of Vanishing enchantment in your inventory!");
            return;
        }

        vanishingBook.setAmount(0);

        if (nbtItem.getBoolean("empty_teleport_book")) {
            teleportBook.setAmount(0);
            putBookInInventory(player, CreateItem.createEmptyTeleportBook(true), slot);
        } else {
            teleportBook.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
            this.recreateBook(player, teleportBook, slot);
        }
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
            int id = jsonScroll.get("id").getAsInt();
            boolean bed = jsonScroll.get("teleport_to_bed").getAsBoolean();
            int count = jsonScroll.get("count").getAsInt();
            int tier = jsonScroll.get("tier").getAsInt();
            String name = ItemHelper.getCustomTeleportScrollName(tier, jsonScroll.get("name").getAsString(), true);

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

        List<BaseTeleportScroll> baseTeleportScrollList = new ArrayList<>();

        for (ItemStack teleportScroll : teleportScrolls) {
            NBTItem nbtScroll = new NBTItem(teleportScroll);
            ItemMeta scrollMeta = teleportScroll.getItemMeta();
            assert scrollMeta != null;

            if (ItemHelper.isBedTeleportScroll(teleportScroll)) {
                baseTeleportScrollList.add(new BedTeleportScroll(i,
                        teleportScroll.getAmount(),
                        nbtScroll.getInteger("tier"),
                        scrollMeta.getDisplayName()));
            } else {
                baseTeleportScrollList.add(new LocationTeleportScroll(
                        i,
                        teleportScroll.getAmount(),
                        nbtScroll.getInteger("tier"),
                        scrollMeta.getDisplayName(),
                        nbtScroll.getString("world"),
                        nbtScroll.getInteger("x"),
                        nbtScroll.getInteger("y"),
                        nbtScroll.getInteger("z"),
                        nbtScroll.getFloat("yaw")
                ));
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
