package io.sjohnson.teleportscroll.objects;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.tr7zw.changeme.nbtapi.NBTItem;
import io.sjohnson.teleportscroll.helpers.ItemHelper;
import io.sjohnson.teleportscroll.objects.json.JsonTeleportScroll;
import io.sjohnson.teleportscroll.utils.TeleportScrollUtils;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.sjohnson.teleportscroll.objects.model.CustomModel.EMPTY_TELEPORT_BOOK;
import static io.sjohnson.teleportscroll.objects.model.CustomModel.TELEPORT_BOOK;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class TeleportBook extends BaseItem {
    private Player player;
    private boolean isEmpty;
    private boolean hasVanishingCurse;
    private List<ItemStack> teleportScrolls;
    private String json;
    private String displayName;
    private String title;
    private String author;

    private int pages = 1;
    private int uniqueCount = 0;
    private int totalCount = 0;

    private static final String EMPTY = "";
    private static final int GENERATION = 3;
    private static final int MAX_PAGES = 9;
    private static final int MAX_PAGES_WITH_VANISHING = 10;

    public TeleportBook(ItemStack originalBook, Player player, List<ItemStack> teleportScrolls) {
        super(Material.WRITTEN_BOOK);

        this.player = player;
        this.hasVanishingCurse = originalBook.containsEnchantment(Enchantment.VANISHING_CURSE);
        this.teleportScrolls = teleportScrolls;
        this.json = getTeleportScrollJSON();
        this.title = player.getDisplayName() + "'s Teleport Book";
        this.author = player.getDisplayName();
        this.displayName = displayNameExistingOr(originalBook, getDefaultTeleportBookName());

        setDisplayName(formatTeleportBookName());
        addItemFlags(hasVanishingCurse);
        addPages();
        this.item = this.toItem();
        this.isEmpty = getTeleportScrolls().isEmpty();
    }

    public TeleportBook(boolean withVanishingCurse) {
        super(Material.WRITTEN_BOOK);

        this.isEmpty = true;
        this.hasVanishingCurse = withVanishingCurse;
        this.displayName = getDefaultTeleportBookName();
        this.teleportScrolls = List.of();

        setDisplayName(displayName);
        setCustomModel(EMPTY_TELEPORT_BOOK);
        addItemFlags(hasVanishingCurse);
        addBasePage();

        this.item = this.toItem();
    }

    public TeleportBook(ItemStack teleportBook) {
        super(teleportBook);

        BookMeta bookMeta = (BookMeta) item.getItemMeta();
        assert bookMeta != null;

        this.author = bookMeta.getAuthor();
        this.title = bookMeta.getTitle();
        this.hasVanishingCurse = item.containsEnchantment(Enchantment.VANISHING_CURSE);
        this.teleportScrolls = getTeleportScrolls(item);
        this.json = getTeleportScrollJSON();
        this.displayName = bookMeta.getDisplayName();
        this.isEmpty = teleportScrolls.isEmpty();

        setDisplayName(formatTeleportBookName());
        this.item = this.toItem();
    }

    public static ItemStack create(ItemStack itemStack, Player player, List<ItemStack> teleportScrolls) {
        return new TeleportBook(itemStack, player, teleportScrolls).toItem();
    }

    public static ItemStack create(boolean withVanishingCurse) {
        return new TeleportBook(withVanishingCurse).toItem();
    }

    public static ItemStack addVanishingCurse(ItemStack existingBook, Player player) {
        existingBook.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
        NBTItem existingBookNbt = new NBTItem(existingBook);

        if (existingBookNbt.getBoolean(NBTFields.EMPTY_TELEPORT_BOOK)) {
            return TeleportBook.create(true);
        }

        return TeleportBook.create(existingBook, player, getTeleportScrolls(existingBook));
    }

    public void consumeTeleport(int index) {
        consumeTeleportScroll(index);
        //return new TeleportBook(item, player, teleportScrolls);
    }

    private void consumeTeleportScroll(Integer consumeIndex) {
        if (isNull(json)) {
            return;
        }

        JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();

        List<ItemizableTeleportScroll> itemizableTeleportScrolls = jsonArray.asList().stream()
                .map(JsonElement::getAsJsonObject)
                .map(TeleportScrollUtils::createFromJson)
                .toList();

        List<ItemStack> outTeleportScrolls = new ArrayList<>();

        for (ItemizableTeleportScroll teleportScroll : itemizableTeleportScrolls) {
            int id = teleportScroll.getId();
            int count = teleportScroll.getCount();
            int tier = teleportScroll.getTier();

            if (id == consumeIndex && tier < 3) {
                count--;
            }

            if (count > 0) {
                teleportScroll.setCount(count);
                outTeleportScrolls.add(teleportScroll.toItem());
            }
        }

        this.teleportScrolls = outTeleportScrolls;
        this.json = getTeleportScrollJSON();
        resetPages();
    }

    public static List<ItemStack> getTeleportScrolls(ItemStack teleportBook) {
        NBTItem nbtTeleportBook = new NBTItem(teleportBook);

        if (!nbtTeleportBook.hasKey(NBTFields.JSON)) {
            return List.of();
        }

        JsonArray jsonArray = JsonParser.parseString(nbtTeleportBook.getString(NBTFields.JSON)).getAsJsonArray();

        return jsonArray.asList().stream()
                .map(JsonElement::getAsJsonObject)
                .map(TeleportScrollUtils::createFromJson)
                .map(ItemizableTeleportScroll::toItem)
                .toList();
    }

    private String getTeleportScrollJSON() {
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

    private String displayNameExistingOr(ItemStack originalItem, String newDisplayName) {
        BookMeta meta = (BookMeta) originalItem.getItemMeta();
        assert meta != null;

        NBTItem nbtItem = new NBTItem(originalItem);

        if (meta.hasDisplayName() && !nbtItem.getBoolean(NBTFields.EMPTY_TELEPORT_BOOK)) {
            return ChatColor.stripColor(meta.getDisplayName());
        }

        return ChatColor.stripColor(newDisplayName);
    }

    private String formatTeleportBookName() {
        if (!isEmpty) {
            return ChatColor.GOLD + "" + ChatColor.BOLD + displayName;
        } else {
            return ChatColor.YELLOW + "Empty Teleport Book";
        }
    }

    private String getDefaultTeleportBookName() {
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

    private void resetPages() {
        BookMeta bookMeta = (BookMeta) item.getItemMeta();
        assert bookMeta != null;

        bookMeta.spigot().setPages(List.of());
        item.setItemMeta(bookMeta);

        addPages();
    }

    private void addPages() {
        BookMeta bookMeta = (BookMeta) item.getItemMeta();
        assert bookMeta != null;

        ComponentBuilder page = getBasePageOne();

        for (ItemStack teleportScroll : teleportScrolls) {
            ItemizableTeleportScroll itemizable = TeleportScrollUtils.createFromItemStack(teleportScroll, uniqueCount);
            totalCount += itemizable.getCount();
            page.append(itemizable.toLink());
            uniqueCount++;

            // Because first page can have only 11 rows, and subsequent ones can have 14
            if ((uniqueCount - getFirstPageMax()) - (13 * (pages - 1)) > 0) {
                bookMeta.spigot().addPage(page.create());
                pages++;
                page = new ComponentBuilder();
            }
        }

        bookMeta.spigot().addPage(page.create());
        bookMeta.setLore(List.of(
                String.format("%sContains %s unique teleport scrolls", ChatColor.AQUA, uniqueCount),
                String.format("%sTotal teleport scrolls: %s", ChatColor.WHITE, totalCount)
        ));
        bookMeta.setTitle(title);
        bookMeta.setAuthor(author);

        item.setItemMeta(bookMeta);
    }

    private void addBasePage() {
        BookMeta bookMeta = (BookMeta) item.getItemMeta();

        BaseComponent[] basePage = getBasePageOne().create();

        bookMeta.spigot().addPage(basePage);
        bookMeta.setTitle(EMPTY);
        bookMeta.setAuthor(EMPTY);
        item.setItemMeta(bookMeta);
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

    public ItemStack toItem() {
        this.isEmpty = teleportScrolls.isEmpty();

        if (isEmpty) {
            this.displayName = getDefaultTeleportBookName();
            setDisplayName(displayName);
            setCustomModel(EMPTY_TELEPORT_BOOK);
        } else {
            setCustomModel(TELEPORT_BOOK);
        }

        if (hasVanishingCurse) {
            item.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
        }

        NBTItem nbtItem = new NBTItem(item);

        nbtItem.setBoolean(NBTFields.IS_TELEPORT_BOOK, true);
        nbtItem.setInteger(NBTFields.GENERATION, GENERATION);
        nbtItem.setString(NBTFields.TELEPORT_BOOK_UUID, UUID.randomUUID().toString());
        nbtItem.setBoolean(NBTFields.EMPTY_TELEPORT_BOOK, isEmpty);

        if (nonNull(json)) {
            nbtItem.setString(NBTFields.JSON, json);
        }

        return nbtItem.getItem();
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public boolean hasVanishingCurse() {
        return hasVanishingCurse;
    }

    public String getJson() {
        return json;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<ItemStack> getTeleportScrolls() {
        return getTeleportScrolls(item);
    }
}
