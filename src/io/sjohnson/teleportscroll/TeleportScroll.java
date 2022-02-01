package io.sjohnson.teleportscroll;


import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class TeleportScroll extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new RightClickListener(), this);
        new CreateRecipe(getServer(), this).registerAll();
        this.getCommand("rename").setExecutor(new Rename());
        this.getCommand("copy").setExecutor(new Copy());
        this.getCommand("spawnscroll").setExecutor(new SpawnScroll());
    }

    @Override
    public void onDisable() {
    }

}
