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
    private final Random random = new Random();

    public SpawnCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Эту команду может использовать только игрок.");
            return true;
        }

        Player sender = (Player) commandSender;

        String targetSpawnNumber = null;
        Location spawnLocation = null;
        boolean isRandom = false;

        if (strings.length > 0) {
            targetSpawnNumber = strings[0];

            if (targetSpawnNumber.equalsIgnoreCase("default")) {
                targetSpawnNumber = "default";
            }

            spawnLocation = plugin.getSpawn(targetSpawnNumber);

            if (spawnLocation == null) {
                sender.sendMessage(ChatColor.RED + "§lСпавн '" + targetSpawnNumber + "' не найден.");
                return true;
            }

        } else {
            List<String> spawns = plugin.getAllSpawns();

            if (spawns.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "§lСпавны не найдены.");
                return true;
            }

            isRandom = true;

            targetSpawnNumber = spawns.get(random.nextInt(spawns.size()));
            spawnLocation = plugin.getSpawn(targetSpawnNumber);

            if (spawnLocation == null) {
                sender.sendMessage(ChatColor.RED + "§lПроизошла ошибка при поиске случайного спавна.");
                return true;
            }
        }

        sender.teleport(spawnLocation);

        String displayMessage;

        if (isRandom) {
            displayMessage = ChatColor.GREEN + "§lВы успешно телепортированы на случайный спавн.";
        } else {
            String displayId = targetSpawnNumber.equals("default") ? "основной" : targetSpawnNumber;
            displayMessage = ChatColor.GREEN + "§lВы успешно телепортированы на спавн " + displayId + ".";
        }

        sender.sendMessage(displayMessage);

        return true;
    }
}