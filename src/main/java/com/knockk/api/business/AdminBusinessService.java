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

	public List<BuildingModel> getBuildings(UUID adminId) throws Exception{
		List<BuildingEntity> buildings =  dataService.findBuildingsByAdminId(adminId);
		List<BuildingModel> buildingModels = new ArrayList<>();

		for (BuildingEntity building : buildings) {
			buildingModels.add(new BuildingModel(building.getName(), building.getId()));
		}

		return buildingModels;
	}

	public List<AdminResidentModel> getResidents(UUID buildingId, boolean verified, Pageable pageable) throws Exception {
		List<AdminResidentEntity> residentEntities = dataService.findResidents(buildingId, verified, pageable);

		//Need to get lease id
		List<AdminResidentModel> residents = new ArrayList<>();
		for(AdminResidentEntity entity: residentEntities){
		residents.add(new AdminResidentModel(entity.getResidentId(), entity.getFirstName(), entity.getLastName(), entity.getEmail(), entity.getFloor(), entity.getRoom(), entity.getLeaseStart(), entity.getLeaseEnd()));};
		
		return residents;
	}

	public AdminResidentModel getResident(UUID residentId) throws Exception {
		AdminResidentEntity residentEntity = dataService.findResident(residentId);
		return new AdminResidentModel(residentEntity.getResidentId(), residentEntity.getFirstName(), residentEntity.getLastName(), residentEntity.getEmail(), residentEntity.getFloor(), residentEntity.getRoom(), residentEntity.getLeaseStart(), residentEntity.getLeaseEnd());
	}

	public Boolean activateResident(UUID residentId) throws Exception {
		return dataService.activateResident(residentId);
		
		// if(residentEntity.isVerified()){
		// 	return true;
		// }
		// else{ return false;}
	}

	public Boolean deleteResident(UUID residentId) throws Exception {
		return dataService.deleteResident(residentId);
		
		// if(residentEntity.isVerified()){
		// 	return true;
		// }
		// else{ return false;}
	}

	public int getNumberOfResidents(UUID buildingId, Boolean verified) throws Exception {

		return dataService.getNumberOfResidents(buildingId, verified);
	}
}
