package com.knockk.api.model;

import java.util.UUID;

/**
 * Model used in the response when checking for residents in a unit.
 */
public class UnitResidentModel {
    private UUID residentId;
    private String name;
    private String profilePhoto;
    private boolean isConnected;

    public UnitResidentModel(UUID residentId, String name, String profilePhoto, boolean isConnected) {
        this.residentId = residentId;
        this.name = name;
        this.profilePhoto = profilePhoto;
        this.isConnected = isConnected;
    }

    public UnitResidentModel(UUID residentId, String name, boolean isConnected) {
        this.residentId = residentId;
        this.name = name;
        this.profilePhoto = null;
        this.isConnected = isConnected;
    }

    public UUID getResidentId() {
        return residentId;
    }
    public void setResidentId(UUID residentId) {
        this.residentId = residentId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getProfilePhoto() {
        return profilePhoto;
    }
    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
    public boolean isConnected() {
        return isConnected;
    }
    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }
}
