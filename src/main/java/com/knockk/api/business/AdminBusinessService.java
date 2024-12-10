/**
 * Provides classes in the business layer of n-layer architecture
 */
package com.knockk.api.business;

import java.util.UUID;

import javax.security.auth.login.CredentialException;

import org.springframework.stereotype.Service;

import com.knockk.api.data.service.AdminDataService;
import com.knockk.api.model.AdminModel;

/**
 * This class implements the business service for admin
 * @author graceradlund
 */
@Service
public class AdminBusinessService {

	AdminDataService dataService;
	
	/**
	 * Constructor for dependency injection
	 * @param dataService admin data service being used
	 */
	public AdminBusinessService(AdminDataService dataService) {
		this.dataService = dataService;
	}
	
	/**
	 * Get the credentials from the AdminModel sent in the request body and use the data service to login
	 * @param credential AdminModel that was sent in the request body
	 * @return response back from the data service
	 * @throws CredentialException exception thrown if the credentials are invalid
	 */
	public UUID login(AdminModel credential) throws CredentialException {
		//
		String username = credential.getUsername();
		String password = credential.getPassword();

		return dataService.findAdminByUsernameAndPassword(username, password);
	}
}
