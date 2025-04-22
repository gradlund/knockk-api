/**
 * Subdirectory of the data package that provides interface repositores
 */
package com.knockk.api.data.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.knockk.api.util.entity.AdminEntity;

/**
 * Inferface for the admin repository
 * 
 * @author graceradlund
 */
public interface AdminRepository extends CrudRepository<AdminEntity, UUID> {

	/**
	 * Finds the id of an admin, given their username and password
	 * 
	 * @param username : username of the admin
	 * @param password : password of the admin
	 * @return a UUID if the credentials are valid
	 */
	@Query(value = "SELECT admin_id from \"Admin\" where username = :username  AND password = :password")
	public Optional<UUID> findByUsernameAndPassword(String username, String password);

	/**
	 * Retrieves an admin entity
	 * 
	 * @param username : username of the admin
	 * @return an optional admin entity
	 */
	@Query(value = "SELECT * from \"Admin\" where username = :username")
	public Optional<AdminEntity> findByUsername(String username);
}
