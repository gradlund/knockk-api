/**
 * Subdirectory of the data package that contains data service classes
 */
package com.knockk.api.data.service;

import java.util.Optional;
import java.util.UUID;

import javax.security.auth.login.CredentialException;

import org.springframework.stereotype.Service;

import com.knockk.api.data.repository.AdminRepository;

// Exception reference: //https://docs.oracle.com/cd/E37115_01/apirefs.1112/e28160/org/identityconnectors/framework/common/exceptions/InvalidCredentialException.html

/**
 * Service class for the admin's data
 * 
 * @author graceradlund
 */
@Service
public class AdminDataService {

	private AdminRepository adminRepository;

	/**
	 * Constructor used for dependency injection
	 * 
	 * @param adminRepository : admin repository being injected
	 */
	public AdminDataService(AdminRepository adminRepository) {
		this.adminRepository = adminRepository;
	}

	/**
	 * Finds the admin by username and password. Uses the admin repository.
	 * 
	 * @param username : username of the admin
	 * @param password : password of the admin
	 * @return the id of the admin if the credentials are valid
	 * @throws CredentialException if the credentials are not valid
	 */
	public UUID findAdminByUsernameAndPassword(String username, String password) throws CredentialException {

		Optional<UUID> id = adminRepository.findByUsernameAndPassword(username, password);

		if (!id.isPresent())
			throw new CredentialException("Invalid credentials.");

		return id.get();
	}
}
