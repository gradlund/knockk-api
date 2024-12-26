package com.knockk.api.data.service;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.security.auth.login.CredentialException;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.knockk.api.data.repository.ResidentRepository;
import com.knockk.api.data.repository.UserRepository;
import com.knockk.api.entity.ResidentEntity;

import com.knockk.api.data.mapper.ResidentMapper;

// Exception reference: //https://docs.oracle.com/cd/E37115_01/apirefs.1112/e28160/org/identityconnectors/framework/common/exceptions/InvalidCredentialException.html

/**
 * Service class for the residents data
 * 
 * @author graceradlund
 */
@Service
public class ResidentDataService {

	private DataSource dataSource;
	private UserRepository userRepository;
	private ResidentRepository residentRepository;
	private JdbcTemplate jdbcTemplateObject;

	/**
	 * Constructor for dependency injection
	 * 
	 * @param userRepository     : user repository being injected
	 * @param residentRepository : resident repository being injected
	 */
	public ResidentDataService(DataSource dataSource, UserRepository userRepository,
			ResidentRepository residentRepository) {
		this.userRepository = userRepository;
		this.residentRepository = residentRepository;
		this.jdbcTemplateObject = new JdbcTemplate(dataSource);
	}

	/**
	 * Finds the id of the resident given their email and password. Uses the user
	 * repository.
	 * 
	 * @param email    : email of the resident
	 * @param password : password of the resident
	 * @return the residents id if the credentials are valid
	 * @throws CredentialException if the credentials are not valid
	 */
	public UUID findResidentByEmailAndPassword(String email, String password) throws CredentialException {

		Optional<UUID> id = userRepository.findByEmailAndPassword(email, password);

		// check for optional
		if (!id.isPresent())
			throw new CredentialException("Invalid credentials.");

		return id.get();
	}

	/***
	 * Finds if the resident is verified.
	 * NOTE: does not use the repository because there is an error with the PGObject convertor...
	 * Have to use the convertor because some types in Supabase need to be converted (like jsonb)
	 * @param residentId : id of the resident
	 * @return a boolean if the resident is verified
	 * @throws CredentialException if there is a problem getting the resident
	 */
	public boolean checkVerified(UUID residentId) throws CredentialException {
		// TODO: make prepared statement
		String sql = "SELECT * FROM \"Resident\" WHERE resident_id = '" + residentId + "'";
		List<ResidentEntity> resident = jdbcTemplateObject.query(sql, new ResidentMapper());

		// Check if the list is empty (means there was a problem returning the resident
		// or they don't exist)
		if (resident.isEmpty()) {
			throw new CredentialException("Invalid credentials.");
		}

		// Return if the resident is verified
		return resident.get(0).isVerified();
	}
}
