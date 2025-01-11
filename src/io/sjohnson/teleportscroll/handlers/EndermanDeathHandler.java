package io.sjohnson.teleportscroll.handlers;

import io.sjohnson.teleportscroll.helpers.CreateItem;
import io.sjohnson.teleportscroll.helpers.ItemHelper;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class EndermanDeathHandler {
    public EndermanDeathHandler(Entity enderman) {
        Location location = enderman.getLocation();
        World world = location.getWorld();

        ItemStack randomDrop = rollRandomDrop(location);

        if (randomDrop != null) {
            assert world != null;
            world.dropItem(location, randomDrop);
        }

    }

    private ItemStack rollRandomDrop(Location location)
    {
        int min = 0;
        int max = 999;
        int structureAttempts = 1;

        ItemStack drop;

        int roll = (int)(Math.random()*(max-min+1)+min);
        boolean is_the_end = Objects.requireNonNull(location.getWorld()).getName().equals("world_the_end");
        int blank_scroll_req, blank_scroll_req_t2, structure_scroll_req, empty_teleport_book_req;

        if (is_the_end) {
            return null;
//            blank_scroll_req = 20;
//            blank_scroll_req_t2 = 30;
//            structure_scroll_req = 35;
//            empty_teleport_book_req = 0;
        } else {
            blank_scroll_req = 20;
            blank_scroll_req_t2 = 30;
            structure_scroll_req = 35;
            empty_teleport_book_req = 36;
        }


        // 2% chance on getting a 4x blank tier 1 scroll as a drop
        if (roll < blank_scroll_req) {
            drop = CreateItem.createTeleportScroll(1);
            drop.setAmount(4);
            return drop;
        }

        // 1% chance on getting a 4x blank tier 2 scroll as a drop
        if (roll < blank_scroll_req_t2) {
            drop = CreateItem.createTeleportScroll(2);
            drop.setAmount(4);
            return drop;
        }

        // 0.5% chance on rolling a Structure Teleport Scroll,
        if (roll < structure_scroll_req) {
            ItemStack structureTeleportScroll;

            for (int i = 0; i < structureAttempts; i++) {
                structureTeleportScroll = generateStructureTeleportScroll(location);
                if (structureTeleportScroll != null) {
                    return structureTeleportScroll;
                }
            }

            // drop 1x blank tier 3 scrolls if we failed to find a structure or a safe location
            drop = CreateItem.createTeleportScroll(3);
            drop.setAmount(1);
            return drop;
        }

        // 0.1% chance to drop an empty teleport book, 0% chance in the end
        if (roll < empty_teleport_book_req)  {
            return CreateItem.createEmptyTeleportBook(false);
        }

        return null;
    }

    private ItemStack generateStructureTeleportScroll(Location location)
    {
        int x, z, tp_x, tp_y, tp_z;
        int y = 128;
        int radius = 5000;

        String worldName = this.getRandomWorld();
        StructureType structure = this.getRandomStructure(worldName);
        World world = Bukkit.getWorld(worldName);

        x = getRandomX(location);
        z = getRandomZ(location);

        Location searchLocation = new Location(world, x, y, z);
        assert world != null;

        Location foundStructure = world.locateNearestStructure(searchLocation, structure, radius, true);

        if (foundStructure == null) {
            return null;
        }

        tp_x = (int) foundStructure.getX();
        tp_z = (int) foundStructure.getZ();

        if (worldName.equals("world_nether")) {
            tp_y = findSafeNetherCoordinate(world, tp_x, tp_z);
        } else {
            tp_y = world.getHighestBlockYAt(tp_x, tp_z) + 1;
        }

        if (tp_y == 0) {
            // no safe location found
            return null;
        }

        String name = this.getStructureName(structure) + " Teleport Scroll";

        ItemStack blankScroll = CreateItem.createTeleportScroll(2);
        ItemStack teleportScroll = CreateItem.createTeleportScrollWithCoords(blankScroll, world.getName(), tp_x, tp_y, tp_z, 0);
        ItemHelper.renameTeleportScrollOrBook(teleportScroll, name);

        return teleportScroll;
    }

    private int findSafeNetherCoordinate(World world, int x, int z)
    {
        int y;

        for (y = 1; y < 126; y++) {
            if (this.isSafeLocation(new Location(world, x, y, z))) {
                return y;
            }
        }

        return 0;
    }

    public boolean isSafeLocation(Location location) {

        Block feet = location.getBlock();
        Block head = feet.getRelative(BlockFace.UP);
        Block ground = feet.getRelative(BlockFace.DOWN);

        if (head.getType() != Material.AIR) {
            return false;
        }

        if (feet.getType() != Material.AIR) {
            return false;
        }

        if (ground.getType() == Material.LAVA) {
            return false;
        }

        if (ground.getType() == Material.AIR) {
            return false;
        }


        return ground.getType().isSolid();
    }

    private int getRandomX(Location location)
    {
        int x = (int) location.getX();
        int min = x - 10000;
        int max = x + 10000;

        return (int)(Math.random()*(max-min+1)+min);
    }

    private int getRandomZ(Location location)
    {
        int z = (int) location.getX();
        int min = z - 10000;
        int max = z + 10000;

        return (int)(Math.random()*(max-min+1)+min);
    }

    private String getRandomWorld()
    {
        // 70% chance on overworld, 20% on nether, 10% on the end
        ArrayList<String> worlds = new ArrayList<>();
        worlds.add("world");
        worlds.add("world");
        worlds.add("world");
        worlds.add("world");
        worlds.add("world");
        worlds.add("world");
        worlds.add("world");
        worlds.add("world_nether");
        worlds.add("world_nether");
        worlds.add("world_the_end");

        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(worlds.size());
        return worlds.get(index);
    }

    private String getStructureName(StructureType structure)
    {
        if (structure == StructureType.MINESHAFT) {
            return "Mineshaft";
        }

        if (structure == StructureType.VILLAGE) {
            return "Village";
        }

        if (structure == StructureType.STRONGHOLD) {
            return "Stronghold";
        }

        if (structure == StructureType.JUNGLE_PYRAMID) {
            return "Jungle Pyramid";
        }

        if (structure == StructureType.OCEAN_RUIN) {
            return "Ocean Ruin";
        }

        if (structure == StructureType.DESERT_PYRAMID) {
            return "Desert Pyramid";
        }

        if (structure == StructureType.IGLOO) {
            return "Igloo";
        }

        if (structure == StructureType.SWAMP_HUT) {
            return "Swamp Hut";
        }

        if (structure == StructureType.OCEAN_MONUMENT) {
            return "Ocean Monument";
        }

        if (structure == StructureType.WOODLAND_MANSION) {
            return "Woodland Mansion";
        }

        if (structure == StructureType.BURIED_TREASURE) {
            return "Buried Treasure";
        }

        if (structure == StructureType.SHIPWRECK) {
            return "Shipwreck";
        }

        if (structure == StructureType.PILLAGER_OUTPOST) {
            return "Pillager Outpost";
        }

        if (structure == StructureType.RUINED_PORTAL) {
            return "Ruined Portal";
        }

        if (structure == StructureType.END_CITY) {
            return "End City";
        }

        if (structure == StructureType.NETHER_FORTRESS) {
            return "Nether Fortress";
        }

        if (structure == StructureType.NETHER_FOSSIL) {
            return "Nether Fossil";
        }

        if (structure == StructureType.BASTION_REMNANT) {
            return "Bastion Remnant";
        }

        return "Unknown Structure";
    }

    private StructureType getRandomStructure(String world)
    {
        ArrayList<StructureType> overworld = new ArrayList<>();
        overworld.add(StructureType.VILLAGE);
        overworld.add(StructureType.STRONGHOLD);
        overworld.add(StructureType.JUNGLE_PYRAMID);
        overworld.add(StructureType.OCEAN_RUIN);
        overworld.add(StructureType.DESERT_PYRAMID);
        overworld.add(StructureType.IGLOO);
        overworld.add(StructureType.SWAMP_HUT);
        overworld.add(StructureType.OCEAN_MONUMENT);
        overworld.add(StructureType.WOODLAND_MANSION);
        overworld.add(StructureType.SHIPWRECK);
        overworld.add(StructureType.PILLAGER_OUTPOST);
        overworld.add(StructureType.RUINED_PORTAL);

        ArrayList<StructureType> end = new ArrayList<>();
        end.add(StructureType.END_CITY);

        ArrayList<StructureType> nether = new ArrayList<>();
        nether.add(StructureType.NETHER_FORTRESS);
        nether.add(StructureType.NETHER_FOSSIL);
        nether.add(StructureType.BASTION_REMNANT);

        return switch (world) {
            case "world_the_end" -> end.get(new Random().nextInt(end.size()));
            case "world_nether" -> nether.get(new Random().nextInt(nether.size()));
            default -> overworld.get(new Random().nextInt(overworld.size()));
        };
    }
}
