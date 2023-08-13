package de.flowwindustries.flowfasttravel.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

import java.time.Instant;

/**
 * Waypoint entity.
 */
@With
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "waypoints", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_waypoints_name_world",
                columnNames = "name, world"
        )}
)
public class Waypoint {

    /**
     * Name, the pair of name and world name have to be unique.
     */
    @Id
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Name of the world.
     */
    @Column(name = "world", nullable = false)
    private String world;

    /**
     * Costs to teleport to this waypoint.
     */
    @Column(name = "cost", nullable = false)
    private Float cost;

    /**
     * X-Coordinate.
     */
    @Column(name = "x", nullable = false, precision = 2)
    private Double x;

    /**
     * Y-Coordinate.
     */
    @Column(name = "y", nullable = false, precision = 2)
    private Double y;

    /**
     * Z-Coordinate.
     */
    @Column(name = "z", nullable = false, precision = 2)
    private Double z;

    /**
     * Description.
     */
    @Column(name = "description")
    private String description;

    /*
     * Creation date.
     */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /**
     * Modification date.
     */
    @Column(name = "last_modified_at")
    private Instant lastModifiedAt;
}