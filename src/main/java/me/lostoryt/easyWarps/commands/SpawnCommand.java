package me.lostoryt.easyWarps.commands;

import me.lostoryt.easyWarps.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public class SpawnCommand implements CommandExecutor {

    private final Main plugin;

    public SpawnCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) return true;

        Player sender = (Player) commandSender;

        Location spawn = plugin.getLocation("spawn");


        String spawnId = null;
        if (spawnId != null) {
            plugin.getSpawn(spawnId);
            if (spawn == null) {
                sender.sendMessage(ChatColor.RED + "§lСпавн" + spawnId + "не найден");
                return true;
            }
        } else {
            List<String> spawns = plugin.getAllSpawns();
            if (spawns.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "§lСпавны не найдены");
                return true;
            }
            Random random = new Random();
            spawnId = spawns.get(random.nextInt(spawns.size()));
            spawn = plugin.getSpawn(spawnId);
        }

        if (spawn == null) {
            sender.sendMessage(ChatColor.RED + "§lСпавн не найден");
            return true;
        }

        sender.teleport(spawn);
        if (spawnId != null) {
            sender.sendMessage(ChatColor.GREEN + "§lВы успешно телепортированны на спавн " + spawnId);
        } else {
            sender.sendMessage(ChatColor.GREEN + "§lВы успешно телепортированны на случайный спавн");
        }

        return true;
    }
}
