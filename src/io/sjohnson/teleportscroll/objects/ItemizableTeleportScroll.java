package io.sjohnson.teleportscroll.objects;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.inventory.ItemStack;

public interface ItemizableTeleportScroll {
    int getId();
    int getCount();
    int getTier();
    void setCount(int count);
    ItemStack toItem();
    BaseComponent[] toLink();
    boolean toBed();
}
