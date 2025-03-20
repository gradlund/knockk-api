package com.knockk.api.model;

import java.util.UUID;

/**
 * Building model commonly used in the response.
 */
public class BuildingModel {
    private String name;
    private UUID id;

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    public BuildingModel(String name, UUID id) {
        this.name = name;
        this.id = id;
    }

}
