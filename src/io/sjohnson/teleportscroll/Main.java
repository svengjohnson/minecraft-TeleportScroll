package io.sjohnson.teleportscroll;


import io.sjohnson.teleportscroll.commands.*;
import io.sjohnson.teleportscroll.helpers.CreateRecipe;
import io.sjohnson.teleportscroll.listeners.EntityDamageEventListener;
import io.sjohnson.teleportscroll.listeners.MobDeathEventListener;
import io.sjohnson.teleportscroll.listeners.RightClickItemListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new RightClickItemListener(), this);
        getServer().getPluginManager().registerEvents(new MobDeathEventListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDamageEventListener(), this);

        new CreateRecipe(getServer(), this).registerAll();
        this.getCommand("rename").setExecutor(new Rename());
        this.getCommand("copy").setExecutor(new Copy());
        this.getCommand("spawnscroll").setExecutor(new SpawnScroll());
        this.getCommand("lifesaver").setExecutor(new Lifesaver());
        this.getCommand("dev").setExecutor(new Dev());
    }

    @Override
    public void onDisable() {
    }

}
