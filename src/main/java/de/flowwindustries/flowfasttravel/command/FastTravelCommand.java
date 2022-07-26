package de.flowwindustries.flowfasttravel.command;

import de.flowwindustries.flowfasttravel.domain.Waypoint;
import de.flowwindustries.flowfasttravel.service.WaypointService;
import de.flowwindustries.flowfasttravel.utils.SpigotParser;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class FastTravelCommand implements CommandExecutor {

    protected static final String INVALID_ARGUMENTS = "Invalid arguments %s";
    private final WaypointService waypointService;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player player) {
            try {
                if (args.length == 1) {
                    executeFastTravelCommand(args, player); //ft <name>
                } else {
                    player.sendMessage(String.format(INVALID_ARGUMENTS, ""));
                }
                return true;
            } catch (IllegalArgumentException ex) {
                player.sendMessage(ChatColor.RED + ex.getMessage());
            }
        }
        return false;
    }

    private void executeFastTravelCommand(String[] args, Player player) {
        String waypointName = args[0];
        Waypoint waypoint = waypointService.getSafe(waypointName);

        Location waypointLocation = new Location(SpigotParser.getWorldSafe(waypoint.getWorldName()), waypoint.getX(), waypoint.getY(), waypoint.getZ());
        player.teleport(waypointLocation);
        player.sendMessage("Welcome to " + waypointName);
    }
}
