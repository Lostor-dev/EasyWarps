package me.lostoryt.easyWarps.commands;

import me.lostoryt.easyWarps.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    private final Main plugin;

    public SpawnCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) return true;
        if (strings.length != 0) return false;

        Player sender = (Player) commandSender;

        Location spawn = plugin.getLocation("spawn");

        if (spawn == null) {
            sender.sendMessage(ChatColor.RED + "Спавн не найден");
            return true;
        }

        sender.teleport(spawn);
        sender.sendMessage(ChatColor.GREEN + "Вы успешно телепортированны на спавн");
        return true;
    }
}
