package com.knockk.api.data.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.knockk.api.entity.ResidentEntity;

/**
 * Interface for the resident repository.
 * 
 * @author graceradlund
 */
public interface ResidentRepository extends CrudRepository<ResidentEntity, UUID> {

	/**
	 * TODO: Error with PG convertor
	 * Retrieves the resident by id
	 * 
	 * @param residentId : id of the resident
	 * @return the resident
	 */
	@Query(value = "SELECT * from \"Resident\" where resident_id = :residentId")
	Optional<ResidentEntity> findById(UUID residentId);

	/**
	 * Retrieves the id of the lease, given the their id
	 * 
	 * @param residentId : id of the resident
	 * @return the lease's id if it exists
	 */
	@Query(value = "SELECT fk_lease_id from \"Resident\" WHERE resident_id = :residentId")
	Optional<UUID> findLeaseIdByResidentId(UUID residentId);

	// Need "" or Supabase otherwise table will not be found
	// switch to returning a list of residents instead - or optional if a room has
	// no residents
	// @Query(value = "select exists(" +
	// " select \"Resident\".resident_id from \"Unit\"" +
	// " INNER JOIN \"Lease\" ON \"Lease\".fk_unit_id = \"Unit\".unit_id" +
	// " INNER JOIN \"Resident\" ON \"Resident\".fk_lease_id = \"Lease\".lease_id" +
	// " where \"Unit\".floor = :floor AND \"Unit\".room = :room" +
	// " )")
	// List<UUID> findResidentsByUnitBad(int floor, int room);

	/**
	 * Retrieves UUIDs of resident given the unit information
	 * 
	 * @param floor : the unit is on
	 * @param room  : the unit is in
	 * @return a list of ids of residents in that unit
	 */
	@Query(value = "SELECT \"Resident\".resident_id from \"Unit\"" +
			"  INNER JOIN \"Lease\" ON \"Lease\".fk_unit_id = \"Unit\".unit_id" +
			"  INNER JOIN \"Resident\" ON \"Resident\".fk_lease_id = \"Lease\".lease_id" +
			"  where \"Unit\".floor = :floor AND \"Unit\".room = :room")
	List<UUID> findResidentsByUnit(int floor, int room);

	@Modifying
	@Transactional
	// @Query(value = "UPDATE \"Resident\" SET age = :age, hometown = :hometown, biography = :biography, profile_photo = :profilePhoto::jsonb, " +
	// " background_photo = :backgroundPhoto::jsonb, instagram = :instagram, snapchat = :snapchat, x = :x, facebook = facebook " +
	// " WHERE \"Resident\".resident_id = :id")
	@Query(value = "UPDATE \"Resident\" SET age = :age, hometown = :hometown, biography = :biography, " +
	// "profile_photo = CAST('\"' || :profilePhoto || '\"' AS JSONB), " +
    //            "background_photo = CAST('\"' || :backgroundPhoto || '\"' AS JSONB), " +
	"profile_photo = CAST('\"' || :profilePhoto || '\"' AS JSONB), " +
               "background_photo = CAST('\"' || :backgroundPhoto || '\"' AS JSONB), " +
	"instagram = :instagram, snapchat = :snapchat, x = :x, facebook = facebook " +
	" WHERE \"Resident\".resident_id = :id")
    int update(int age, String hometown, String biography, String profilePhoto, String backgroundPhoto, String instagram, String snapchat, String x, String facebook, UUID id);
}
