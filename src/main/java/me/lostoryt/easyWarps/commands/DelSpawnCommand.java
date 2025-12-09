package me.lostoryt.easyWarps.commands;

import me.lostoryt.easyWarps.Main;
import org.bukkit.ChatColor;
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
        if (strings.length != 0) return false;

        Player sender = (Player) commandSender;

        if (!sender.hasPermission("easywarps.delspawn")) {
            sender.sendMessage(ChatColor.RED + "У вас недостаточно прав");
            return true;
        }

        plugin.deleteLocation("spawn");
        sender.sendMessage(ChatColor.GREEN + "Вы успешно удалили спавн");

        return true;
    }
}
