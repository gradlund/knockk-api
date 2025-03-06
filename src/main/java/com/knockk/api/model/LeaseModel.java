package com.knockk.api.model;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;
import java.util.Date;

/**
 * Super class model that the AdminResidentModel extends.
 */
public class LeaseModel {

    
    @NotNull
    private UUID buildingId;
    @NotNull
    private int floor;
    @NotNull
    private int room;
    @NotNull
    private Date startDate;
    @NotNull
    private Date endDate;

    public LeaseModel(UUID buildingId, int floor, int room, Date startDate,
            Date endDate) {
        this.buildingId = buildingId;
        this.floor = floor;
        this.room = room;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public UUID getBuildingId() {
        return buildingId;
    }

    public int getFloor() {
        return floor;
    }

    public int getRoom() {
        return room;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

}
