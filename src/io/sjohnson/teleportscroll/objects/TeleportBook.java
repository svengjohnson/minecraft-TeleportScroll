package io.sjohnson.teleportscroll.objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.tr7zw.changeme.nbtapi.NBTItem;
import io.sjohnson.teleportscroll.helpers.CreateItem;
import io.sjohnson.teleportscroll.helpers.ItemHelper;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static io.sjohnson.teleportscroll.objects.model.CustomModel.EMPTY_TELEPORT_BOOK;
import static io.sjohnson.teleportscroll.objects.model.CustomModel.TELEPORT_BOOK;
import static java.util.Objects.nonNull;

public class TeleportBook extends BaseItem {
    private Player player;
    private boolean isEmpty;
    private boolean hasVanishingCurse;
    private List<ItemStack> teleportScrolls;
    private String json;

    private int pages = 1;
    private int uniqueCount = 0;
    private int totalCount = 0;

    private static final String EMPTY = "";
    private static final int GENERATION = 3;
    private static final int MAX_PAGES = 9;
    private static final int MAX_PAGES_WITH_VANISHING = 10;

    private TeleportBook(ItemStack originalBook, Player player, List<ItemStack> teleportScrolls, String json) {
        super(Material.WRITTEN_BOOK);

        this.player = player;
        this.hasVanishingCurse = originalBook.containsEnchantment(Enchantment.VANISHING_CURSE);
        this.teleportScrolls = teleportScrolls;
        this.json = json;

        if (new NBTItem(originalBook).getBoolean(NBTFields.EMPTY_TELEPORT_BOOK)) {
            setDisplayName(getTeleportBookName());
        } else {
            setDisplayName(Objects.requireNonNull(originalBook.getItemMeta()).getDisplayName());
        }

        setCustomModel(TELEPORT_BOOK);
        addItemFlags(hasVanishingCurse);
        addPages();
        setLore(String.format("%sContains %s unique teleport scrolls;Total teleport scrolls: %s", ChatColor.GOLD, uniqueCount, totalCount));
    }

    private TeleportBook(boolean withVanishingCurse) {
        super(Material.WRITTEN_BOOK);

        this.isEmpty = true;
        this.hasVanishingCurse = withVanishingCurse;

        setDisplayName(getTeleportBookName());
        setCustomModel(EMPTY_TELEPORT_BOOK);
        addItemFlags(hasVanishingCurse);
        addBasePage();
    }

    public static ItemStack create(ItemStack itemStack, Player player, List<ItemStack> teleportScrolls, String json) {
        return new TeleportBook(itemStack, player, teleportScrolls, json).getItem();
    }

    public static ItemStack create(boolean withVanishingCurse) {
        return new TeleportBook(withVanishingCurse).getItem();
    }

    public static ItemStack addVanishingCurse(ItemStack existingBook, Player player) {
        existingBook.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
        NBTItem existingBookNbt = new NBTItem(existingBook);

        if (existingBookNbt.getBoolean(NBTFields.EMPTY_TELEPORT_BOOK)) {
            return TeleportBook.create(true);
        }

        return TeleportBook.create(existingBook, player, getTeleportScrolls(existingBook), existingBookNbt.getString(NBTFields.JSON));
    }

    private static List<ItemStack> getTeleportScrolls(ItemStack teleportBook) {
        boolean consumeScroll = false;
        int consumeIndex = 0;

        List<ItemStack> teleportScrolls = new ArrayList<>();

        for (JsonObject jsonScroll : getExistingScrollsJson(teleportBook)) {
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
                teleportScrolls.add(BedTeleportScroll.create(tier, count, name));
            } else {
                String world = jsonScroll.get("world").getAsString();
                int x = jsonScroll.get("x").getAsInt();
                int y = jsonScroll.get("y").getAsInt();
                int z = jsonScroll.get("z").getAsInt();
                float yaw = jsonScroll.get("yaw").getAsFloat();

                teleportScrolls.add(LocationTeleportScroll.create(tier, world, x, y, z, yaw, name, count));
            }
        }

        return teleportScrolls;
    }

    private static ArrayList<JsonObject> getExistingScrollsJson(ItemStack teleportBook) {
        ArrayList<JsonObject> existingScrolls = new ArrayList<>();
        NBTItem nbtTeleportBook = new NBTItem(teleportBook);

        if (!nbtTeleportBook.hasKey(NBTFields.JSON)) {
            return existingScrolls;
        }

        String teleportJson = nbtTeleportBook.getString(NBTFields.JSON);

        JsonArray jsonArray = new JsonParser().parse(teleportJson).getAsJsonArray();

        for (JsonElement teleportElement : jsonArray) {
            JsonObject teleport = teleportElement.getAsJsonObject();
            existingScrolls.add(teleport);
        }

        return existingScrolls;
    }

    private String getTeleportBookName() {
        if (nonNull(player)) {
            String playerName = ChatColor.stripColor(player.getDisplayName());
            return ChatColor.GOLD + "" + ChatColor.BOLD + playerName + "'s Teleport Book";
        }

        return ChatColor.YELLOW + "Empty Teleport Book";
    }

    public int getFirstPageMax() {
        if (hasVanishingCurse) {
            return MAX_PAGES_WITH_VANISHING;
        }

        return MAX_PAGES;
    }

    private void addPages() {
        BookMeta bookMeta = (BookMeta) getItemMeta();
        assert bookMeta != null;

        ComponentBuilder page = getBasePageOne();

        for (ItemStack teleportScroll : teleportScrolls) {
            totalCount += teleportScroll.getAmount();
            page.append(this.createLinkFromItemStack(uniqueCount, teleportScroll).create());
            uniqueCount++;

            // Because first page can have only 11 rows, and subsequent ones can have 14
            if ((uniqueCount - getFirstPageMax()) - (13 * (pages - 1)) > 0) {
                bookMeta.spigot().addPage(page.create());
                pages++;
                page = new ComponentBuilder();
            }
        }

        bookMeta.spigot().addPage(page.create());

        bookMeta.setTitle(player.getDisplayName() + "'s Teleport Book");
        bookMeta.setAuthor(player.getDisplayName());
        setItemMeta(bookMeta);
    }

    private void addBasePage() {
        BookMeta bookMeta = (BookMeta) getItemMeta();

        BaseComponent[] basePage = getBasePageOne().create();

        bookMeta.spigot().addPage(basePage);
        bookMeta.setTitle(EMPTY);
        bookMeta.setAuthor(EMPTY);
        setItemMeta(bookMeta);
    }

    private ComponentBuilder getBasePageOne() {
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

    private ComponentBuilder createLinkFromData(int id, int count, int tier, String world, int x, int y, int z, float yaw, String displayName) {
        String name = formatBookLinkName(tier, displayName, count);
        String direction = ItemHelper.getCardinalDirection((int) yaw);
        String tierName = ItemHelper.getDefaultTeleportScrollName(tier, false);

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

    private ItemStack getItem() {
        if (hasVanishingCurse) {
            addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
        }

        NBTItem nbtItem = new NBTItem(this);

        nbtItem.setBoolean(NBTFields.IS_TELEPORT_BOOK, true);
        nbtItem.setInteger(NBTFields.GENERATION, GENERATION);
        nbtItem.setString(NBTFields.TELEPORT_BOOK_UUID, UUID.randomUUID().toString());
        nbtItem.setBoolean(NBTFields.EMPTY_TELEPORT_BOOK, isEmpty);

        if (nonNull(json)) {
            nbtItem.setString(NBTFields.JSON, json);
        }

        return nbtItem.getItem();
    }

    private static class NBTFields {
        private NBTFields() {}
        public static final String IS_TELEPORT_BOOK = "is_teleport_book";
        public static final String EMPTY_TELEPORT_BOOK = "empty_teleport_book";
        public static final String GENERATION = "generation";
        public static final String TELEPORT_BOOK_UUID = "teleport_book_uuid";
        public static final String JSON = "json";
    }
}
