package me.lostoryt.easyWarps.commands;

import me.lostoryt.easyWarps.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

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
            sender.sendMessage(ChatColor.RED + "§lСпавн удалять нельзя");
            return true;
        }

        if (!sender.hasPermission("easywarps.delwarp")) {
            sender.sendMessage(ChatColor.RED + "§lУ вас недостаточно прав");
            return true;
        }

        if (!plugin.warpExist(name)) {
            sender.sendMessage( ChatColor.RED + "§lВарп " + name + " не существует");
            sender.sendMessage( ChatColor.YELLOW + "§lДоступные варпы: " + plugin.getWarpList());
            return true;
        }

        UUID warpOwner = plugin.getWarpOwner(name);
        if (warpOwner == null && warpOwner.equals(sender.getUniqueId())){
            plugin.deleteLocation(name);
            sender.sendMessage(ChatColor.GREEN + "§lВы успешно удалили варп " + name);
            return true;
        } else if (sender.isOp()) {
            plugin.deleteLocation(name);
            sender.sendMessage(ChatColor.GREEN + "§lВы успешно удалили варп " + name);
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "§lВы не можете удалить чужой варп");
            return true;
        }
    }
}
