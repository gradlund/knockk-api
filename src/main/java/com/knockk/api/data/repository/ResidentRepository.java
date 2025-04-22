package com.knockk.api.data.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.knockk.api.util.Gender;
import com.knockk.api.util.entity.AdminResidentEntity;
import com.knockk.api.util.entity.ResidentEntity;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

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
	 * Retrieves entites given the building id, and based on whether they are
	 * verified, and pagable options
	 * 
	 * @param buildingId : id of the building the residents are being retireved from
	 * @param verified   : if the resident are verified or not
	 * @param limit      : max number of residents to return
	 * @param offset     : the page number
	 * @return a list of admin resident entites based on arguments
	 */
	@Query(value = "SELECT \"Resident\".resident_id, \"Resident\".first_name, \"Resident\".last_name, \"Resident\".gender, \"User\".email, \"Unit\".floor, \"Unit\".room, \"Lease\".start_date, \"Lease\".end_date, \"Resident\".verified from \"Building\""
			+
			"  INNER JOIN \"Unit\" ON \"Unit\".fk_building_id = \"Building\".building_id" +
			"  INNER JOIN \"Lease\" ON \"Lease\".fk_unit_id = \"Unit\".unit_id" +
			"  INNER JOIN \"Resident\" ON \"Resident\".fk_lease_id = \"Lease\".lease_id" +
			"  INNER JOIN \"User\" ON \"User\".user_id = \"Resident\".resident_id" +
			"  where \"Building\".building_id = :buildingId AND \"Resident\".verified = :verified" +

			"  ORDER BY \"Resident\".resident_id LIMIT :limit OFFSET :offset")
	List<AdminResidentEntity> findAllByBuildingIdAndVerification(UUID buildingId, boolean verified, int limit,
			long offset);

	/**
	 * Retrieves entites given the building id, and based on whether they are
	 * verified, pagable options, and sorts by floor ascending
	 * 
	 * @param buildingId : id of the building the residents are being retrieved from
	 * @param verified   : if the resident are verified or not
	 * @param limit      : max number of residents to return
	 * @param offset     : the page number
	 * @return a list of admin resident entities based on arguments, and sorts them
	 *         by floor ascending
	 */
	@Query(value = "SELECT \"Resident\".resident_id, \"Resident\".first_name, \"Resident\".last_name, \"Resident\".gender, \"User\".email, \"Unit\".floor, \"Unit\".room, \"Lease\".start_date, \"Lease\".end_date, \"Resident\".verified from \"Building\""
			+
			"  INNER JOIN \"Unit\" ON \"Unit\".fk_building_id = \"Building\".building_id" +
			"  INNER JOIN \"Lease\" ON \"Lease\".fk_unit_id = \"Unit\".unit_id" +
			"  INNER JOIN \"Resident\" ON \"Resident\".fk_lease_id = \"Lease\".lease_id" +
			"  INNER JOIN \"User\" ON \"User\".user_id = \"Resident\".resident_id" +
			"  where \"Building\".building_id = :buildingId AND \"Resident\".verified = :verified" +

			"  ORDER BY \"Unit\".floor, \"Unit\".room, \"Resident\".first_name LIMIT :limit OFFSET :offset")
	List<AdminResidentEntity> findAllByBuildingIdAndVerificationSortByFloor(UUID buildingId, boolean verified,
			int limit, long offset);

	/**
	 * Retrieves entites given the building id, and based on whether they are
	 * verified, pagable options, and sorts by floor descending
	 * 
	 * @param buildingId : id of the building the residents are being retrieved from
	 * @param verified   : if the resident are verified or not
	 * @param limit      : max number of residents to return
	 * @param offset     : the page number
	 * @return a list of admin resident entities based on arguments, and sorts them
	 *         by floor descending
	 */
	@Query(value = "SELECT \"Resident\".resident_id, \"Resident\".first_name, \"Resident\".last_name, \"Resident\".gender, \"User\".email, \"Unit\".floor, \"Unit\".room, \"Lease\".start_date, \"Lease\".end_date, \"Resident\".verified from \"Building\""
			+
			"  INNER JOIN \"Unit\" ON \"Unit\".fk_building_id = \"Building\".building_id" +
			"  INNER JOIN \"Lease\" ON \"Lease\".fk_unit_id = \"Unit\".unit_id" +
			"  INNER JOIN \"Resident\" ON \"Resident\".fk_lease_id = \"Lease\".lease_id" +
			"  INNER JOIN \"User\" ON \"User\".user_id = \"Resident\".resident_id" +
			"  where \"Building\".building_id = :buildingId AND \"Resident\".verified = :verified" +

			"  ORDER BY \"Unit\".floor DESC, \"Unit\".room, \"Resident\".first_name LIMIT :limit OFFSET :offset")
	List<AdminResidentEntity> findAllByBuildingIdAndVerificationSortByFloorDesc(UUID buildingId, boolean verified,
			int limit, long offset);

	/**
	 * Retrieves entites given the building id, and based on whether they are
	 * verified, pagable options, and sorts by last name ascending
	 * 
	 * @param buildingId : id of the building the residents are being retrieved from
	 * @param verified   : if the resident are verified or not
	 * @param limit      : max number of residents to return
	 * @param offset     : the page number
	 * @return a list of admin resident entities based on arguments, and sorts them
	 *         by last name ascending
	 */
	@Query(value = "SELECT \"Resident\".resident_id, \"Resident\".first_name, \"Resident\".last_name, \"Resident\".gender, \"User\".email, \"Unit\".floor, \"Unit\".room, \"Lease\".start_date, \"Lease\".end_date, \"Resident\".verified from \"Building\""
			+
			"  INNER JOIN \"Unit\" ON \"Unit\".fk_building_id = \"Building\".building_id" +
			"  INNER JOIN \"Lease\" ON \"Lease\".fk_unit_id = \"Unit\".unit_id" +
			"  INNER JOIN \"Resident\" ON \"Resident\".fk_lease_id = \"Lease\".lease_id" +
			"  INNER JOIN \"User\" ON \"User\".user_id = \"Resident\".resident_id" +
			"  where \"Building\".building_id = :buildingId AND \"Resident\".verified = :verified" +

			"  ORDER BY \"Resident\".last_name, \"Resident\".first_name LIMIT :limit OFFSET :offset")
	List<AdminResidentEntity> findAllByBuildingIdAndVerificationSortByLastName(UUID buildingId, boolean verified,
			int limit, long offset);

	/**
	 * Retrieves entites given the building id, and based on whether they are
	 * verified, pagable options, and sorts by last name descending
	 * 
	 * @param buildingId : id of the building the residents are being retrieved from
	 * @param verified   : if the resident are verified or not
	 * @param limit      : max number of residents to return
	 * @param offset     : the page number
	 * @return a list of admin resident entities based on arguments, and sorts them
	 *         by last name descending
	 */
	@Query(value = "SELECT \"Resident\".resident_id, \"Resident\".first_name, \"Resident\".last_name, \"Resident\".gender, \"User\".email, \"Unit\".floor, \"Unit\".room, \"Lease\".start_date, \"Lease\".end_date, \"Resident\".verified from \"Building\""
			+
			"  INNER JOIN \"Unit\" ON \"Unit\".fk_building_id = \"Building\".building_id" +
			"  INNER JOIN \"Lease\" ON \"Lease\".fk_unit_id = \"Unit\".unit_id" +
			"  INNER JOIN \"Resident\" ON \"Resident\".fk_lease_id = \"Lease\".lease_id" +
			"  INNER JOIN \"User\" ON \"User\".user_id = \"Resident\".resident_id" +
			"  where \"Building\".building_id = :buildingId AND \"Resident\".verified = :verified" +

			"  ORDER BY \"Resident\".last_name DESC, \"Resident\".first_name ASC LIMIT :limit OFFSET :offset")
	List<AdminResidentEntity> findAllByBuildingIdAndVerificationSortByLastNameDesc(UUID buildingId, boolean verified,
			int limit, long offset);

	/**
	 * Retireves the number of residents given the building id and whether they are
	 * verified
	 * 
	 * @param buildingId : id of the building
	 * @param verified   : if the residents are verified
	 * @return the number of residents based on the parameters
	 */
	@Query(value = "SELECT Count(\"Resident\".resident_id) from \"Building\"" +
			"  INNER JOIN \"Unit\" ON \"Unit\".fk_building_id = \"Building\".building_id" +
			"  INNER JOIN \"Lease\" ON \"Lease\".fk_unit_id = \"Unit\".unit_id" +
			"  INNER JOIN \"Resident\" ON \"Resident\".fk_lease_id = \"Lease\".lease_id" +
			"  where \"Building\".building_id = :buildingId AND \"Resident\".verified = :verified")
	public int retrieveNumberOfResidents(UUID buildingId, boolean verified);

	/**
	 * Retrieves a resident based on their id
	 * 
	 * @param residentId : id of the resident being retrieved
	 * @return an optional admin resident entity
	 */
	@Query(value = "SELECT \"Building\".building_id, \"Resident\".resident_id, \"Resident\".first_name, \"Resident\".last_name, \"Resident\".gender, \"User\".email, \"Unit\".floor, \"Unit\".room, \"Lease\".start_date, \"Lease\".end_date, \"Resident\".verified from \"Building\""
			+
			"  INNER JOIN \"Unit\" ON \"Unit\".fk_building_id = \"Building\".building_id" +
			"  INNER JOIN \"Lease\" ON \"Lease\".fk_unit_id = \"Unit\".unit_id" +
			"  INNER JOIN \"Resident\" ON \"Resident\".fk_lease_id = \"Lease\".lease_id" +
			"  INNER JOIN \"User\" ON \"User\".user_id = \"Resident\".resident_id" +
			"  where \"Resident\".resident_id = :residentId")
	Optional<AdminResidentEntity> findResidentById(UUID residentId);

	/**
	 * Retrieves a list of resident id's based on the unit number
	 * 
	 * @param floor : floor of the unit
	 * @param room  : room number of the unit
	 * @return a list of resident's id's
	 */
	@Query(value = "SELECT \"Resident\".resident_id from \"Unit\"" +
			"  INNER JOIN \"Lease\" ON \"Lease\".fk_unit_id = \"Unit\".unit_id" +
			"  INNER JOIN \"Resident\" ON \"Resident\".fk_lease_id = \"Lease\".lease_id" +
			"  where \"Unit\".floor = :floor AND \"Unit\".room = :room AND verified = true")
	List<UUID> findResidentsByUnit(int floor, int room);

	/**
	 * Creates a row in the Resident table based on the arguments passed on
	 * 
	 * @param id              : id of the resident (created when registering their
	 *                        email)
	 * @param firstName       : first name of the resident
	 * @param lastName        : last name of the resident
	 * @param age             : age of the resident
	 * @param hometown        : hometown of the resident (optional field)
	 * @param biography       : biography of the resident (optional field)
	 * @param profilePhoto    : profile photo the resident provided (optional field)
	 * @param backgroundPhoto : background photo the resident provided (optional
	 *                        field)
	 * @param instagram       : instagram handle of the resident (optional field)
	 * @param snapchat        : snapchat handle of the resident (optional field)
	 * @param x               : x handle of the resident (optional field)
	 * @param facebook        : facebook handle of the resident (optional field)
	 * @param gender          : the resident's gender
	 * @param leaseId         : the lease id
	 * @param verified        : if the resident has been verified by admin
	 * @return an integer if successfully inserted
	 */
	@Modifying
	@Query(value = "INSERT INTO \"Resident\" (resident_id, first_name, last_name, age, hometown, biography, profile_photo, background_photo, "
			+
			"instagram, snapchat, x, facebook, gender, fk_lease_id, verified) VALUES " +
			"(:id, :firstName, :lastName, :age, :hometown, :biography, " +
			"CAST('\"' || :backgroundPhoto || '\"' AS JSONB), " +
			"CAST('\"' || :backgroundPhoto || '\"' AS JSONB), " +
			":instagram, :snapchat, :x, :facebook, CAST(:gender AS \"Gender\"), :leaseId, :verified)")

	int register(UUID id, String firstName, String lastName, int age, String hometown, String biography,
			String profilePhoto, String backgroundPhoto,
			String instagram, String snapchat, String x, String facebook, Gender gender, UUID leaseId,
			boolean verified);

	/**
	 * Verifies a resident. Method used by admin.
	 * Modifying annotation returns a value
	 * 
	 * @param residentId : id of the resident
	 * @return 1 if sucessfully updated
	 */
	@Modifying
	@Query(value = "UPDATE \"Resident\" SET verified = true WHERE \"Resident\".resident_id = :residentId")
	int activate(UUID residentId);

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
			"instagram = :instagram, snapchat = :snapchat, x = :x, facebook = :facebook " +
			" WHERE \"Resident\".resident_id = :id")
	int update(int age, String hometown, String biography, String profilePhoto, String backgroundPhoto,
			String instagram, String snapchat, String x, String facebook, UUID id);
}
