package me.lostoryt.easyWarps.commands;

import me.lostoryt.easyWarps.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCommand implements CommandExecutor {

    private final Main plugin;

    public WarpCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return true;
        if (strings.length != 1) return false;

        Player sender = (Player) commandSender;


        String name = strings[0];
        if (name == null) {
            sender.sendMessage(ChatColor.RED + "§lВы ввели неправильное имя");
            return true;
        }

        Location warp = plugin.getLocation(name);

        if (warp == null) {
            sender.sendMessage(ChatColor.RED + "§lВарп с именем " + name + " не найден");
            return true;
        }

        if (warp.getWorld() == null) {
            sender.sendMessage(ChatColor.RED + "§lМир варпа не найден");
            return true;
        }


        sender.teleport(warp);
        sender.sendMessage(ChatColor.GREEN + "§lВы успешно телепортировались на варп " + name);

        return true;
    }
}
