package me.lostoryt.easyWarps.commands;

import me.lostoryt.easyWarps.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelWarpCommand implements CommandExecutor {

    private final Main plugin;

    public DelWarpCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) return true;
        if (strings.length != 1) return false;

        Player sender = (Player) commandSender;
        String name = strings[0];

        if (name.equals("spawn")) {
            sender.sendMessage(ChatColor.RED + "Спавн удалять нельзя");
            return true;
        }

        if (!sender.hasPermission("easywarps.delwarp")) {
            sender.sendMessage(ChatColor.RED + "У вас недостаточно прав");
            return true;
        }

        plugin.deleteLocation(name);
        sender.sendMessage(ChatColor.GREEN + "Вы успешно удалили варп " + name);

        return true;
    }
}
