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
import java.util.UUID;

public class TeleportBookHelper {
    public static ComponentBuilder getBasePage()
    {
        return new ComponentBuilder()
                .append(ChatColor.DARK_PURPLE + "[add scrolls]")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teleportbook addScrolls"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Adds all of the teleport scrolls from your inventory").create()))
                .append("\n")
                .append(ChatColor.DARK_PURPLE + "[remove scrolls]")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teleportbook removeScrolls"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Removes all of the scrolls and puts them in your inventory").create()))
                .append("\n")
                .append("\n");
    }

    public void addTeleportScrolls(Player player, ItemStack teleportBook)
    {
        ArrayList<ItemStack> allTeleports = getTeleportScrolls(player.getInventory());
        String scrollJson = getTeleportScrollJSON(allTeleports);
        ItemStack newBook = createBookWithAllTheTeleports(player, allTeleports, scrollJson);

        player.getInventory().addItem(newBook);
    }

    private ArrayList<ItemStack> getTeleportScrolls(Inventory inventory) {
        ArrayList<ItemStack> teleportScrolls = new ArrayList<>();

        for (ItemStack item : inventory.getContents()) {
            if (ItemHelper.isTeleportScroll(item)) {
                NBTItem scroll = new NBTItem(item);
                if (scroll.hasKey("x")) {
                    teleportScrolls.add(item);
                }
            }
        }

        return teleportScrolls;
    }

    private String getTeleportScrollJSON(ArrayList<ItemStack> teleportScrolls)
    {
        int i = 0;
        JsonArrayBuilder teleportScrollBuilder = Json.createArrayBuilder();

        for (ItemStack teleportScroll : teleportScrolls) {
            NBTItem nbtScroll = new NBTItem(teleportScroll);
            ItemMeta scrollMeta = teleportScroll.getItemMeta();
            assert scrollMeta != null;

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
            );

            i++;
        }

        return teleportScrollBuilder.build().toString();
    }

    private ItemStack createBookWithAllTheTeleports(Player player, ArrayList<ItemStack> teleportScrolls, String JSON)
    {
        ItemStack teleportBook = new ItemStack(Material.WRITTEN_BOOK);
        ItemHelper.setItemName(teleportBook, ChatColor.GOLD + "" + ChatColor.BOLD + "Teleport Book");

        ItemMeta Meta = teleportBook.getItemMeta();
        assert Meta != null;

        BookMeta bookMeta = (BookMeta) teleportBook.getItemMeta();
        ComponentBuilder page = null;

        int pages = 1;
        int i = 0;

        for (ItemStack teleportScroll : teleportScrolls) {
            if (page == null) {
                if (pages == 1) {
                    page = TeleportBookHelper.getBasePage();
                } else {
                    page = new ComponentBuilder();
                }
            }


            page.append(this.createLink(i, teleportScroll).create());
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

        ItemHelper.setItemLore(teleportBook, ChatColor.GOLD + "Contains " + i + " different teleport scrolls");

        NBTItem nbtItem = new NBTItem(teleportBook);
        nbtItem.setBoolean("is_teleport_book", true);
        nbtItem.setInteger("generation", 3);
        nbtItem.setString("teleport_book_uuid", UUID.randomUUID().toString());
        nbtItem.setString("json", JSON);

        return nbtItem.getItem();
    }

    private ComponentBuilder createLink(int id, ItemStack itemStack)
    {
        NBTItem nbtItem = new NBTItem(itemStack);
        ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;

        String fullDN = itemMeta.getDisplayName();
        String displayName = truncate(ChatColor.stripColor(fullDN));

        int tier = nbtItem.getInteger("tier");
        int count = itemStack.getAmount();

        String name = switch (tier) {
            case 2 -> ChatColor.BLUE + "" + displayName + " (" + count + ")";
            case 3 -> ChatColor.DARK_PURPLE + "" + displayName;
            default -> ChatColor.GOLD + "" + displayName  + " (" + count + ")";
        };

        String direction;
        String tierName;
        String world = nbtItem.getString("world");
        int x = nbtItem.getInteger("x");
        int y = nbtItem.getInteger("y");
        int z = nbtItem.getInteger("z");
        float yaw = nbtItem.getFloat("yaw");

        if (yaw == 180) {
            direction = "N";
        } else if (yaw == -90) {
            direction = "E";
        } else if (yaw == 90) {
            direction = "W";
        } else {
            direction = "S";
        }

        switch (tier) {
            case 2 -> {
                tierName = ChatColor.AQUA + "Enhanced Teleport Scroll";
            }
            case 3 -> {
                tierName = ChatColor.LIGHT_PURPLE + "Eternal Teleport Scroll";
            }
            default -> {
                tierName = ChatColor.YELLOW + "Teleport Scroll";
            }
        }

        String altText = String.format("%s\n%s\n" + ChatColor.WHITE + "%s X %s Y %s Z %s %s", fullDN, tierName, world, x, y, z, direction);

        return new ComponentBuilder()
                .append(name)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teleportbook teleportTo " + id))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(altText).create()))
                .append("\n");
    }

    public void teleportTo(Player player, ItemStack teleportBook, String teleportIdx) throws InterruptedException {
        NBTItem nbtTeleportBook = new NBTItem(teleportBook);
        String teleportJson = nbtTeleportBook.getString("json");

        JsonArray jsonArray = new JsonParser().parse(teleportJson).getAsJsonArray();

        int id = Integer.parseInt(teleportIdx);

        for (JsonElement teleportElement : jsonArray) {
            JsonObject teleport = teleportElement.getAsJsonObject();
            int index = teleport.get("id").getAsInt();
            int tier = teleport.get("tier").getAsInt();

            if (index == id) {
                Location destination = TeleportHelper.getDestinationForTeleportBook(player, teleport);
                if (TeleportHelper.canTeleport(player, destination, tier, true)) {
                    TeleportHelper.teleport(player, destination, tier, false);
                }
            }
        }
    }

    public String truncate(String value) {
        if (value.length() > 15) {
            return value.substring(0, 13) + "...";
        } else {
            return value;
        }
    }
}
