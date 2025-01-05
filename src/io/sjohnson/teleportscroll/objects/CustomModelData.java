package io.sjohnson.teleportscroll.objects;

import org.bukkit.Color;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.craftbukkit.v1_21_R3.inventory.components.CraftCustomModelDataComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomModelData implements CustomModelDataComponent, ConfigurationSerializable {
    List<Float> floats = new ArrayList<>();
    List<Boolean> flags = new ArrayList<>();
    List<String> strings = new ArrayList<>();
    List<Color> colors = new ArrayList<>();

    @Override
    public List<Float> getFloats() {
        return floats;
    }

    @Override
    public void setFloats(List<Float> floats) {
        this.floats = floats;
    }

    @Override
    public List<Boolean> getFlags() {
        return flags;
    }

    @Override
    public void setFlags(List<Boolean> flags) {
        this.flags = flags;
    }

    @Override
    public List<String> getStrings() {
        return strings;
    }

    @Override
    public void setStrings(List<String> strings) {
        this.strings = strings;
    }

    @Override
    public List<Color> getColors() {
        return colors;
    }

    @Override
    public void setColors(List<Color> colors) {
        this.colors = colors;
    }

    @Override
    public Map<String, Object> serialize() {
        return Map.of("floats", floats, "flags", flags, "strings", strings, "colors", colors);
    }

    private CustomModelData(int modelId) {
        this.setStrings(List.of(String.valueOf(modelId)));
    }

    public static CraftCustomModelDataComponent fromModelId(int modelId) {
        return new CustomModelData(modelId).getCustomModelDataComponent();
    }

    private CraftCustomModelDataComponent getCustomModelDataComponent() {
        return new CraftCustomModelDataComponent(this.serialize());
    }
}
