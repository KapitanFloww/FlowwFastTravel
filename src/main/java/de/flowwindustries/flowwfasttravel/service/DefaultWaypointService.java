package de.flowwindustries.flowwfasttravel.service;

import de.flowwindustries.flowwfasttravel.domain.Waypoint;
import de.flowwindustries.flowwfasttravel.repository.WaypointRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Log
@Transactional
@RequiredArgsConstructor
public class DefaultWaypointService implements WaypointService {

    private static final String WAYPOINT_NOT_FOUND = "Waypoint %s not found";
    private static final String DUPLICATE_WAYPOINT_ID = "Waypoint %s already exists";
    private static final String INVALID_WAYPOINT_ID = "Invalid waypoint name: %s";
    private static final List<String> INVALID_IDS = List.of("help", "list", "create", "update");

    private final Clock clock;
    private final WaypointRepository waypointRepository;

    @Override
    public Waypoint createWaypoint(Waypoint waypoint) throws IllegalArgumentException {
        assertUniqueName(waypoint.getName());
        if(INVALID_IDS.contains(waypoint.getName())) {
            throw new IllegalArgumentException(String.format(INVALID_WAYPOINT_ID, waypoint.getName()));
        }
        log.info("Creating waypoint: " + waypoint);
        return waypointRepository.create(waypoint);
    }

    @Override
    public Waypoint getWaypoint(String name) throws NoSuchElementException {
        return this.findWaypoint(name).orElseThrow(() -> new NoSuchElementException(String.format(WAYPOINT_NOT_FOUND, name)));
    }

    @Override
    public List<Waypoint> getAll() {
        log.config("Listing all waypoints");
        return waypointRepository.findAll().stream().toList();
    }

    @Override
    public Waypoint updateWaypoint(String name, Waypoint waypoint) throws IllegalArgumentException {
        Waypoint oldWaypoint = this.getWaypoint(name);

        log.info("Updating waypoint: " + name);

        // If names do not match delete the old waypoint and create a new one
        if(!Objects.equals(oldWaypoint.getName(), waypoint.getName())) {
            this.deleteWaypoint(oldWaypoint.getName());
            return this.createWaypoint(waypoint);
        }

        // Update the waypoint
        oldWaypoint.setCost(waypoint.getCost());
        oldWaypoint.setDescription(waypoint.getDescription());
        oldWaypoint.setWorld(waypoint.getWorld());
        oldWaypoint.setX(waypoint.getX());
        oldWaypoint.setY(waypoint.getY());
        oldWaypoint.setZ(waypoint.getZ());

        oldWaypoint.setLastModifiedAt(Instant.now(clock));
        return waypointRepository.edit(oldWaypoint);
    }

    @Override
    public void deleteWaypoint(String name) {
        log.info("Removing waypoint: " + name);
        Waypoint waypoint = this.getWaypoint(name);
        waypointRepository.remove(waypoint);
    }

    @Override
    public void init() {
        final long count = waypointRepository.findAll().size();
        log.info("Found %s waypoints".formatted(count));
    }

    private Optional<Waypoint> findWaypoint(String name) {
        log.config("Getting waypoint: " + name);
        return waypointRepository.find(name);
    }

    private void assertUniqueName(String name) throws IllegalArgumentException {
        if(this.findWaypoint(name).isPresent()) {
            throw new IllegalArgumentException(String.format(DUPLICATE_WAYPOINT_ID, name));
        }
    }
}
