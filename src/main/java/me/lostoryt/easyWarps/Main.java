package me.lostoryt.easyWarps;

import me.lostoryt.easyWarps.commands.*;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getLogger().info("Plugin EasyWarps enabled");
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("delspawn").setExecutor(new DelSpawnCommand(this));
        getCommand("setwarp").setExecutor(new SetWarpCommand(this));
        getCommand("warp").setExecutor(new WarpCommand(this));
        getCommand("delwarp").setExecutor(new DelWarpCommand(this));
    }

    public void saveLocation(String name, Location loc) {
        YamlConfiguration config = (YamlConfiguration) this.getConfig();
        config.set("warps." + name + ".world", loc.getWorld().getName());
        config.set("warps." + name + ".x", loc.getX());
        config.set("warps." + name + ".y", loc.getY());
        config.set("warps." + name + ".z", loc.getZ());
        config.set("warps." + name + ".yaw", loc.getYaw());
        config.set("warps." + name + ".pitch", loc.getPitch());
        saveConfig();
    }

    public Location getLocation(String name) {
        YamlConfiguration config = (YamlConfiguration) this.getConfig();
        if (!config.contains("warps." + name)) return null;

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

}
