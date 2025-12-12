package me.lostoryt.easyWarps.commands;

import me.lostoryt.easyWarps.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location; // Необходим импорт Location для проверки
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelSpawnCommand implements CommandExecutor {

    private final Main plugin;

    public DelSpawnCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return true;

        Player sender = (Player) commandSender;

        // 1. Проверка прав
        if (!sender.hasPermission("easywarps.delspawn")) {
            sender.sendMessage(ChatColor.RED + "§lУ вас недостаточно прав.");
            return true;
        }

        // 2. Определение ID спавна
        String spawnId = "default";
        if (strings.length > 0) {
            spawnId = strings[0];
            // Также обрабатываем случай, если игрок ввел 'default'
            if (spawnId.equalsIgnoreCase("default")) {
                spawnId = "default";
            }
        }

        // 3. ПРОВЕРКА: Существует ли спавн?
        Location existingSpawn = plugin.getSpawn(spawnId);

        if (existingSpawn == null) {
            // Спавн не найден
            String displayId = spawnId.equals("default") ? "основной" : spawnId;
            sender.sendMessage(ChatColor.RED + "§lСпавн '" + displayId + "' не найден!");
            return true;
        }


        // 4. Удаление и сообщение об успехе
        plugin.deleteSpawn(spawnId);

        String displayId = spawnId.equals("default") ? "основной" : spawnId;
        sender.sendMessage(ChatColor.GREEN + "§lВы успешно удалили спавн '" + displayId + "'.");

        return true;
    }
}