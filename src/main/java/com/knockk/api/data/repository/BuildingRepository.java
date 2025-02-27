package com.knockk.api.data.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.knockk.api.entity.BuildingEntity;

/**
 * Interface for the building repository
 * 
 * @author graceradlund
 */
public interface BuildingRepository extends CrudRepository<BuildingEntity, UUID> {

    // @Query(value = "SELECT name from \"Building\" WHERE address = :address")
    @Query(value = "SELECT * from \"Building\" WHERE address = :address")
    public Optional<BuildingEntity> findByAddress(String address); // TODO: could return a list of buildingentity

    @Query(value = "SELECT building_id from \"Building\" WHERE address = :address AND name = :name")
    public Optional<UUID> findByAddressAndName(String address, String name);

    @Query(value = "SELECT * from \"Building\" WHERE fk_admin_id = :adminId")
    public List<BuildingEntity> findAllByAdminId(UUID adminId);
}
