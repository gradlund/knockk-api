package com.knockk.api.data.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.knockk.api.entity.BuildingEntity;

/**
 * Interface for the building repository
 * 
 * @author graceradlund
 */
public interface BuildingRepository  extends CrudRepository<BuildingEntity, UUID> {
    
}
