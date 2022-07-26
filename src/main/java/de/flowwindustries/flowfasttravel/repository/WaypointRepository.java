package de.flowwindustries.flowfasttravel.repository;

import de.flowwindustries.flowfasttravel.domain.Waypoint;

/**
 * Waypoint Repository.
 */
public class WaypointRepository extends AbstractRepository<Waypoint, String> {
    public WaypointRepository(Class<Waypoint> entityClass) {
        super(entityClass);
    }
}
