package io.sjohnson.teleportscroll.helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class TeleportBookHelper {
    public static ComponentBuilder getBasePage() {
        return new ComponentBuilder()
                .append(ChatColor.BLACK + "[add all scrolls]")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teleportbook addScrolls"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Adds all of the teleport scrolls from your inventory").create()))
                .append("\n")
                .append(ChatColor.BLACK + "[remove all scrolls]")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teleportbook removeScrolls"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Removes all of the scrolls and puts them in your inventory\n(or on the ground, if you don't have room in your inventory").create()))
                .append("\n")
                .append("\n");
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

        teleportBook.setAmount(0);
        putBookInInventory(player, CreateItem.createEmptyTeleportBook(), slot);
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
                if (TeleportHelper.canTeleport(player, destination, tier, true)) {
                    TeleportHelper.teleport(player, destination, tier, false);

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
            String name = jsonScroll.get("name").getAsString();

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

    private String getTeleportScrollJSON(ArrayList<ItemStack> teleportScrolls) {
        int i = 0;
        JsonArrayBuilder teleportScrollBuilder = Json.createArrayBuilder();

        for (ItemStack teleportScroll : teleportScrolls) {
            NBTItem nbtScroll = new NBTItem(teleportScroll);
            ItemMeta scrollMeta = teleportScroll.getItemMeta();
            assert scrollMeta != null;

            if (ItemHelper.isBedTeleportScroll(teleportScroll)) {
                teleportScrollBuilder.add(Json.createObjectBuilder()
                        .add("id", i)
                        .add("count", teleportScroll.getAmount())
                        .add("tier", nbtScroll.getInteger("tier"))
                        .add("name", scrollMeta.getDisplayName())
                        .add("teleport_to_bed", true)
                );
            } else {
                teleportScrollBuilder.add(Json.createObjectBuilder()
                        .add("id", i)
                        .add("count", teleportScroll.getAmount())
                        .add("tier", nbtScroll.getInteger("tier"))
                        .add("name", scrollMeta.getDisplayName())
                        .add("world", nbtScroll.getString("world"))
                        .add("x", nbtScroll.getInteger("x"))
                        .add("y", nbtScroll.getInteger("y"))
                        .add("z", nbtScroll.getInteger("z"))
                        .add("yaw", nbtScroll.getFloat("yaw"))
                        .add("teleport_to_bed", false)
                );
            }

            i++;
        }

        return teleportScrollBuilder.build().toString();
    }

    private ItemStack createBookWithAllTheTeleports(ItemStack originalBook, Player player, ArrayList<ItemStack> teleportScrolls, String JSON) {
        ItemStack teleportBook = new ItemStack(Material.WRITTEN_BOOK);
        ItemHelper.setCustomModel(teleportBook, 10013);
        NBTItem originalNBT = new NBTItem(originalBook);

        if (originalNBT.getBoolean("empty_teleport_book")) {
            ItemHelper.setItemName(teleportBook, ChatColor.GOLD + "" + ChatColor.BOLD + "Teleport Book");
        } else {
            ItemHelper.setItemName(teleportBook, Objects.requireNonNull(originalBook.getItemMeta()).getDisplayName());
        }

        BookMeta bookMeta = (BookMeta) teleportBook.getItemMeta();
        assert bookMeta != null;

        ComponentBuilder page = null;

        int pages = 1;
        int totalCount = 0;

        int i = 0;
        for (ItemStack teleportScroll : teleportScrolls) {
            totalCount += teleportScroll.getAmount();
            if (page == null) {
                if (pages == 1) {
                    page = TeleportBookHelper.getBasePage();
                } else {
                    page = new ComponentBuilder();
                }
            }

            page.append(this.createLinkFromItemStack(i, teleportScroll).create());
            i++;

            // Because first page can have only 11 rows, and subsequent ones can have 14
            if ((i - 10) - (13 * (pages - 1)) > 0) {
                bookMeta.spigot().addPage(page.create());
                pages++;
                page = null;
            }
        }

        if (page != null) {
            bookMeta.spigot().addPage(page.create());
        }

        bookMeta.setTitle(player.getDisplayName() + "'s Teleport Book");
        bookMeta.setAuthor(player.getDisplayName());
        teleportBook.setItemMeta(bookMeta);

        ItemHelper.setItemLore(teleportBook, ChatColor.GOLD + "Contains " + i + " unique teleport scroll(s);Total teleport scrolls: " + totalCount);

        NBTItem nbtItem = new NBTItem(teleportBook);
        nbtItem.setBoolean("is_teleport_book", true);
        nbtItem.setBoolean("empty_teleport_book", false);
        nbtItem.setInteger("generation", 3);
        nbtItem.setString("teleport_book_uuid", UUID.randomUUID().toString());
        nbtItem.setString("json", JSON);

        return nbtItem.getItem();
    }

    private ComponentBuilder createLinkFromData(int id, int count, int tier, String world, int x, int y, int z, float yaw, String displayName) {
        String name = formatBookLinkName(tier, displayName, count);
        String direction = ItemHelper.getCardinalDirection((int) yaw);
        String tierName = ItemHelper.getDefaultTeleportScrollName(tier);

        String altText = String.format("%s\n%s\n" + ChatColor.WHITE + "%s X %s Y %s Z %s %s", displayName, tierName, world, x, y, z, direction);

        return new ComponentBuilder()
                .append(name)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teleportbook teleportTo " + id))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(altText).create()))
                .append("\n");
    }

    private ComponentBuilder createBedTeleportScrollLink(int id, int count, int tier, String displayName) {
        String name = formatBookLinkName(tier, displayName, count);
        String tierName = ItemHelper.getBedTeleportScrollName(tier);

        String altText = String.format("%s\n%s\n" + ChatColor.WHITE + "Teleports you to your respawn point", displayName, tierName);

        return new ComponentBuilder()
                .append(name)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teleportbook teleportTo " + id))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(altText).create()))
                .append("\n");
    }

    private ComponentBuilder createLinkFromItemStack(int id, ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;


        int count = itemStack.getAmount();
        int tier = nbtItem.getInteger("tier");
        String displayName = itemMeta.getDisplayName();

        if (ItemHelper.isBedTeleportScroll(itemStack)) {
            return createBedTeleportScrollLink(id, count, tier, displayName);
        } else {
            String world = nbtItem.getString("world");
            int x = nbtItem.getInteger("x");
            int y = nbtItem.getInteger("y");
            int z = nbtItem.getInteger("z");
            float yaw = nbtItem.getFloat("yaw");

            return createLinkFromData(id, count, tier, world, x, y, z, yaw, displayName);
        }
    }

    private void consumeTeleport(Player player, ItemStack oldBook, int indexToConsume, int slot)
    {
        ArrayList<ItemStack> newScrolls = getExistingScrolls(oldBook, true, indexToConsume);

        if (newScrolls.size() > 0) {
            String scrollJson = getTeleportScrollJSON(newScrolls);
            ItemStack newBook = createBookWithAllTheTeleports(oldBook, player, newScrolls, scrollJson);

            oldBook.setAmount(0);
            putBookInInventory(player, newBook, slot);
        } else {
            oldBook.setAmount(0);
            putBookInInventory(player, CreateItem.createEmptyTeleportBook(), slot);
        }

    }

    private String formatBookLinkName(int tier, String displayName, int count)
    {
        String truncatedName = truncate(ChatColor.stripColor(displayName), tier);

        return switch (tier) {
            case 2 -> ChatColor.BLUE + "" + truncatedName + " (" + count + ")";
            case 3 -> ChatColor.DARK_PURPLE + "" + truncatedName;
            default -> ChatColor.GOLD + "" + truncatedName + " (" + count + ")";
        };
    }

    private String truncate(String value, int tier) {
        int maxlength;

        if (tier == 3) {
            maxlength = 19;
        } else {
            maxlength = 15;
        }

        if (value.length() > maxlength) {
            return value.substring(0, maxlength - 2) + "...";
        } else {
            return value;
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
