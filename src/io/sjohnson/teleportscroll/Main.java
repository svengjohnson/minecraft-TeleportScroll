package io.sjohnson.teleportscroll;


import io.sjohnson.teleportscroll.commands.Copy;
import io.sjohnson.teleportscroll.commands.Rename;
import io.sjohnson.teleportscroll.commands.SpawnScroll;
import io.sjohnson.teleportscroll.helpers.CreateRecipe;
import io.sjohnson.teleportscroll.listeners.RightClickItemListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new RightClickItemListener(), this);
        new CreateRecipe(getServer(), this).registerAll();
        this.getCommand("rename").setExecutor(new Rename());
        this.getCommand("copy").setExecutor(new Copy());
        this.getCommand("spawnscroll").setExecutor(new SpawnScroll());
    }

    @Override
    public void onDisable() {
    }

}
