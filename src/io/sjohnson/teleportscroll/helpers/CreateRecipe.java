package io.sjohnson.teleportscroll.helpers;

import io.sjohnson.teleportscroll.Main;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

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

        ItemStack teleportScroll = CreateItem.createTeleportScroll(1);
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

        ItemStack enhancedTeleportScroll = CreateItem.createTeleportScroll(2);
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
        ItemStack eternalTeleportScroll = CreateItem.createTeleportScroll(3);

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
