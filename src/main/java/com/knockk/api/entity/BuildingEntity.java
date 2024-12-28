package com.knockk.api.entity;

import java.util.ArrayList;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Entity class for the Building table.
 */
@Table("Building")
public class BuildingEntity {
    // Make sure the column names match
    @Id
    @Column("building_id")
    private UUID buildingId;

    @Column("name")
    private String name;

    @Column("number_of_rooms")
    private int numberOfRoom;

    @Column("top_floor")
    private int topFloor;

    @Column("bottom_floor")
    private int bottomFloor;

    @Column("no_room_right")
    private ArrayList<Integer> noRoomsRight;

    @Column("no_room_left")
    private ArrayList<Integer> noRoomsLeft;

    @Column("fk_admin_id")
    private UUID adminId;

    public BuildingEntity(UUID buildingId, String name, int numberOfRoom, int topFloor, int bottomFloor,
            ArrayList<Integer> noRoomsRight, ArrayList<Integer> noRoomsLeft, UUID adminId) {
        this.buildingId = buildingId;
        this.name = name;
        this.numberOfRoom = numberOfRoom;
        this.topFloor = topFloor;
        this.bottomFloor = bottomFloor;
        this.noRoomsRight = noRoomsRight;
        this.noRoomsLeft = noRoomsLeft;
        this.adminId = adminId;
    }

    public int getTopFloor() {
        return topFloor;
    }

    public int getBottomFloor() {
        return bottomFloor;
    }

    public ArrayList<Integer> getNoRoomsRight() {
        return noRoomsRight;
    }

    public ArrayList<Integer> getNoRoomsLeft() {
        return noRoomsLeft;
    }
    
}
