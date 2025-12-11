package me.lostoryt.easyWarps.commands;

import me.lostoryt.easyWarps.Main;
import me.lostoryt.easyWarps.gui.WarpGui;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WarpsCommand implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public WarpsCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эта команда только для игроков!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("easywarps.warps")) {
            player.sendMessage(ChatColor.RED + "§lУ вас недостаточно прав!");
            return true;
        }

        String filter = "all";
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "my":
                case "мои":
                    filter = "my";
                    break;
                case "spawns":
                case "спавны":
                    filter = "spawns";
                    break;
                case "server":
                case "серверные":
                    filter = "server";
                    break;
                case "public":
                case "публичные":
                    filter = "public";
                    break;
                case "all":
                case "все":
                    filter = "all";
                    break;
                default:
                    player.sendMessage(ChatColor.RED + "§lНеизвестный фильтр!");
                    player.sendMessage(ChatColor.YELLOW + "Доступные фильтры: all, my, spawns, server, public");
                    return true;
            }
        }

        WarpGui gui = new WarpGui(plugin, player, 1, "all");
        gui.open();

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("all");
            completions.add("my");
            completions.add("spawns");
            completions.add("server");
            completions.add("public");
        }

        return completions;
    }
}