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
import java.util.Arrays; // Добавим для удобства
import java.util.List;
import java.util.stream.Collectors; // Добавим для фильтрации

public class WarpsCommand implements CommandExecutor, TabCompleter {

    private final Main plugin;
    // Список доступных фильтров для Tab Completer и проверки
    private static final List<String> VALID_FILTERS = Arrays.asList(
            "all", "my", "spawns", "server", "public",
            "все", "мои", "спавны", "серверные", "публичные"
    );
    private static final List<String> ENGLISH_FILTERS = Arrays.asList(
            "all", "my", "spawns", "server", "public"
    );


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

        String filter = "all"; // Фильтр по умолчанию

        if (args.length > 0) {
            String arg = args[0].toLowerCase();
            String determinedFilter = null;

            switch (arg) {
                case "my":
                case "мои":
                    determinedFilter = "my";
                    break;
                case "spawns":
                case "спавны":
                    determinedFilter = "spawns";
                    break;
                case "server":
                case "серверные":
                    determinedFilter = "server";
                    break;
                case "public":
                case "публичные":
                    determinedFilter = "public";
                    break;
                case "all":
                case "все":
                    determinedFilter = "all";
                    break;
            }

            if (determinedFilter == null) {
                player.sendMessage(ChatColor.RED + "§lНеизвестный фильтр!");
                player.sendMessage(ChatColor.YELLOW + "Доступные фильтры: all, my, spawns, server, public (или русские аналоги)");
                return true;
            }
            // Переменная 'filter' получает корректное английское значение для передачи в WarpGui
            filter = determinedFilter;
        }

        // ИСПРАВЛЕНИЕ: Используем переменную 'filter' вместо жестко заданного "all"
        WarpGui gui = new WarpGui(plugin, player, 1, filter);
        gui.open();

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String partial = args[0].toLowerCase();

            // Фильтруем только английские названия для передачи в систему.
            return ENGLISH_FILTERS.stream()
                    .filter(f -> f.startsWith(partial))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}