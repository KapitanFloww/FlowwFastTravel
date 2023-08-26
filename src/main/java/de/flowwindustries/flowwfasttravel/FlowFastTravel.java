package de.flowwindustries.flowwfasttravel;

import de.flowwindustries.flowwfasttravel.command.FastTravelCommand;
import de.flowwindustries.flowwfasttravel.command.WaypointCommand;
import de.flowwindustries.flowwfasttravel.config.DefaultConfiguration;
import de.flowwindustries.flowwfasttravel.domain.Waypoint;
import de.flowwindustries.flowwfasttravel.repository.WaypointRepository;
import de.flowwindustries.flowwfasttravel.service.WaypointService;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Clock;
import java.time.ZoneId;
import java.util.Objects;

public final class FlowFastTravel extends JavaPlugin {

    @Getter
    private static FlowFastTravel instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        DefaultConfiguration.setupDefaultConfiguration(instance.getConfig());

        Clock clock = Clock.system(ZoneId.systemDefault());
        WaypointRepository waypointRepository = new WaypointRepository(Waypoint.class);
        WaypointService waypointService = new WaypointService(clock, waypointRepository);
        waypointService.init();

        var wpCommand = Objects.requireNonNull(getCommand("wp"));
        wpCommand.setExecutor(new WaypointCommand(waypointService));
        wpCommand.setTabCompleter(new WaypointCommand(waypointService));

        var ftCommand = Objects.requireNonNull(getCommand("ft"));
        ftCommand.setExecutor(new FastTravelCommand(waypointService));
        ftCommand.setTabCompleter(new FastTravelCommand(waypointService));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        instance = null;
    }
}
