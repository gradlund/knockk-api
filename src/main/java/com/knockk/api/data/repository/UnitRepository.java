package com.knockk.api.data.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.knockk.api.entity.UnitEntity;

/**
 * Interface of the unit repository.
 * 
 * @author graceradlund
 */
public interface UnitRepository extends CrudRepository<UnitEntity, UUID> {

    // @Query(value = "SELECT * FROM \"Unit\" where floor = :floor  AND room = :room")
	// public List<UnitEntity> findByFloorAndRoom(int floor, int room);

    // @Query(value = "SELECT unit_id FROM \"Unit\" where floor = :floor  AND room = :room")
    // public Optional<UUID> findUnitIdByFloorAndRoom(int floor, int room);

     @Query(value = "SELECT unit_id FROM \"Unit\" where fk_building_id = : buildingId AND floor = :floor  AND room = :room")
    public Optional<UUID> findUnitId(UUID buildingId, int floor, int room);
}
