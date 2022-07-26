package de.flowwindustries.flowfasttravel.command;

import de.flowwindustries.flowfasttravel.domain.Waypoint;
import de.flowwindustries.flowfasttravel.service.WaypointService;
import de.flowwindustries.flowfasttravel.utils.SpigotUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

@Log
@RequiredArgsConstructor
public class WaypointCommand implements CommandExecutor {
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
                    case 4 -> executeCreateCommand(args, player); //wp create <name> <region> <cost>
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
        SpigotUtils.sendPlayerMessage(player, String.format("%s/wp <help>%s - %sDisplay this help", ChatColor.GOLD, ChatColor.GRAY, ChatColor.YELLOW));
        SpigotUtils.sendPlayerMessage(player, String.format("%s/wp <list> [world] %s- %sShow all waypoints [of given world]", ChatColor.GOLD, ChatColor.GRAY, ChatColor.YELLOW));
        SpigotUtils.sendPlayerMessage(player, String.format("%s/wp <create> <name> <tag> <cost> %s- %sCrate a new waypoint at your current location", ChatColor.GOLD, ChatColor.GRAY, ChatColor.YELLOW));
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
            waypointService.getAll().forEach(waypoint -> {
                String msg = String.format("%s%s %s| %s%s %s%s | (%s)", ChatColor.GOLD, waypoint.getName(), ChatColor.YELLOW, ChatColor.ITALIC, waypoint.getDescription(), ChatColor.RESET, ChatColor.YELLOW, waypoint.getCost());
                SpigotUtils.sendPlayerMessage(player, ChatColor.YELLOW + msg);
            });
        } else {
            player.sendMessage(ChatColor.YELLOW + "Waypoints of World " + ChatColor.GOLD + worldName + ChatColor.YELLOW + ":");
            waypointService.getAll().stream()
                    .filter(waypoint -> waypoint.getWorldName().equals(worldName))
                    .forEach(waypoint -> {
                        String msg = String.format("%s%s %s| %s%s %s%s | (%s)", ChatColor.GOLD, waypoint.getName(), ChatColor.YELLOW, ChatColor.ITALIC, waypoint.getDescription(), ChatColor.RESET, ChatColor.YELLOW, waypoint.getCost());
                        SpigotUtils.sendPlayerMessage(player, ChatColor.YELLOW + msg);
                    });
        }
    }

    private void executeCreateCommand(String[] args, Player player) {
        assertArgumentMatch(args, 0, "create");
        try {
            float costs = Float.parseFloat(args[3]);
            Waypoint waypoint = new Waypoint(
                    args[1],
                    args[2],
                    costs,
                    player.getLocation().getX(),
                    player.getLocation().getY(),
                    player.getLocation().getZ(),
                    player.getWorld().getName()
            );
            waypointService.create(waypoint);
            SpigotUtils.sendPlayerMessage(player, String.format("%sCreated waypoint: %s%s", ChatColor.YELLOW, ChatColor.GOLD, waypoint.getName()));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(String.format(INVALID_ARGUMENTS, args[3]));
        }
    }

    private void assertArgumentMatch(String[] args, int pos, String argument) {
        if(!args[pos].toLowerCase(Locale.ROOT).equals(argument.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException(String.format(INVALID_ARGUMENTS, args[pos]));
        }
    }
}
