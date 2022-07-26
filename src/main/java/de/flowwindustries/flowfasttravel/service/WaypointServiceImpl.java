package de.flowwindustries.flowfasttravel.service;

import de.flowwindustries.flowfasttravel.domain.Waypoint;
import de.flowwindustries.flowfasttravel.repository.WaypointRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.time.Clock;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Log
@Transactional
@RequiredArgsConstructor
public class WaypointServiceImpl implements WaypointService {

    private static final String WAYPOINT_NOT_FOUND = "Waypoint %s not found";
    private static final String DUPLICATE_WAYPOINT_ID = "Waypoint %s already exists";
    private static final String INVALID_WAYPOINT_ID = "Invalid waypoint name: %s";
    private static final List<String> INVALID_IDS = List.of("help", "list", "create", "update");

    private final Clock clock;
    private final WaypointRepository waypointRepository;

    @Override
    public Waypoint create(Waypoint waypoint) throws IllegalArgumentException {
        assertUniqueName(waypoint.getName());
        if(INVALID_IDS.contains(waypoint.getName())) {
            throw new IllegalArgumentException(String.format(INVALID_WAYPOINT_ID, waypoint.getName()));
        }
        log.info("Creating waypoint: " + waypoint);
        return waypointRepository.create(waypoint);
    }

    @Override
    public Waypoint getSafe(String name) throws IllegalArgumentException {
        return this.get(name).orElseThrow(() -> new IllegalArgumentException(String.format(WAYPOINT_NOT_FOUND, name)));
    }

    @Override
    public Optional<Waypoint> get(String name) {
        log.info("Getting waypoint: " + name);
        return waypointRepository.find(name);
    }

    @Override
    public Collection<Waypoint> getAll() {
        log.info("Listing all waypoints");
        return waypointRepository.findAll();
    }

    @Override
    public Waypoint update(String name, Waypoint waypoint) throws IllegalArgumentException {
        Waypoint oldWaypoint = this.getSafe(name);

        log.info("Updating waypoint: " + name);

        // If names do not match delete the old waypoint and create a new one
        if(!Objects.equals(oldWaypoint.getName(), waypoint.getName())) {
            this.delete(oldWaypoint.getName());
            return this.create(waypoint);
        }

        // Update the waypoint
        oldWaypoint.setCost(waypoint.getCost());
        oldWaypoint.setDescription(waypoint.getDescription());
        oldWaypoint.setWorldName(waypoint.getWorldName());
        oldWaypoint.setX(waypoint.getX());
        oldWaypoint.setY(waypoint.getY());
        oldWaypoint.setZ(waypoint.getZ());

        oldWaypoint.setLastModifiedAt(Instant.now(clock));
        return waypointRepository.edit(oldWaypoint);
    }

    @Override
    public void delete(String name) {
        log.info("Removing waypoint: " + name);
        Waypoint waypoint = this.getSafe(name);
        waypointRepository.remove(waypoint);
    }

    private void assertUniqueName(String name) throws IllegalArgumentException {
        if(this.get(name).isPresent()) {
            throw new IllegalArgumentException(String.format(DUPLICATE_WAYPOINT_ID, name));
        }
    }
}
