package com.knockk.api.data.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.knockk.api.util.entity.UnitEntity;

/**
 * Interface of the unit repository.
 * 
 * @author graceradlund
 */
public interface UnitRepository extends CrudRepository<UnitEntity, UUID> {

    /**
     * Retrieves a unit given the building id, floor, and room number
     * 
     * @param buildingId : id of the building
     * @param floor      : floor the unit is on
     * @param room       : room number of the unit
     * @return an optional id if the unit was found
     */
    @Query(value = "SELECT unit_id FROM \"Unit\" where fk_building_id = : buildingId AND floor = :floor  AND room = :room")
    public Optional<UUID> findUnitId(UUID buildingId, int floor, int room);
}
