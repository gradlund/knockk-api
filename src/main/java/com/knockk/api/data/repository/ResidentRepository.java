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

	/**
	 * Updates a resident's optional fields.
	 * 
	 * NOTE: modifying and transactional annotations to show this query modifies
	 * data.
	 * 
	 * @param age             : age of the resident
	 * @param hometown        : hometown of the resident
	 * @param biography       : biography of the resident
	 * @param profilePhoto    : profile photo the resident has chosen
	 * @param backgroundPhoto : background photo the resident has chosen
	 * @param instagram       : instagram of the resident
	 * @param snapchat        : snapchat account of the resident
	 * @param x               : x account of the resident
	 * @param facebook        : facebook of the resident
	 * @param id              : id of the resident
	 * @return the number of rows affected
	 */
	@Modifying
	@Transactional
	@Query(value = "UPDATE \"Resident\" SET age = :age, hometown = :hometown, biography = :biography, " +
			"profile_photo = CAST('\"' || :profilePhoto || '\"' AS JSONB), " +
			"background_photo = CAST('\"' || :backgroundPhoto || '\"' AS JSONB), " +
			"instagram = :instagram, snapchat = :snapchat, x = :x, facebook = facebook " +
			" WHERE \"Resident\".resident_id = :id")
	int update(int age, String hometown, String biography, String profilePhoto, String backgroundPhoto,
			String instagram, String snapchat, String x, String facebook, UUID id);
}
