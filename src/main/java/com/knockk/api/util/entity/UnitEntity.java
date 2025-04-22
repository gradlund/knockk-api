package com.knockk.api.util.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Entity class for the Unit table.
 */
@Table("Unit")
public class UnitEntity {
    // Make sure the column names match
    @Id
    @Column("unit_id")
    private UUID unitId;

    @Column("floor")
    private int floor;

    @Column("room")
    private int room;

    @Column("number_of_bedrooms")
    private int numberOfBedrooms;

    @Column("fk_building_id")
    private UUID buildingId;

    public UnitEntity(UUID unitId, int floor, int room, int numberOfBedrooms, UUID buildingId) {
        this.unitId = unitId;
        this.floor = floor;
        this.room = room;
        this.numberOfBedrooms = numberOfBedrooms;
        this.buildingId = buildingId;
    }

    public int getFloor() {
        return floor;
    }

    public int getRoom() {
        return room;
    }

    public UUID getBuildingId() {
        return buildingId;
    }
}
