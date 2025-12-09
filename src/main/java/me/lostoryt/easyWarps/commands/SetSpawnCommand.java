package me.lostoryt.easyWarps.commands;

import me.lostoryt.easyWarps.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand implements CommandExecutor {

    private final Main plugin;

    public SetSpawnCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return true;
        if (strings.length !=0) return false;

        Player sender = (Player) commandSender;

        if (!sender.hasPermission("easywarps.setspawn")) {
            sender.sendMessage(ChatColor.RED + "У вас недостаточно прав");
            return true;
        }

        int x = (int) sender.getLocation().getX();
        int y = (int) sender.getLocation().getY();
        int z = (int) sender.getLocation().getZ();


        plugin.saveLocation("spawn", sender.getLocation());
        sender.sendMessage(ChatColor.GREEN + "Вы установили спавн на координатах: " + ChatColor.YELLOW + " " + x  + " "+ y + " " + z);

        return true;
    }
}
