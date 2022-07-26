package de.flowwindustries.flowfasttravel.service;

import de.flowwindustries.flowfasttravel.domain.Waypoint;

import java.util.Collection;
import java.util.Optional;

public interface WaypointService {

    Waypoint create(Waypoint waypoint) throws IllegalArgumentException;

    Waypoint getSafe(String name) throws IllegalArgumentException;

    Optional<Waypoint> get(String name);

    Collection<Waypoint> getAll();

    Waypoint update(String name, Waypoint waypoint) throws IllegalArgumentException;

    void delete(String name);
}
