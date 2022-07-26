package de.flowwindustries.flowfasttravel.command;

import de.flowwindustries.flowfasttravel.domain.Waypoint;
import de.flowwindustries.flowfasttravel.service.WaypointService;
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
                    case 1 -> executeListCommand(args, player); //wp list
                    case 2 -> executeListCommand(args, player); //wp list <worldName>
                    case 4 -> executeCreateCommand(args, player); //wp create <name> <region> <cost>
                    default -> throw new IllegalArgumentException(String.format(INVALID_ARGUMENTS, ""));
                }
                return true;
            } catch (IllegalArgumentException ex) {
                player.sendMessage(ChatColor.RED + ex.getMessage());
            }
        }
        return false;
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
            player.sendMessage(ChatColor.GOLD + "Waypoints:");
            waypointService.getAll().forEach(waypoint -> player.sendMessage(String.format("%s | %s (%s)", waypoint.getName(), waypoint.getDescription(), waypoint.getCost())));
        } else {
            player.sendMessage(ChatColor.GOLD + "Waypoints of World " + ChatColor.YELLOW + worldName + ChatColor.GOLD + ":");
            waypointService.getAll().stream()
                    .filter(waypoint -> waypoint.getWorldName().equals(worldName))
                    .forEach(waypoint -> player.sendMessage(String.format("%s | %s (%s)", waypoint.getName(), waypoint.getDescription(), waypoint.getCost())));
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
