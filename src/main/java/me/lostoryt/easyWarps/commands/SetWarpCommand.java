package me.lostoryt.easyWarps.commands;

import me.lostoryt.easyWarps.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetWarpCommand implements CommandExecutor {

    private final Main plugin;

    public SetWarpCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) return true;
        if (strings.length != 1) return false;

        Player sender = (Player) commandSender;
        String name = strings[0];

        if (name.equals("spawn")) {
            sender.sendMessage(ChatColor.RED + "Такое имя устанавливать нельзя");
            return true;
        }

        if (!sender.hasPermission("easywarps.setwarp")) {
            sender.sendMessage(ChatColor.RED + "У вас недостаточно прав");
            return true;
        }

        int x = (int) sender.getLocation().getX();
        int y = (int) sender.getLocation().getY();
        int z = (int) sender.getLocation().getZ();

        plugin.saveLocation(name, sender.getLocation());
        sender.sendMessage(ChatColor.GREEN + "Вы установили варп " + name + " на координатах: " + ChatColor.YELLOW + " " + x  + " "+ y + " " + z);

        return true;
    }
}
