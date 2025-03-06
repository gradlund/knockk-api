package com.knockk.api.model;

import java.util.Date;
import java.util.UUID;

/**
 * AdminResidentModel that is commonly used in the response
 * 
 * NOTE: AdminResidentEntity has verified property, but model does not
 */
public class AdminResidentModel extends LeaseModel {

    private UUID residentId;
    private String firstName;
    private String lastName;
    private String email;
    // private Boolean verified;

    /* TODO - get rid of leaseId? */
    public AdminResidentModel(UUID buildingId, UUID residentId, String firstName, String lastName, String email,
            int floor, int room, Date startDate, Date endDate) {
        super(buildingId, floor, room, startDate, endDate);
        this.residentId = residentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        // this.verified = verified;
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

    public String getEmail() {
        return email;
    }
    // public Boolean isVerified() {
    // return verified;
    // }

}
