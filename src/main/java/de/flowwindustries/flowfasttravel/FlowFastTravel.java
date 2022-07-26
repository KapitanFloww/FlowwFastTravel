package de.flowwindustries.flowfasttravel;

import de.flowwindustries.flowfasttravel.command.FastTravelCommand;
import de.flowwindustries.flowfasttravel.command.WaypointCommand;
import de.flowwindustries.flowfasttravel.domain.Waypoint;
import de.flowwindustries.flowfasttravel.repository.WaypointRepository;
import de.flowwindustries.flowfasttravel.service.WaypointService;
import de.flowwindustries.flowfasttravel.service.WaypointServiceImpl;
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

        Clock clock = Clock.system(ZoneId.systemDefault());
        WaypointRepository waypointRepository = new WaypointRepository(Waypoint.class);
        WaypointService waypointService = new WaypointServiceImpl(clock, waypointRepository);

        Objects.requireNonNull(getCommand("wp")).setExecutor(new WaypointCommand(waypointService));
        Objects.requireNonNull(getCommand("ft")).setExecutor(new FastTravelCommand(waypointService));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        instance = null;
    }
}
