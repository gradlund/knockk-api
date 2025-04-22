package com.knockk.api.util.model;

import jakarta.validation.constraints.NotNull;

/**
 * Model for registering a resident. Extends the OptionalResidentModel which
 * contains optional fields.
 */
public class RegisterModel extends OptionalResidentModel {
    @NotNull
    private String id;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String gender; // enum?
    @NotNull
    private String leaseId;

    public RegisterModel(String id, String firstName,
            String lastName, String gender, String leaseId, int age, String hometown, String biography,
            String profilePhoto, String backgroundPhoto,
            String instagram, String snapchat, String x, String facebook) {
        super(age, hometown, biography, profilePhoto, backgroundPhoto, instagram, snapchat, x, facebook);
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.leaseId = leaseId;
    }

    // Need getters for request body
    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getLeaseId() {
        return leaseId;
    }
}
