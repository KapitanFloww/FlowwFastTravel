package de.flowwindustries.flowwfasttravel.command;

import de.flowwindustries.flowwfasttravel.domain.Waypoint;
import de.flowwindustries.flowwfasttravel.service.WaypointService;
import de.flowwindustries.flowwfasttravel.utils.SpigotUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Log
@RequiredArgsConstructor
public class WaypointCommand implements CommandExecutor, TabCompleter {
    protected static final String INVALID_ARGUMENTS = "Invalid arguments %s";

    private final WaypointService waypointService;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player player) {
            try {
                switch (args.length) {
                    case 1 -> {
                        if(args[0].equalsIgnoreCase("help")) {
                            executeHelpCommand(player);
                        } else if(args[0].equalsIgnoreCase("list")) {
                            executeListCommand(args, player); //wp list
                        } else {
                            throw new IllegalArgumentException(String.format(INVALID_ARGUMENTS, args[0]));
                        }
                    }
                    case 2 -> executeListCommand(args, player); //wp list <worldName>
                    case 3 -> executeCreateCommand(args, player); //wp create <name> <cost>
                    default -> throw new IllegalArgumentException(String.format(INVALID_ARGUMENTS, ""));
                }
                return true;
            } catch (IllegalArgumentException ex) {
                SpigotUtils.sendPlayerMessage(player, ChatColor.RED + ex.getMessage());
            }
        }
        return false;
    }

    private void executeHelpCommand(Player player) {
        SpigotUtils.sendPlayerMessage(player, String.format("%sFastTravel Commands:", ChatColor.GOLD));
        SpigotUtils.sendPlayerMessage(player, String.format("%s/ft <name> %s- %sFast travel to a location", ChatColor.GOLD, ChatColor.GRAY, ChatColor.YELLOW));
        SpigotUtils.sendPlayerMessage(player, String.format("%s/wp help %s- %sDisplay this help", ChatColor.GOLD, ChatColor.GRAY, ChatColor.YELLOW));
        SpigotUtils.sendPlayerMessage(player, String.format("%s/wp list [world] %s- %sShow all waypoints [of given world]", ChatColor.GOLD, ChatColor.GRAY, ChatColor.YELLOW));
        SpigotUtils.sendPlayerMessage(player, String.format("%s/wp create <name> <cost> %s- %sCrate a new waypoint at your current location", ChatColor.GOLD, ChatColor.GRAY, ChatColor.YELLOW));
    }

    private void executeListCommand(String[] args, Player player) {
        assertArgumentMatch(args, 0, "list");
        if (args.length == 1) {
            executeListCommandIntern(player, null);
        } else {
            executeListCommandIntern(player, args[1]);
        }
    }

    private void executeListCommandIntern(Player player, String worldName) {
        if(worldName == null) {
            SpigotUtils.sendPlayerMessage(player, ChatColor.YELLOW + "Waypoints:");
            waypointService.getAllWaypoints().forEach(waypoint -> {
                String msg = String.format("%s%s %s| %s%s %s%s | (%s)", ChatColor.GOLD, waypoint.getName(), ChatColor.YELLOW, ChatColor.ITALIC, waypoint.getDescription(), ChatColor.RESET, ChatColor.YELLOW, waypoint.getCost());
                SpigotUtils.sendPlayerMessage(player, ChatColor.YELLOW + msg);
            });
        } else {
            player.sendMessage(ChatColor.YELLOW + "Waypoints of World " + ChatColor.GOLD + worldName + ChatColor.YELLOW + ":");
            waypointService.getAllWaypoints().stream()
                    .filter(waypoint -> waypoint.getWorld().equals(worldName))
                    .forEach(waypoint -> {
                        String msg = String.format("%s%s %s| %s%s %s%s | (%s)", ChatColor.GOLD, waypoint.getName(), ChatColor.YELLOW, ChatColor.ITALIC, waypoint.getDescription(), ChatColor.RESET, ChatColor.YELLOW, waypoint.getCost());
                        SpigotUtils.sendPlayerMessage(player, ChatColor.YELLOW + msg);
                    });
        }
    }

    private void executeCreateCommand(String[] args, Player player) {
        if (!player.hasPermission("floww.fasttravel.waypoint")) {
            throw new IllegalArgumentException("Player is lacking permission to execute this command");
        }
        assertArgumentMatch(args, 0, "create");
        try {
            final String name = args[1];
            final float costs = Float.parseFloat(args[2]);
            Waypoint waypoint = new Waypoint()
                    .withName(name)
                    .withCost(costs)
                    .withX(player.getLocation().getX())
                    .withY(player.getLocation().getY())
                    .withZ(player.getLocation().getZ())
                    .withWorld(player.getWorld().getName())
                    .withCreatedAt(Instant.now());
            waypointService.createWaypoint(waypoint);
            SpigotUtils.sendPlayerMessage(player, String.format("%sCreated waypoint: %s%s", ChatColor.YELLOW, ChatColor.GOLD, waypoint.getName()));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(String.format(INVALID_ARGUMENTS, args[2]));
        }
    }

    private void assertArgumentMatch(String[] args, int pos, String argument) {
        if(!args[pos].toLowerCase(Locale.ROOT).equals(argument.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException(String.format(INVALID_ARGUMENTS, args[pos]));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return List.of("create", "list", "help");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("list")) {
            return Bukkit.getWorlds().stream().map(World::getName).toList();
        }
        return Collections.emptyList();
    }
}
