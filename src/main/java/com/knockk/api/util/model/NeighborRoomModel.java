package com.knockk.api.util.model;

/*
 * Model used in a response when a user logs in to see what rooms they are neighboring with.
 */
public class NeighborRoomModel {
    private String direction;
    private int floor;
    private int room;

    public NeighborRoomModel(String direction, int floor, int room) {
        this.direction = direction;
        this.floor = floor;
        this.room = room;
    }

    // Getters needed for making models
    public String getDirection() {
        return this.direction;
    }

    public int getFloor() {
        return floor;
    }

    public int getRoom() {
        return room;
    }

}
