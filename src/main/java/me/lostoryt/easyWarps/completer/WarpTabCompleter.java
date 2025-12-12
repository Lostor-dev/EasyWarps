package me.lostoryt.easyWarps.completer;

import me.lostoryt.easyWarps.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WarpTabCompleter implements TabCompleter {

    private final Main plugin;

    public WarpTabCompleter(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (!(sender instanceof Player)) {
            return new ArrayList<>(); // Консоль не должна получать автодополнение
        }

        // 1. Автодополнение только для первого аргумента (название варпа)
        if (args.length == 1) {

            // Получаем список всех варпов, кроме спаунов (новый метод в Main)
            List<String> warpNames = plugin.getAllWarpsExcludingSpawns();

            String partialName = args[0].toLowerCase();

            // Фильтрация: оставляем только те имена, которые начинаются с введенного текста
            List<String> completions = warpNames.stream()
                    .filter(name -> name.toLowerCase().startsWith(partialName))
                    .collect(Collectors.toList());

            return completions;
        }

        // 2. Для второго и последующих аргументов автодополнение не нужно
        return new ArrayList<>();
    }
}