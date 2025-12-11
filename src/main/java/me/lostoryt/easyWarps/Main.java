package me.lostoryt.easyWarps;

import me.lostoryt.easyWarps.commands.*;
import me.lostoryt.easyWarps.listeners.GUIListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getLogger().info("Plugin EasyWarps enabled");

        getServer().getPluginManager().registerEvents(new GUIListener(this), this);

        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("delspawn").setExecutor(new DelSpawnCommand(this));
        getCommand("setwarp").setExecutor(new SetWarpCommand(this));
        getCommand("warp").setExecutor(new WarpCommand(this));
        getCommand("delwarp").setExecutor(new DelWarpCommand(this));
        getCommand("warps").setExecutor(new WarpsCommand(this));
    }

    public void saveLocation(String name, Location loc, String type, UUID owner) {
        YamlConfiguration config = (YamlConfiguration) this.getConfig();
        config.set("warps." + name + ".world", loc.getWorld().getName());
        config.set("warps." + name + ".x", loc.getX());
        config.set("warps." + name + ".y", loc.getY());
        config.set("warps." + name + ".z", loc.getZ());
        config.set("warps." + name + ".yaw", loc.getYaw());
        config.set("warps." + name + ".pitch", loc.getPitch());
        config.set("warps." + name + ".type", type);
        if (owner != null) {
            config.set("warps." + name + ".owner", owner.toString());
        } else{
            config.set("warps." + name + ".owner", "server");
        }
        saveConfig();
    }

    public Location getSpawn(String number) {
        YamlConfiguration config = (YamlConfiguration) this.getConfig();

        if (number == null || number.isEmpty() || number.equals("default")) {
            number = "default";
        }

        if (!config.contains("spawns." + number)) {
            return null;
        }

        String world = config.getString("spawns." + number + ".world");
        double x = config.getDouble("spawns." + number + ".x");
        double y = config.getDouble("spawns." + number + ".y");
        double z = config.getDouble("spawns." + number + ".z");
        float yaw = (float) config.getDouble("spawns." + number + ".yaw");
        float pitch = (float) config.getDouble("spawns." + number + ".pitch");

        return new Location(getServer().getWorld(world), x, y, z, yaw, pitch);
    }

    public void deleteSpawn(String number) {
        YamlConfiguration config = (YamlConfiguration) this.getConfig();

        if (number == null || number.isEmpty() || number.equals("default")) {
            number = "default";
        }

        config.set("spawns." + number, null);
        saveConfig();
    }

    public void saveSpawn(String number, Location loc) {
        YamlConfiguration config = (YamlConfiguration) this.getConfig();

        if (number == null || number.isEmpty() || number.equals("default")) {
            number = "default";
        }

        config.set("spawns." + number + ".world", loc.getWorld().getName());
        config.set("spawns." + number + ".x", loc.getX());
        config.set("spawns." + number + ".y", loc.getY());
        config.set("spawns." + number + ".z", loc.getZ());
        config.set("spawns." + number + ".yaw", loc.getYaw());
        config.set("spawns." + number + ".pitch", loc.getPitch());

        saveConfig();
    }

    public List<String> getAllSpawns() {
        YamlConfiguration config = (YamlConfiguration) getConfig();
        List<String> spawns = new ArrayList<>();

        if (!config.contains("spawns")) {
            return spawns;
        }

        for (String key : config.getConfigurationSection("spawns").getKeys(false)) {
            spawns.add(key.equals("default") ? "default" : key);
        }

        return spawns;
    }

    public int getSpawnNumberForSorting(String warpName) {
        if (warpName.equals("spawn")) {
            return 0;
        } else if (warpName.startsWith("spawn_")) {
            try {
                String numberStr = warpName.replace("spawn_", "");
                return Integer.parseInt(numberStr);
            } catch (NumberFormatException e) {
                return 999;
            }
        }
        return -1;
    }

    public int getWarpSortPriority(String warpName) {
        if (isSpawnName(warpName)) {
            return 0;
        }

        String warpType = getWarpType(warpName);
        if (warpType.equals("server")) {
            return 1;
        } else {
            return 2;
        }
    }

    public UUID getWarpOwner(String name) {
        if (name.startsWith("spawn_")) {
            return null;
        }

        YamlConfiguration config = (YamlConfiguration) getConfig();
        String ownerStr = config.getString("warps." + name + ".owner");

        if (ownerStr == null) {
            return null;
        }

        try {
            return UUID.fromString(ownerStr);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public String getOwnerName(UUID uuid) {
        if (uuid == null) return "Сервер";

        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            return player.getName();
        }

        return Bukkit.getOfflinePlayer(uuid).getName();
    }


    public Location getLocation(String name) {
        YamlConfiguration config = (YamlConfiguration) this.getConfig();

        if (name.startsWith("spawn_")) {
            String spawnNumber = name.replace("spawn_", "");
            if (!config.contains("spawns." + spawnNumber)) {
                return null;
            }

            String world = config.getString("spawns." + spawnNumber + ".world");
            double x = config.getDouble("spawns." + spawnNumber + ".x");
            double y = config.getDouble("spawns." + spawnNumber + ".y");
            double z = config.getDouble("spawns." + spawnNumber + ".z");
            float yaw = (float) config.getDouble("spawns." + spawnNumber + ".yaw");
            float pitch = (float) config.getDouble("spawns." + spawnNumber + ".pitch");

            return new Location(getServer().getWorld(world), x, y, z, yaw, pitch);
        }

        if (!config.contains("warps." + name)) {
            return null;
        }

        String world = config.getString("warps." + name + ".world");
        double x = config.getDouble("warps." + name + ".x");
        double y = config.getDouble("warps." + name + ".y");
        double z = config.getDouble("warps." + name + ".z");
        float yaw = (float) config.getDouble("warps." + name + ".yaw");
        float pitch = (float) config.getDouble("warps." + name + ".pitch");

        return new Location(getServer().getWorld(world), x, y, z, yaw, pitch);
    }

    public void deleteLocation(String name) {
        YamlConfiguration config = (YamlConfiguration) this.getConfig();
        config.set("warps." + name, null);
        saveConfig();
    }

    public boolean warpExist(String name) {
        YamlConfiguration config = (YamlConfiguration) getConfig();

        if (name.startsWith("spawn_")) {
            String spawnNumber = name.replace("spawn_", "");
            return config.contains("spawns." + spawnNumber);
        }

        return config.contains("warps." + name);
    }

    public String getWarpList() {
        YamlConfiguration config = (YamlConfiguration) getConfig();
        if (!config.contains("warps")) {
            return "нет варпов";
        }
        return String.join(", ", config.getConfigurationSection("warps").getKeys(false));
    }

    public List<String> getAllWarpsForGUI() {
        YamlConfiguration config = (YamlConfiguration) getConfig();
        List<String> allWarps = new ArrayList<>();

        if (config.contains("warps")) {
            allWarps.addAll(config.getConfigurationSection("warps").getKeys(false));
        }
        if (config.contains("spawns")) {
            for (String key : config.getConfigurationSection("spawns").getKeys(false)) {
                allWarps.add("spawn_" + key);
            }
        }

        return allWarps;
    }

    public boolean isSpawnName(String name) {
        return name.equals("spawn") || name.startsWith("spawn_");
    }

    public String getDisplayName(String warpName) {
        if (warpName.equals("spawn")) {
            return "Спавн";
        } else if (warpName.startsWith("spawn_")) {
            String number = warpName.replace("spawn_", "");
            return "Спавн #" + number;
        }
        return warpName;
    }

    public String getSpawnNumber(String warpName) {
        if (warpName.equals("spawn")) {
            return "default";
        } else if (warpName.startsWith("spawn_")) {
            return warpName.replace("spawn_", "");
        }
        return null;
    }

    public boolean isSafeLocation(Location loc) {
        Block blockBelow = loc.clone().add(0, -1, 0).getBlock();
        Material material = blockBelow.getType();
        if (material == Material.AIR ||
            material == Material.CAVE_AIR ||
            material == Material.VOID_AIR ||
            material == Material.WATER ||
            material == Material.LAVA ||
            material == Material.FIRE) {
            return false;
        }

        Block headBlock = loc.clone().add(0, 1, 0).getBlock();
        Block bodyBlock = loc.getBlock();
        if (headBlock.getType() != Material.AIR || bodyBlock.getType() != Material.AIR) return false;
        return true;
    }

    public String getWarpType(String name) {
        YamlConfiguration config = (YamlConfiguration) getConfig();

        if (name.startsWith("spawn_")) {
            return "server";
        }

        return config.getString("warps." + name + ".type", "public");
    }
}
