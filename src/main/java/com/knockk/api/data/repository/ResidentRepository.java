package com.knockk.api.data.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.knockk.api.data.Gender;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.knockk.api.entity.AdminResidentEntity;
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


	// @Transactional
	// @Query(value = "SELECT \"Resident\".resident_id, \"Resident\".first_name, \"Resident\".last_name, \"Resident\".gender, \"User\".email, \"Unit\".floor, \"Unit\".room, \"Lease\".start_date, \"Lease\".end_date, \"Resident\".verified from \"Building\""
	// 		+
	// 		"  INNER JOIN \"Unit\" ON \"Unit\".fk_building_id = \"Building\".building_id" +
	// 		"  INNER JOIN \"Lease\" ON \"Lease\".fk_unit_id = \"Unit\".unit_id" +
	// 		"  INNER JOIN \"Resident\" ON \"Resident\".fk_lease_id = \"Lease\".lease_id" +
	// 		"  INNER JOIN \"User\" ON \"User\".user_id = \"Resident\".resident_id" +
	// 		"  where \"Building\".building_id = :buildingId AND \"Resident\".verified = :verified" +
			
	// 		"  ORDER BY :sortBy ASC LIMIT :limit OFFSET :offset"
	// 		)
	// 		//" \"Resident\".:orderBy "+
			
			
	// // ORDERBY
	// // countQuery = "SELECT count(*)" +
	// // " INNER JOIN \"Unit\" ON \"Unit\".fk_building_id = \"Building\".building_id"
	// // +
	// // " INNER JOIN \"Lease\" ON \"Lease\".fk_unit_id = \"Unit\".unit_id" +
	// // " INNER JOIN \"Resident\" ON \"Resident\".fk_lease_id = \"Lease\".lease_id" +
	// // " INNER JOIN \"User\" ON \"User\".user_id = \"Resident\".resident_id" +
	// // " where \"Building\".building_id = :buildingId AND \"Resident\".verified =
	// // :verified")
	// List<AdminResidentEntity> findAllByBuildingIdAndVerification(UUID buildingId, boolean verified, 
	// //Pageable pageable
	// //String orderBy, 
	//  String sortBy,
	//  int limit,
	// 		long offset
	// 		// String orderBy
			 
	// 		);

	@Query(value = "SELECT \"Resident\".resident_id, \"Resident\".first_name, \"Resident\".last_name, \"Resident\".gender, \"User\".email, \"Unit\".floor, \"Unit\".room, \"Lease\".start_date, \"Lease\".end_date, \"Resident\".verified from \"Building\""
+
"  INNER JOIN \"Unit\" ON \"Unit\".fk_building_id = \"Building\".building_id" +
"  INNER JOIN \"Lease\" ON \"Lease\".fk_unit_id = \"Unit\".unit_id" +
"  INNER JOIN \"Resident\" ON \"Resident\".fk_lease_id = \"Lease\".lease_id" +
"  INNER JOIN \"User\" ON \"User\".user_id = \"Resident\".resident_id" +
"  where \"Building\".building_id = :buildingId AND \"Resident\".verified = :verified" +

"  ORDER BY \"Resident\".resident_id LIMIT :limit OFFSET :offset")
List<AdminResidentEntity> findAllByBuildingIdAndVerification(UUID buildingId, boolean verified, int limit, long offset);

	@Query(value = "SELECT \"Resident\".resident_id, \"Resident\".first_name, \"Resident\".last_name, \"Resident\".gender, \"User\".email, \"Unit\".floor, \"Unit\".room, \"Lease\".start_date, \"Lease\".end_date, \"Resident\".verified from \"Building\""
	+
	"  INNER JOIN \"Unit\" ON \"Unit\".fk_building_id = \"Building\".building_id" +
	"  INNER JOIN \"Lease\" ON \"Lease\".fk_unit_id = \"Unit\".unit_id" +
	"  INNER JOIN \"Resident\" ON \"Resident\".fk_lease_id = \"Lease\".lease_id" +
	"  INNER JOIN \"User\" ON \"User\".user_id = \"Resident\".resident_id" +
	"  where \"Building\".building_id = :buildingId AND \"Resident\".verified = :verified" +
	
	"  ORDER BY \"Unit\".floor, \"Unit\".room, \"Resident\".first_name LIMIT :limit OFFSET :offset")
List<AdminResidentEntity> findAllByBuildingIdAndVerificationSortByFloor(UUID buildingId, boolean verified, int limit, long offset);
@Query(value = "SELECT \"Resident\".resident_id, \"Resident\".first_name, \"Resident\".last_name, \"Resident\".gender, \"User\".email, \"Unit\".floor, \"Unit\".room, \"Lease\".start_date, \"Lease\".end_date, \"Resident\".verified from \"Building\""
+
"  INNER JOIN \"Unit\" ON \"Unit\".fk_building_id = \"Building\".building_id" +
"  INNER JOIN \"Lease\" ON \"Lease\".fk_unit_id = \"Unit\".unit_id" +
"  INNER JOIN \"Resident\" ON \"Resident\".fk_lease_id = \"Lease\".lease_id" +
"  INNER JOIN \"User\" ON \"User\".user_id = \"Resident\".resident_id" +
"  where \"Building\".building_id = :buildingId AND \"Resident\".verified = :verified" +

"  ORDER BY \"Unit\".floor DESC, \"Unit\".room, \"Resident\".first_name LIMIT :limit OFFSET :offset")
List<AdminResidentEntity> findAllByBuildingIdAndVerificationSortByFloorDesc(UUID buildingId, boolean verified, int limit, long offset);
	
	@Query(value = "SELECT \"Resident\".resident_id, \"Resident\".first_name, \"Resident\".last_name, \"Resident\".gender, \"User\".email, \"Unit\".floor, \"Unit\".room, \"Lease\".start_date, \"Lease\".end_date, \"Resident\".verified from \"Building\""
	+
	"  INNER JOIN \"Unit\" ON \"Unit\".fk_building_id = \"Building\".building_id" +
	"  INNER JOIN \"Lease\" ON \"Lease\".fk_unit_id = \"Unit\".unit_id" +
	"  INNER JOIN \"Resident\" ON \"Resident\".fk_lease_id = \"Lease\".lease_id" +
	"  INNER JOIN \"User\" ON \"User\".user_id = \"Resident\".resident_id" +
	"  where \"Building\".building_id = :buildingId AND \"Resident\".verified = :verified" +
	
	"  ORDER BY \"Resident\".last_name, \"Resident\".first_name LIMIT :limit OFFSET :offset")
List<AdminResidentEntity> findAllByBuildingIdAndVerificationSortByLastName(UUID buildingId, boolean verified, int limit, long offset);
	
	@Query(value = "SELECT \"Resident\".resident_id, \"Resident\".first_name, \"Resident\".last_name, \"Resident\".gender, \"User\".email, \"Unit\".floor, \"Unit\".room, \"Lease\".start_date, \"Lease\".end_date, \"Resident\".verified from \"Building\""
			+
			"  INNER JOIN \"Unit\" ON \"Unit\".fk_building_id = \"Building\".building_id" +
			"  INNER JOIN \"Lease\" ON \"Lease\".fk_unit_id = \"Unit\".unit_id" +
			"  INNER JOIN \"Resident\" ON \"Resident\".fk_lease_id = \"Lease\".lease_id" +
			"  INNER JOIN \"User\" ON \"User\".user_id = \"Resident\".resident_id" +
			"  where \"Building\".building_id = :buildingId AND \"Resident\".verified = :verified" +
			
			"  ORDER BY \"Resident\".last_name DESC, \"Resident\".first_name ASC LIMIT :limit OFFSET :offset")
	List<AdminResidentEntity> findAllByBuildingIdAndVerificationSortByLastNameDesc(UUID buildingId, boolean verified, int limit, long offset);

	// @Query(value = "SELECT CEIL(COUNT(\"Resident\".resident_id)::float / :pageSize) from \"Building\"" +
	// "  INNER JOIN \"Unit\" ON \"Unit\".fk_building_id = \"Building\".building_id" +
	// "  INNER JOIN \"Lease\" ON \"Lease\".fk_unit_id = \"Unit\".unit_id" +
	// "  INNER JOIN \"Resident\" ON \"Resident\".fk_lease_id = \"Lease\".lease_id" +
	// "  where \"Building\".building_id = :buildingId AND \"Resident\".verified = true")
	@Query(value = "SELECT Count(\"Resident\".resident_id) from \"Building\"" +
	"  INNER JOIN \"Unit\" ON \"Unit\".fk_building_id = \"Building\".building_id" +
	"  INNER JOIN \"Lease\" ON \"Lease\".fk_unit_id = \"Unit\".unit_id" +
	"  INNER JOIN \"Resident\" ON \"Resident\".fk_lease_id = \"Lease\".lease_id" +
	"  where \"Building\".building_id = :buildingId AND \"Resident\".verified = :verified")
    public int retrieveNumberOfResidents(UUID buildingId, boolean verified);
			@Query(value = "SELECT \"Resident\".resident_id, \"Resident\".first_name, \"Resident\".last_name, \"Resident\".gender, \"User\".email, \"Unit\".floor, \"Unit\".room, \"Lease\".start_date, \"Lease\".end_date, \"Resident\".verified from \"Building\""
			+
			"  INNER JOIN \"Unit\" ON \"Unit\".fk_building_id = \"Building\".building_id" +
			"  INNER JOIN \"Lease\" ON \"Lease\".fk_unit_id = \"Unit\".unit_id" +
			"  INNER JOIN \"Resident\" ON \"Resident\".fk_lease_id = \"Lease\".lease_id" +
			"  INNER JOIN \"User\" ON \"User\".user_id = \"Resident\".resident_id" +
			"  where \"Resident\".resident_id = :residentId")
	Optional<AdminResidentEntity> findResidentById(UUID residentId);

	@Query(value = "SELECT \"Resident\".resident_id from \"Unit\"" +
			"  INNER JOIN \"Lease\" ON \"Lease\".fk_unit_id = \"Unit\".unit_id" +
			"  INNER JOIN \"Resident\" ON \"Resident\".fk_lease_id = \"Lease\".lease_id" +
			"  where \"Unit\".floor = :floor AND \"Unit\".room = :room")
	List<UUID> findResidentsByUnit(int floor, int room);

	// @Transactional
	@Modifying
	@Query(value = "INSERT INTO \"Resident\" (resident_id, first_name, last_name, age, hometown, biography, profile_photo, background_photo, "
			+
			"instagram, snapchat, x, facebook, gender, fk_lease_id, verified) VALUES " +
			"(:id, :firstName, :lastName, :age, :hometown, :biography, " +
			"CAST('\"' || :backgroundPhoto || '\"' AS JSONB), " +
			"CAST('\"' || :backgroundPhoto || '\"' AS JSONB), " +
			":instagram, :snapchat, :x, :facebook, CAST(:gender AS \"Gender\"), :leaseId, :verified)")
	// int register(ResidentEntity resident);
	int register(UUID id, String firstName, String lastName, int age, String hometown, String biography,
			String profilePhoto, String backgroundPhoto,
			String instagram, String snapchat, String x, String facebook, Gender gender, UUID leaseId,
			boolean verified);

			// modifying returns a value
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
