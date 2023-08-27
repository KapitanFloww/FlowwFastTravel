package de.flowwindustries.flowwfasttravel.service;

import de.flowwindustries.flowwfasttravel.domain.Waypoint;

import java.util.List;
import java.util.NoSuchElementException;

public interface WaypointService {

    Waypoint createWaypoint(Waypoint waypoint) throws IllegalArgumentException;

    Waypoint getWaypoint(String name) throws NoSuchElementException;

    List<Waypoint> getAll();

    Waypoint updateWaypoint(String name, Waypoint waypoint) throws IllegalArgumentException;

    void deleteWaypoint(String name);

    void init();
}
