package com.knockk.api.data.service;

import java.util.Optional;
import java.util.UUID;

import javax.security.auth.login.CredentialException;

import org.springframework.stereotype.Service;

import com.knockk.api.data.repository.UserRepository;

// Exception reference: //https://docs.oracle.com/cd/E37115_01/apirefs.1112/e28160/org/identityconnectors/framework/common/exceptions/InvalidCredentialException.html

/**
 * Service class for the residents data
 * @author graceradlund
 */
@Service
public class ResidentDataService {
	
	private UserRepository userRepository;
	
	/**
	 * Constructor for dependency injection
	 * @param userRepository : user repository being injected
	 */
	public ResidentDataService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	/**
	 * Finds the id of the resident given their email and password. Uses the user repository.
	 * @param email : email of the resident
	 * @param password : password of the resident
	 * @return the residents id if the credentials are valid
	 * @throws CredentialException if the credentials are not valid
	 */
	public UUID findResidentByEmailAndPassword(String email, String password) throws CredentialException {
		
		Optional<UUID> id =  userRepository.findByEmailAndPassword(email, password);
		
		if(!id.isPresent())
			throw new CredentialException("Invalid credentials.");
		
		return id.get();
	}
}
