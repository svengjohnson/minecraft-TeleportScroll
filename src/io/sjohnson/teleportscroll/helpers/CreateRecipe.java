package io.sjohnson.teleportscroll.helpers;

import io.sjohnson.teleportscroll.Main;
import io.sjohnson.teleportscroll.helpers.CreateItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

@SuppressWarnings("DuplicatedCode")
public class CreateRecipe {

    Server server;
    Main plugin;

    public CreateRecipe(Server server, Main plugin) {
        this.server = server;
        this.plugin = plugin;
    }

    public void registerAll()
    {
        this.server.addRecipe(this.teleportScroll());
        this.server.addRecipe(this.enhancedTeleportScroll());
        this.server.addRecipe(this.eternalTeleportScroll());
    }

    public ShapedRecipe teleportScroll() {
        NamespacedKey key = new NamespacedKey(plugin, "teleport_scroll");
        String name = "Blank Teleport Scroll";
        String displayName = ChatColor.WHITE + name;
        String lore = ChatColor.DARK_GREEN + name;

        ItemStack teleportScroll = CreateItem.teleportScroll(displayName, lore);
        teleportScroll.setAmount(8);

        ShapedRecipe Recipe = new ShapedRecipe(key, teleportScroll);
        Recipe.shape(
                "PPP",
                "PEP",
                "PPP"
        );

        Recipe.setIngredient('P', new RecipeChoice.ExactChoice(new ItemStack(Material.PAPER)));
        Recipe.setIngredient('E', new RecipeChoice.ExactChoice(new ItemStack(Material.ENDER_PEARL)));
        return Recipe;
    }

    public ShapedRecipe enhancedTeleportScroll() {
        NamespacedKey key = new NamespacedKey(plugin, "enhanced_teleport_scroll");
        String name = "Blank Enhanced Teleport Scroll";
        String displayName = ChatColor.YELLOW + name;
        String lore = ChatColor.DARK_GREEN + name;

        ItemStack enhancedTeleportScroll = CreateItem.enhancedTeleportScroll(displayName, lore);
        enhancedTeleportScroll.setAmount(8);

        ShapedRecipe Recipe = new ShapedRecipe(key, enhancedTeleportScroll);
        Recipe.shape(
                "PDP",
                "PEP",
                "PPP"
        );

        Recipe.setIngredient('D', new RecipeChoice.ExactChoice(new ItemStack(Material.DIAMOND)));
        Recipe.setIngredient('P', new RecipeChoice.ExactChoice(new ItemStack(Material.PAPER)));
        Recipe.setIngredient('E', new RecipeChoice.ExactChoice(new ItemStack(Material.ENDER_EYE)));
        return Recipe;
    }

    public ShapedRecipe eternalTeleportScroll() {
        NamespacedKey key = new NamespacedKey(plugin, "eternal_teleport_scroll");
        String name = "Blank Eternal Teleport Scroll";
        String displayName = ChatColor.YELLOW + name;
        String lore = ChatColor.DARK_GREEN + name;

        ItemStack eternalTeleportScroll = CreateItem.eternalTeleportScroll(displayName, lore);

        ShapedRecipe Recipe = new ShapedRecipe(key, eternalTeleportScroll);
        Recipe.shape(
                "DDD",
                "EPE",
                "EEE"
        );

        Recipe.setIngredient('D', new RecipeChoice.ExactChoice(new ItemStack(Material.DIAMOND)));
        Recipe.setIngredient('P', new RecipeChoice.ExactChoice(new ItemStack(Material.PAPER)));
        Recipe.setIngredient('E', new RecipeChoice.ExactChoice(new ItemStack(Material.ENDER_EYE)));
        return Recipe;
    }
}
