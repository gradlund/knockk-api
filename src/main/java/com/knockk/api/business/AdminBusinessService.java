/**
 * Provides classes in the business layer of n-layer architecture
 */
package com.knockk.api.business;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.security.auth.login.CredentialException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.knockk.api.data.service.AdminDataService;
import com.knockk.api.entity.AdminResidentEntity;
import com.knockk.api.entity.BuildingEntity;
import com.knockk.api.model.AdminModel;
import com.knockk.api.model.AdminResidentModel;
import com.knockk.api.model.BuildingModel;

/**
 * This class implements the business service for admin
 * 
 * @author graceradlund
 */
@Service
public class AdminBusinessService {

	AdminDataService dataService;

	/**
	 * Constructor for dependency injection
	 * 
	 * @param dataService admin data service being used
	 */
	public AdminBusinessService(AdminDataService dataService) {
		this.dataService = dataService;
	}

	/**
	 * Activate a resident by resident id
	 * 
	 * NOTE: this updates the verified column in the database
	 * 
	 * @param residentId : id of the resident being updated
	 * @return a boolean if the resident was successfully updated
	 * @throws Exception thrown if there is an error in the data service
	 */
	public Boolean activateResident(UUID residentId) throws Exception {
		return dataService.activateResident(residentId);
	}

	/**
	 * Delete a resident by resident it
	 * 
	 * @param residentId : id of the resident being deleted
	 * @return a boolean if the resident was successfully deleted
	 * @throws Exception thrown if there is an error in the data service
	 */
	public Boolean deleteResident(UUID residentId) throws Exception {
		return dataService.deleteResident(residentId);
	}

	/**
	 * Get a list of buildings by admin id
	 * 
	 * @param adminId : id of the admin who manages the building(s)
	 * @return a list of building models
	 * @throws Exception thrown if there is an error in the data service
	 */
	public List<BuildingModel> getBuildings(UUID adminId) throws Exception {
		List<BuildingModel> buildingModels = new ArrayList<>();

		// Retrieve a list of building entites using the data service
		List<BuildingEntity> buildings = dataService.findBuildingsByAdminId(adminId);

		// Convert each entity into a model and add it to the list of building models
		for (BuildingEntity building : buildings) {
			buildingModels.add(new BuildingModel(building.getName(), building.getId()));
		}

		return buildingModels;
	}

	/**
	 * Get a resident by the resident's id
	 * 
	 * @param residentId : id of the resident being retrieved
	 * @return a admin resident model
	 *         TODO: could probably seperate the resident and admin into two so I
	 *         wouldn't have to call it admin resident model
	 * @throws Exception thrown if there is an error in the data service
	 */
	public AdminResidentModel getResident(UUID residentId) throws Exception {
		// Retrieve resident by the id of the resident
		AdminResidentEntity residentEntity = dataService.findResident(residentId);
		// Convert the entity into a model and return it
		return new AdminResidentModel(residentEntity.getBuildingId(), residentEntity.getResidentId(),
				residentEntity.getFirstName(), residentEntity.getLastName(), residentEntity.getEmail(),
				residentEntity.getFloor(), residentEntity.getRoom(), residentEntity.getLeaseStart(),
				residentEntity.getLeaseEnd());
	}

	/**
	 * Get a list of residents by building id, if they are verified, and pagination
	 * information
	 * 
	 * @param buildingId : id of the building the residents live in
	 * @param verified   : boolean if the resident are verified or not
	 * @param pageable   : pagination information for the data service query
	 * @return a list of admin resident models
	 * @throws Exception thrown if there is an error in the data service
	 */
	public List<AdminResidentModel> getResidents(UUID buildingId, boolean verified, Pageable pageable)
			throws Exception {
		List<AdminResidentModel> residents = new ArrayList<>();

		// Retrieve a list of residents by building id, verificiation, and pagination
		List<AdminResidentEntity> residentEntities = dataService.findResidents(buildingId, verified, pageable);

		// Convert each entity to a model and add it to the list of resident models
		for (AdminResidentEntity entity : residentEntities) {
			residents.add(new AdminResidentModel(entity.getBuildingId(), entity.getResidentId(), entity.getFirstName(),
					entity.getLastName(), entity.getEmail(), entity.getFloor(), entity.getRoom(),
					entity.getLeaseStart(), entity.getLeaseEnd()));
		}
		;

		return residents;
	}

	/**
	 * Get the credentials from the AdminModel sent in the request body and uses the
	 * data service to login
	 * 
	 * @param credential AdminModel that was sent in the request body
	 * @return the UUID response from the data service
	 * @throws CredentialException exception thrown if the credentials are invalid
	 *                             (thrown in the data service)
	 */
	public UUID login(AdminModel credential) throws CredentialException {

		String username = credential.getUsername();
		String password = credential.getPassword();

		// Use the dataservice to return the id of the admin
		return dataService.findAdminByUsernameAndPassword(username, password);
	}

	// TODO : delete
	public int getNumberOfResidents(UUID buildingId, Boolean verified) throws Exception {

		return dataService.getNumberOfResidents(buildingId, verified);
	}
}
