package com.knockk.api.util.entity;

import java.sql.Date;
import java.util.UUID;

import org.springframework.data.relational.core.mapping.Column;

import com.knockk.api.util.Gender;

/**
 * NOTE: this is generally not practice. It is better to replicate the tables of
 * a database directly.
 */
// @Entity
public class AdminResidentEntity {

    private UUID buildingId;
    private UUID residentId;
    private String firstName;
    private String lastName;
    private Gender gender;
    private String email;
    private int floor;
    private int room;
    @Column("start_date")
    private Date leaseStart;
    @Column("end_date")
    private Date leaseEnd;
    private Boolean verified;

    public UUID getBuildingId() {
        return buildingId;
    }

    public UUID getResidentId() {
        return residentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public int getFloor() {
        return floor;
    }

    public int getRoom() {
        return room;
    }

    public Date getLeaseStart() {
        return leaseStart;
    }

    public Date getLeaseEnd() {
        return leaseEnd;
    }

    public Boolean isVerified() {
        return verified;
    }

    public AdminResidentEntity(UUID buildingId, UUID residentId, String firstName, String lastName, Gender gender,
            String email,
            int floor, int room, Date leaseStart, Date leaseEnd, Boolean verified) {
        this.buildingId = buildingId;
        this.residentId = residentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.email = email;
        this.floor = floor;
        this.room = room;
        this.leaseStart = leaseStart;
        this.leaseEnd = leaseEnd;
        this.verified = verified;
    }
}
