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

        if (name.equalsIgnoreCase("spawn")) {
            sender.sendMessage(ChatColor.RED + "§lТакое имя устанавливать нельзя");
            return true;
        }

        if (!sender.hasPermission("easywarps.setwarp")) {
            sender.sendMessage(ChatColor.RED + "§lУ вас недостаточно прав");
            return true;
        }

        if (name.length() > 16) {
            sender.sendMessage(ChatColor.RED + "§lПревышен лимит символов в названии (макс 16 символов)!");
            return true;
        }

        if (plugin.warpExist(name)) {
            sender.sendMessage(ChatColor.RED + "§lТакое имя уже занято");
            return true;
        }

        if (!plugin.isSafeLocation(sender.getLocation())) {
            sender.sendMessage(ChatColor.RED + "§lНебезопасное место");
            return true;
        }

        String warpType = sender.isOp() ? "server" : "public";

        plugin.saveLocation(name, sender.getLocation(), warpType, sender.getUniqueId());

        int x = (int) sender.getLocation().getX();
        int y = (int) sender.getLocation().getY();
        int z = (int) sender.getLocation().getZ();

        String typeMessage = warpType.equals("server") ? "серверный" : "публичный";
        sender.sendMessage(ChatColor.GREEN + "§lСоздан " + typeMessage + " варп '" + name +
                "' на координатах: " + ChatColor.YELLOW + x + " " + y + " " + z);

        if (warpType.equals("public")) {
            sender.sendMessage(ChatColor.GRAY + "Администраторы могут удалить этот варп, если он нарушает правила.");
        }

        return true;
    }
}
