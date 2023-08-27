package de.flowwindustries.flowwfasttravel.service;

import de.flowwindustries.flowwfasttravel.domain.Waypoint;
import lombok.extern.java.Log;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Log
public class CachedWaypointService implements WaypointService {

    private final Map<String, Waypoint> waypointCache;
    private final WaypointService delegate;

    public CachedWaypointService(WaypointService delegate) {
        this.delegate = Objects.requireNonNull(delegate);
        this.waypointCache = new ConcurrentHashMap<>();
    }

    @Override
    public Waypoint createWaypoint(Waypoint waypoint) throws IllegalArgumentException {
        final var savedWaypoint = delegate.createWaypoint(waypoint);
        putCache(savedWaypoint);
        return savedWaypoint;
    }

    @Override
    public Waypoint getWaypoint(String name) throws NoSuchElementException {
        if (!waypointCache.containsKey(name)) {
            return delegate.getWaypoint(name);
        }
        return waypointCache.get(name);
    }

    @Override
    public List<Waypoint> getAll() {
        return waypointCache.values().stream().toList();
    }

    @Override
    public Waypoint updateWaypoint(String name, Waypoint waypoint) throws IllegalArgumentException {
        evictCache(name);
        return delegate.updateWaypoint(name, waypoint);
    }

    @Override
    public void deleteWaypoint(String name) {
        evictCache(name);
        delegate.deleteWaypoint(name);
    }

    @Override
    public void init() {
        waypointCache.clear();
        delegate.init();
        delegate.getAll().forEach(this::putCache);
    }

    private void evictCache(String name) {
        log.config("CacheEvict for waypoint: " + name);
        waypointCache.remove(name);
    }

    private void putCache(Waypoint waypoint) {
        log.config("CachePut for waypoint: " + waypoint.getName());
        waypointCache.put(waypoint.getName(), waypoint);
    }
}
