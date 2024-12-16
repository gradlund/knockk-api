package com.knockk.api.business;

import java.util.UUID;

import javax.security.auth.login.CredentialException;

import org.springframework.stereotype.Service;

import com.knockk.api.data.service.ResidentDataService;
import com.knockk.api.model.LoginModel;
import com.knockk.api.model.ResidentModel;
import com.knockk.api.model.UserModel;

/**
 * This class implements the business service for residents
 * @author graceradlund
 */
@Service
public class ResidentBusinessService {
	ResidentDataService dataService;
	
	/**
	 * Constructor used for dependency injection
	 * @param dataService data service used in for residents
	 */
	public ResidentBusinessService(ResidentDataService dataService) {
		this.dataService = dataService;
	}
	
	//TODO: Wrap in try catch
	/**
	 * Get the credentials from the AdminModel sent in the request body and use the data service to login
	 * @param credential AdminModel that was sent in the request body
	 * @return response back from the data service
	 * @throws CredentialException exception thrown if the credentials are invalid
	 */
	public LoginModel login(UserModel credential) throws CredentialException {
		String email = credential.getEmail();
		String password = credential.getPassword();

		//Find id
		UUID id = dataService.findResidentByEmailAndPassword(email, password);
		
		//Find if verified
		Boolean verified = dataService.checkVerified(id);
		
		return new LoginModel(id, verified);
	}
}
