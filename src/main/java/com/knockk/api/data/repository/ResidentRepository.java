package com.knockk.api.data.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.knockk.api.entity.ResidentEntity;


public interface ResidentRepository extends CrudRepository<ResidentEntity, UUID> {

	/**
	 * Finds the id of the resident, given their email and password
	 * @param email : email of the resident
	 * @param password : password of the resident
	 * @return the resident's id if the credentials are valid
	 */
	@Query(value = "SELECT * from \"Resident\" where resident_id = :residentId")
	Optional<ResidentEntity> findById(UUID residentId);

}
