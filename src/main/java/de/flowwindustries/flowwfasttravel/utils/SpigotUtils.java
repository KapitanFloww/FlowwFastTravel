package de.flowwindustries.flowwfasttravel.utils;

import de.flowwindustries.flowwfasttravel.FlowFastTravel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;

public class SpigotUtils {

    /**
     * Null-Safe way to get a world by its name.
     * @param name the name of the world
     * @return the parsed {@link World}
     * @throws IllegalArgumentException if the world does not exist
     */
    public static World getWorldSafe(String name) throws IllegalArgumentException {
        return Optional.ofNullable(Bukkit.getWorld(name)).orElseThrow(() -> new IllegalArgumentException("World does not exist: " + name));
    }

    private static final String prefix = ChatColor.GRAY + "[" + ChatColor.GOLD +  FlowFastTravel.getInstance().getDescription().getPrefix() + ChatColor.GRAY + "]";

    public static void sendPlayerMessage(Player player, String message) {
        player.sendMessage(String.format("%s %s", prefix, message));
    }

    public static void sendPlayerMessage(Collection<Player> players, String message) {
        players.forEach(player -> SpigotUtils.sendPlayerMessage(player, message));
    }
}
