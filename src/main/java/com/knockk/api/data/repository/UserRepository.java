package com.knockk.api.data.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.knockk.api.util.entity.UserEntity;

/**
 * Interface for the resident repository
 * 
 * @author graceradlund
 */
public interface UserRepository extends CrudRepository<UserEntity, UUID> {

	/**
	 * Finds the id of the resident, given their email and password
	 * 
	 * @param email    : email of the resident
	 * @param password : password of the resident
	 * @return the resident's id if the credentials are valid
	 */
	@Query(value = "SELECT user_id from \"User\" where email = :email  AND password = :password")
	public Optional<UUID> findByEmailAndPassword(String email, String password);

	/**
	 * Finds a user from their email
	 * 
	 * @param email : email address of the user
	 * @return a user entity if found
	 */
	@Query(value = "SELECT * from \"User\" where email = :email")
	public Optional<UserEntity> findByEmail(String email);

	/**
	 * Registers an account given their email and password
	 * 
	 * @param email    : email of the user
	 * @param password : password of the user
	 * @return the id of the user, if successfully registered
	 */
	@Query(value = "INSERT INTO \"User\" (email, password) VALUES (:email, :password) RETURNING user_id")
	public UUID saveAccount(String email, String password);
}
