package me.lostoryt.easyWarps.gui;

import me.lostoryt.easyWarps.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class WarpGui implements InventoryHolder {

    private final Main plugin;
    private final Player player;
    private final int page;
    private final String currentFilter;
    private Inventory inventory;

    public static final int MODEL_ID_CLOSE = 1001;
    public static final int MODEL_ID_PREV_PAGE = 1002;
    public static final int MODEL_ID_NEXT_PAGE = 1003;
    public static final int MODEL_ID_INFO = 1004;
    public static final int MODEL_ID_FILTER_TOGGLE = 1005;
    public static final int MODEL_ID_MY_WARPS = 1006;

    public WarpGui(Main plugin, Player player, int page, String filter) {
        this.plugin = plugin;
        this.player = player;
        this.page = page;
        this.currentFilter = filter;
        createInventory();
    }

    private void createInventory() {
        List<String> warps = getFilteredWarps();

        warps.sort((w1, w2) -> {
            int type1 = getSortType(w1);
            int type2 = getSortType(w2);

            if (type1 != type2) {
                return Integer.compare(type1, type2);
            }

            if (type1 == 0) {
                int num1 = getSpawnNumber(w1);
                int num2 = getSpawnNumber(w2);
                return Integer.compare(num1, num2);
            }

            String display1 = plugin.getDisplayName(w1);
            String display2 = plugin.getDisplayName(w2);
            return display1.compareToIgnoreCase(display2);
        });

        int itemsPerPage = 45;
        int totalPages = Math.max(1, (int) Math.ceil((double) warps.size() / itemsPerPage));
        int currentPage = Math.min(page, totalPages);

        String title = "Варпы";
        if (currentFilter.equals("spawns")) title = "Спауны";
        else if (currentFilter.equals("server")) title = "Серверные варпы";
        else if (currentFilter.equals("public")) title = "Публичные варпы";
        else if (currentFilter.equals("my")) title = "Мои варпы";

        inventory = Bukkit.createInventory(
                this,
                54,
                ChatColor.DARK_BLUE + "§l" + title + " §7(" + currentPage + "/" + totalPages + ")"
        );

        fillWarps(warps, currentPage, itemsPerPage);
        addNavigationButtons(totalPages, currentPage);
    }

    private ItemStack setCustomModelData(ItemStack item, int modelId) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(modelId);
            item.setItemMeta(meta);
        }
        return item;
    }

    private int getSortType(String warpName) {
        if (plugin.isSpawnName(warpName)) {
            return 0;
        }
        String warpType = plugin.getWarpType(warpName);
        if (warpType.equals("server")) {
            return 1;
        } else {
            return 2;
        }
    }

    private int getSpawnNumber(String warpName) {
        if (warpName.equals("spawn")) {
            return 0;
        } else if (warpName.startsWith("spawn_")) {
            try {
                String numStr = warpName.replace("spawn_", "");
                return Integer.parseInt(numStr);
            } catch (NumberFormatException e) {
                return 999;
            }
        }
        return -1;
    }

    private List<String> getFilteredWarps() {
        List<String> allWarps = plugin.getAllWarpsForGUI();
        List<String> filtered = new ArrayList<>();
        UUID playerUUID = player.getUniqueId();

        for (String warp : allWarps) {
            boolean isSpawn = plugin.isSpawnName(warp);
            String type = plugin.getWarpType(warp);
            UUID ownerUUID = plugin.getWarpOwner(warp);

            switch (currentFilter) {
                case "spawns":
                    if (isSpawn) filtered.add(warp);
                    break;
                case "server":
                    if (!isSpawn && type.equals("server")) filtered.add(warp);
                    break;
                case "public":
                    if (!isSpawn && type.equals("public")) filtered.add(warp);
                    break;
                case "my":
                    if (!isSpawn && ownerUUID != null && ownerUUID.equals(playerUUID)) {
                        filtered.add(warp);
                    }
                    break;
                case "all":
                default:
                    filtered.add(warp);
                    break;
            }
        }
        return filtered;
    }

    private void fillWarps(List<String> warps, int currentPage, int itemsPerPage) {
        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, warps.size());

        for (int i = startIndex; i < endIndex; i++) {
            String warpName = warps.get(i);
            ItemStack warpItem = createWarpItem(warpName);
            if (warpItem != null) {
                inventory.setItem(i - startIndex, warpItem);
            }
        }
    }

    private ItemStack createWarpItem(String warpName) {
        try {
            boolean isSpawn = plugin.isSpawnName(warpName);
            String displayName = plugin.getDisplayName(warpName);

            if (isSpawn) {
                return createSpawnItem(warpName, displayName);
            }

            String warpType = plugin.getWarpType(warpName);
            Material material = Material.COMPASS;
            if (warpType.equals("server")) {
                material = Material.BOOK;
            }

            String lowerName = warpName.toLowerCase();
            if (lowerName.contains("дом") || lowerName.contains("home")) material = Material.OAK_DOOR;
            else if (lowerName.contains("магазин") || lowerName.contains("shop")) material = Material.EMERALD;
            else if (lowerName.contains("шахта") || lowerName.contains("mine")) material = Material.IRON_PICKAXE;
            else if (lowerName.contains("пвп") || lowerName.contains("pvp")) material = Material.DIAMOND_SWORD;

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();

            ChatColor nameColor = warpType.equals("server") ? ChatColor.GOLD : ChatColor.GREEN;
            meta.setDisplayName(nameColor + "§l" + displayName);

            List<String> lore = new ArrayList<>();
            lore.add(warpType.equals("server") ? ChatColor.RED + "Серверный варп" : ChatColor.BLUE + "Публичный варп");

            UUID ownerUUID = plugin.getWarpOwner(warpName);
            if (ownerUUID != null) {
                String ownerName = plugin.getOwnerName(ownerUUID);
                lore.add(ChatColor.GRAY + "Владелец: " + ChatColor.WHITE + ownerName);
            } else if (warpType.equals("server")) {
                lore.add(ChatColor.GRAY + "Владелец: " + ChatColor.WHITE + "Сервер");
            }

            org.bukkit.Location loc = plugin.getLocation(warpName);
            if (loc != null) {
                lore.add(ChatColor.GRAY + "══════════════");
                lore.add(ChatColor.GRAY + "Мир: " + ChatColor.WHITE + loc.getWorld().getName());
                lore.add(ChatColor.GRAY + "Координаты:");
                lore.add(ChatColor.WHITE + "  X: " + (int) loc.getX());
                lore.add(ChatColor.WHITE + "  Y: " + (int) loc.getY());
                lore.add(ChatColor.WHITE + "  Z: " + (int) loc.getZ());
            }

            lore.add("");
            lore.add(ChatColor.YELLOW + "ЛКМ - телепортироваться");

            boolean canDelete = false;
            if (player.hasPermission("easywarps.delwarp")) {
                if (player.isOp()) {
                    canDelete = true;
                } else if (warpType.equals("public")) {
                    if (ownerUUID == null || ownerUUID.equals(player.getUniqueId())) {
                        canDelete = true;
                    }
                }
            }

            if (canDelete) {
                lore.add(ChatColor.RED + "ПКМ - удалить варп");
            }

            meta.setLore(lore);

            NamespacedKey key = new NamespacedKey(plugin, "warp_id");
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, warpName);

            item.setItemMeta(meta);
            return item;

        } catch (Exception e) {
            plugin.getLogger().warning("Error creating warp item for: " + warpName + " - " + e.getMessage());
            return null;
        }
    }

    private ItemStack createSpawnItem(String warpName, String displayName) {
        Material material = Material.BEACON;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "§l" + displayName);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_PURPLE + "⚡ Спаун сервера");

        String spawnNumber = plugin.getSpawnNumber(warpName);
        if (spawnNumber.equals("default")) {
            lore.add(ChatColor.GRAY + "Тип: " + ChatColor.WHITE + "Основной спаун");
        } else {
            lore.add(ChatColor.GRAY + "Тип: " + ChatColor.WHITE + "Дополнительный спаун #" + spawnNumber);
        }

        org.bukkit.Location loc = plugin.getLocation(warpName);
        if (loc != null) {
            lore.add(ChatColor.GRAY + "══════════════");
            lore.add(ChatColor.GRAY + "Мир: " + ChatColor.WHITE + loc.getWorld().getName());
            lore.add(ChatColor.GRAY + "Координаты:");
            lore.add(ChatColor.WHITE + "  X: " + (int) loc.getX());
            lore.add(ChatColor.WHITE + "  Y: " + (int) loc.getY());
            lore.add(ChatColor.WHITE + "  Z: " + (int) loc.getZ());
        }

        lore.add("");
        lore.add(ChatColor.YELLOW + "ЛКМ - телепортироваться");

        if (player.isOp()) {
            lore.add(ChatColor.RED + "ПКМ - удалить спаун");
        }

        meta.setLore(lore);

        NamespacedKey key = new NamespacedKey(plugin, "warp_id");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, warpName);

        item.setItemMeta(meta);
        return item;
    }

    private void addNavigationButtons(int totalPages, int currentPage) {

        // --- ОБНОВЛЕННЫЕ СЛОТЫ ---
        int backSlot = 45;
        int myWarpsSlot = 48; // Перемещено из 47 в 48
        int closeSlot = 49;
        int filterToggleSlot = 50;
        int nextSlot = 53;
        // ------------------------

        // Кнопка Назад
        if (currentPage > 1) {
            ItemStack backButton = new ItemStack(Material.ARROW);
            ItemMeta backMeta = backButton.getItemMeta();
            backMeta.setDisplayName(ChatColor.GREEN + "◀ Предыдущая страница");
            backButton.setItemMeta(backMeta);
            setCustomModelData(backButton, MODEL_ID_PREV_PAGE);
            inventory.setItem(backSlot, backButton);
        } else {
            ItemStack inactiveBack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta inactiveMeta = inactiveBack.getItemMeta();
            inactiveMeta.setDisplayName(ChatColor.DARK_GRAY + "◀ Нет предыдущей страницы");
            inactiveBack.setItemMeta(inactiveMeta);
            inventory.setItem(backSlot, inactiveBack);
        }

        // КНОПКА: МОИ ВАРПЫ (Слот 48)
        ItemStack myWarpsButton = new ItemStack(Material.ENDER_PEARL);
        ItemMeta myWarpsMeta = myWarpsButton.getItemMeta();
        // Считаем варпы, созданные игроком
        long myWarpsCount = getFilteredWarps().stream()
                .filter(w -> !plugin.isSpawnName(w) && plugin.getWarpOwner(w) != null && plugin.getWarpOwner(w).equals(player.getUniqueId()))
                .count();

        myWarpsMeta.setDisplayName(ChatColor.YELLOW + "⭐ §lМои варпы (" + myWarpsCount + ")");
        myWarpsMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Показать только варпы, созданные вами."));
        myWarpsButton.setItemMeta(myWarpsMeta);
        setCustomModelData(myWarpsButton, MODEL_ID_MY_WARPS);
        inventory.setItem(myWarpsSlot, myWarpsButton);

        // Кнопка Закрыть
        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeButton.getItemMeta();
        closeMeta.setDisplayName(ChatColor.RED + "✖ §lЗакрыть");
        closeButton.setItemMeta(closeMeta);
        setCustomModelData(closeButton, MODEL_ID_CLOSE);
        inventory.setItem(closeSlot, closeButton);

        // КНОПКА: ПЕРЕКЛЮЧАТЕЛЬ ФИЛЬТРОВ (Слот 50)
        ItemStack filterButton = new ItemStack(Material.COMPASS);
        ItemMeta filterMeta = filterButton.getItemMeta();

        filterMeta.setDisplayName(ChatColor.AQUA + "⚙ §lТекущий фильтр: " + ChatColor.WHITE + currentFilter.toUpperCase());

        // Список фильтров для Lore
        List<String> filterLore = new ArrayList<>();
        filterLore.add(ChatColor.GRAY + "ЛКМ: Сбросить фильтр");
        filterLore.add(ChatColor.YELLOW + "ПКМ: Переключить тип");
        filterLore.add(ChatColor.GRAY + "══════════════");

        Map<String, String> filterNames = new LinkedHashMap<>();
        filterNames.put("all", "Все варпы");
        filterNames.put("spawns", "Спауны");
        filterNames.put("server", "Серверные");
        filterNames.put("public", "Публичные");

        for (Map.Entry<String, String> entry : filterNames.entrySet()) {
            ChatColor color = entry.getKey().equals(currentFilter) ? ChatColor.GREEN : ChatColor.GRAY;
            filterLore.add(color + "- " + entry.getValue());
        }

        filterMeta.setLore(filterLore);
        filterButton.setItemMeta(filterMeta);
        setCustomModelData(filterButton, MODEL_ID_FILTER_TOGGLE);
        inventory.setItem(filterToggleSlot, filterButton);

        // Кнопка Вперед
        if (currentPage < totalPages) {
            ItemStack nextButton = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextButton.getItemMeta();
            nextMeta.setDisplayName(ChatColor.GREEN + "Следующая страница ▶");
            nextButton.setItemMeta(nextMeta);
            setCustomModelData(nextButton, MODEL_ID_NEXT_PAGE);
            inventory.setItem(nextSlot, nextButton);
        } else {
            ItemStack inactiveNext = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta inactiveMeta = inactiveNext.getItemMeta();
            inactiveMeta.setDisplayName(ChatColor.DARK_GRAY + "Нет следующей страницы ▶");
            inactiveNext.setItemMeta(inactiveMeta);
            inventory.setItem(nextSlot, inactiveNext);
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void open() {
        player.openInventory(inventory);
    }

    public Player getPlayer() {
        return player;
    }

    public int getPage() {
        return page;
    }

    public String getFilter() {
        return currentFilter;
    }
}