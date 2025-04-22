package com.knockk.api.data.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.knockk.api.util.entity.BuildingEntity;

/**
 * Interface for the building repository
 * 
 * @author graceradlund
 */
public interface BuildingRepository extends CrudRepository<BuildingEntity, UUID> {

    /**
     * Retrieves a list of building entities
     * 
     * @param address : street address of the building(s)
     * @return a list of building entites that have the address provided
     */
    @Query(value = "SELECT * from \"Building\" WHERE address = :address")
    public List<BuildingEntity> findByAddress(String address);

    /**
     * Retrieves the id of the building given it's address and name
     * 
     * @param address : street address of the building
     * @param name    : name of the building
     * @return an optional building id if one exists
     */
    @Query(value = "SELECT building_id from \"Building\" WHERE address = :address AND name = :name")
    public Optional<UUID> findByAddressAndName(String address, String name);

    /**
     * Retrieves a list of buildings given the admin's id
     * 
     * @param adminId : id of the admin who manages buildings
     * @return a list of buildings the admin manages
     */
    @Query(value = "SELECT * from \"Building\" WHERE fk_admin_id = :adminId")
    public List<BuildingEntity> findAllByAdminId(UUID adminId);
}
