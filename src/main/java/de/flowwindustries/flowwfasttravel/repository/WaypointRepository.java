package de.flowwindustries.flowwfasttravel.repository;

import de.flowwindustries.flowwfasttravel.domain.Waypoint;

/**
 * Waypoint Repository.
 */
public class WaypointRepository extends AbstractRepository<Waypoint, String> {
    public WaypointRepository(Class<Waypoint> entityClass) {
        super(entityClass);
    }
}
