package de.flowwindustries.flowwfasttravel.command;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import de.flowwindustries.flowwfasttravel.domain.Waypoint;
import de.flowwindustries.flowwfasttravel.service.WaypointService;
import de.flowwindustries.flowwfasttravel.utils.SpigotUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

@Log
@RequiredArgsConstructor
public class FastTravelCommand implements CommandExecutor {

    public static final String INVALID_ARGUMENTS = "Invalid arguments: %s";
    public static final String META_NOT_NULL = "ItemMeta must not be null";
    public static final String WAYPOINT_NAME_FORMAT = "§6§l";

    private final WaypointService waypointService;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player player) {
            if (!player.hasPermission("floww.fasttravel.travel")) {
                throw new IllegalArgumentException("Player is lacking permission to execute this command");
            }
            try {
                if(args.length == 0) {
                    executeGuiCommand(player);
                } else if (args.length == 1) {
                    executeFastTravelCommand(args[0], player); //ft <name>
                } else {
                    throw new IllegalArgumentException(String.format(INVALID_ARGUMENTS, "Usage /ft <name>"));
                }
                return true;
            } catch (IllegalArgumentException ex) {
                SpigotUtils.sendPlayerMessage(player, ChatColor.RED + ex.getMessage());
            }
        }
        return false;
    }

    private void executeFastTravelCommand(String waypointName, Player player) {
        Waypoint waypoint = waypointService.getWaypoint(waypointName);

        Location waypointLocation = new Location(SpigotUtils.getWorldSafe(waypoint.getWorld()), waypoint.getX(), waypoint.getY(), waypoint.getZ());
        player.teleport(waypointLocation);
        SpigotUtils.sendPlayerMessage(player, String.format("%sWelcome to %s%s", ChatColor.YELLOW, ChatColor.GOLD, waypoint.getName()));
    }

    private void executeGuiCommand(Player player) {
        Collection<Waypoint> waypoints = waypointService.getAllWaypoints();
        int quote = (waypoints.size() / 9);
        int rows = quote == 0 ? 1 : quote;

        log.config("Creating Rows: " + rows);

        ChestGui gui = new ChestGui(rows, "Fast travel to...");
        OutlinePane pane = new OutlinePane(0, 0, 9, 5);

        List<GuiItem> waypointItems = new ArrayList<>();
        waypoints.forEach(waypoint -> {
            log.config("Creating item for waypoint " + waypoint.getName());

            // Craft meta
            ItemStack item = new ItemStack(Material.OAK_SIGN);
            ItemMeta meta = Optional.ofNullable(item.getItemMeta())
                    .orElseThrow(() -> new IllegalStateException(META_NOT_NULL));
            String name = String.format(WAYPOINT_NAME_FORMAT + "%s", waypoint.getName());
            String tag = String.format("§f%s (Costs: %s)", waypoint.getDescription(), waypoint.getCost());
            String world = String.format("§f%s", waypoint.getWorld());

            meta.setDisplayName(name);
            meta.setLore(List.of(tag, world));

            // Set meta and item
            item.setItemMeta(meta);
            GuiItem guiItem = new GuiItem(item, event -> handleWaypointClick.accept(player, event));

            waypointItems.add(guiItem);
        });

        log.config("Adding " + waypointItems.size() + " items to gui");

        waypointItems.forEach(pane::addItem);
        gui.addPane(pane);

        gui.setOnTopClick(event -> event.setCancelled(true));
        gui.setOnBottomClick(event -> event.setCancelled(true));
        gui.setOnGlobalClick(event -> event.setCancelled(true));
        gui.setOnOutsideClick(event -> event.setCancelled(true));

        gui.show(player);
    }

    // Teleport players
    private final BiConsumer<Player, InventoryClickEvent> handleWaypointClick = (player, event) -> {
        if(event.getCurrentItem() != null) {
            event.setCancelled(true);
            ItemMeta itemMeta = Optional.ofNullable(event.getCurrentItem().getItemMeta())
                    .orElseThrow(() -> new IllegalStateException(META_NOT_NULL));
            String name = itemMeta.getDisplayName();
            name = name.replace(WAYPOINT_NAME_FORMAT, "");
            executeFastTravelCommand(name, player);
        }
    };
}
