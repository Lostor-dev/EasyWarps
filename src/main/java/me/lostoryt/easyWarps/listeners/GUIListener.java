package me.lostoryt.easyWarps.listeners;

import me.lostoryt.easyWarps.Main;
import me.lostoryt.easyWarps.gui.WarpGui;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class GUIListener implements Listener {

    private final Main plugin;
    private final NamespacedKey warpKey;

    private final String[] filterCycle = {"all", "spawns", "server", "public"};

    public GUIListener(Main plugin) {
        this.plugin = plugin;
        this.warpKey = new NamespacedKey(plugin, "warp_id");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof WarpGui)) {
            return;
        }

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null) return;

        Player player = (Player) event.getWhoClicked();
        WarpGui gui = (WarpGui) event.getInventory().getHolder();

        if (meta.hasCustomModelData()) {
            int modelData = meta.getCustomModelData();

            switch (modelData) {
                case WarpGui.MODEL_ID_CLOSE:
                    player.closeInventory();
                    return;

                case WarpGui.MODEL_ID_PREV_PAGE:
                    if (gui.getPage() > 1) {
                        new WarpGui(plugin, player, gui.getPage() - 1, gui.getFilter()).open();
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                    }
                    return;
                case WarpGui.MODEL_ID_NEXT_PAGE:
                    new WarpGui(plugin, player, gui.getPage() + 1, gui.getFilter()).open();
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                    return;

                case WarpGui.MODEL_ID_MY_WARPS:
                    new WarpGui(plugin, player, 1, "my").open();
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                    return;

                case WarpGui.MODEL_ID_FILTER_TOGGLE:
                    handleFilterToggle(player, gui, event.isRightClick());
                    return;
            }
        }

        if (meta.getPersistentDataContainer().has(warpKey, PersistentDataType.STRING)) {
            String warpId = meta.getPersistentDataContainer().get(warpKey, PersistentDataType.STRING);
            String displayName = plugin.getDisplayName(warpId);

            if (event.isRightClick()) {
                handleWarpDelete(player, warpId, displayName, gui);
            } else {
                handleWarpTeleport(player, warpId, displayName);
            }
        }
    }

    private void handleFilterToggle(Player player, WarpGui gui, boolean isRightClick) {
        String currentFilter = gui.getFilter();
        String newFilter;

        if (isRightClick) {
            int currentIndex = -1;
            for (int i = 0; i < filterCycle.length; i++) {
                if (filterCycle[i].equals(currentFilter)) {
                    currentIndex = i;
                    break;
                }
            }

            // Циклически переключаем на следующий фильтр
            int nextIndex = (currentIndex + 1) % filterCycle.length;
            newFilter = filterCycle[nextIndex];
        } else {
            // ЛКМ сбрасывает на "all"
            newFilter = "all";
        }

        new WarpGui(plugin, player, 1, newFilter).open();
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
    }

    private void handleWarpTeleport(Player player, String warpId, String displayName) {

        String type = plugin.getWarpType(warpId);
        if ("server".equals(type) && !player.isOp()) {
            player.sendMessage(ChatColor.RED + "У вас нет доступа к серверным варпам.");
            return;
        }

        Location loc = plugin.getLocation(warpId);
        if (loc != null) {
            player.closeInventory();
            player.teleport(loc);
            player.sendMessage(ChatColor.GREEN + "Телепортация на " + displayName + "!");
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        } else {
            player.sendMessage(ChatColor.RED + "Ошибка: локация варпа не найдена.");
        }
    }

    private void handleWarpDelete(Player player, String warpId, String displayName, WarpGui gui) {
        boolean isSpawn = plugin.isSpawnName(warpId);
        boolean canDelete = false;

        if (isSpawn) {
            if (player.hasPermission("easywarps.delspawn")) canDelete = true;
        } else {
            if (player.hasPermission("easywarps.delwarp")) {
                if (player.isOp()) {
                    canDelete = true;
                } else {
                    UUID owner = plugin.getWarpOwner(warpId);
                    if (owner != null && owner.equals(player.getUniqueId())) {
                        canDelete = true;
                    }
                }
            }
        }

        if (canDelete) {
            if (isSpawn) {
                String spawnNum = plugin.getSpawnNumber(warpId);
                plugin.deleteSpawn(spawnNum);
            } else {
                plugin.deleteLocation(warpId);
            }

            player.sendMessage(ChatColor.GREEN + "Варп " + displayName + " удален.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1f, 1f);

            new WarpGui(plugin, player, gui.getPage(), gui.getFilter()).open();
        } else {
            player.sendMessage(ChatColor.RED + "У вас нет прав на удаление этого варпа.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
        }
    }
}