package de.flowwindustries.flowfasttravel.command;

import de.flowwindustries.flowfasttravel.domain.Waypoint;
import de.flowwindustries.flowfasttravel.service.WaypointService;
import de.flowwindustries.flowfasttravel.utils.SpigotUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class FastTravelCommand implements CommandExecutor {

    protected static final String INVALID_ARGUMENTS = "Invalid arguments: %s";
    private final WaypointService waypointService;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player player) {
            try {
                if (args.length == 1) {
                    executeFastTravelCommand(args, player); //ft <name>
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

    private void executeFastTravelCommand(String[] args, Player player) {
        String waypointName = args[0];
        Waypoint waypoint = waypointService.getSafe(waypointName);

        Location waypointLocation = new Location(SpigotUtils.getWorldSafe(waypoint.getWorldName()), waypoint.getX(), waypoint.getY(), waypoint.getZ());
        player.teleport(waypointLocation);
        SpigotUtils.sendPlayerMessage(player, String.format("%sWelcome to %s%s", ChatColor.YELLOW, ChatColor.GOLD, waypoint.getName()));
    }
}
