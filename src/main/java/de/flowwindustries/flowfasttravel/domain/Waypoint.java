package de.flowwindustries.flowfasttravel.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.Instant;

/**
 * Waypoint entity.
 */
@Data
@Entity
public class Waypoint {

    /**
     * Name, the pair of name and world name have to be unique.
     */
    @Id
    private String name;
    /**
     * Description.
     */
    private String description;

    /**
     * Costs to teleport to this waypoint.
     */
    private Float cost;

    /**
     * X-Coordinate.
     */
    private Double x;

    /**
     * Y-Coordinate.
     */
    private Double y;

    /**
     * Z-Coordinate.
     */
    private Double z;

    /**
     * Name of the world.
     */
    private String worldName;

    /*
     * Creation date.
     */
    private Instant createdAt;

    /**
     * Modification date.
     */
    private Instant lastModifiedAt;

    public Waypoint(String name, String description, Float cost, Double x, Double y, Double z, String worldName) {
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
    }

    public Waypoint() {}
}