/**
 * Subdirectory of the data package that contains data service classes
 */
package com.knockk.api.data.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.security.auth.login.CredentialException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.knockk.api.data.repository.AdminRepository;
import com.knockk.api.data.repository.BuildingRepository;
import com.knockk.api.data.repository.ResidentRepository;
import com.knockk.api.entity.AdminResidentEntity;
import com.knockk.api.entity.BuildingEntity;
import com.knockk.api.model.BuildingModel;

// Exception reference: //https://docs.oracle.com/cd/E37115_01/apirefs.1112/e28160/org/identityconnectors/framework/common/exceptions/InvalidCredentialException.html

/**
 * Service class for the admin's data
 * 
 * @author graceradlund
 */
@Service
public class AdminDataService {

	private AdminRepository adminRepository;
	private BuildingRepository buildingRepository;
	private ResidentRepository residentRepository;

	/**
	 * Constructor used for dependency injection
	 * 
	 * @param adminRepository : admin repository being injected
	 */
	public AdminDataService(AdminRepository adminRepository, BuildingRepository buildingRepository, ResidentRepository residentRepository) {
		this.adminRepository = adminRepository;
		this.buildingRepository = buildingRepository;
		this.residentRepository = residentRepository;
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

    public List<BuildingEntity> findBuildingsByAdminId(UUID adminId) throws Exception {
       List<BuildingEntity> buildings = buildingRepository.findAllByAdminId(adminId);

	   if(buildings.isEmpty()){
		throw new Exception("Not Found. No buildings found.");
	   }

	   return buildings;
    }

	public List<AdminResidentEntity> findResidents(UUID buildingId, boolean verified, Pageable pageable) throws Exception {
		int limit = pageable.getPageSize();
		//String sort = pageable.getSort();
		long offset = pageable.getOffset();
		
		// try catch
		Sort sort = pageable.getSort();
	
		String[] sortProperties = sort.toString().split(":");
		String sortBy = sortProperties[0].trim();
		String direction = sortProperties[1].trim();
		//System.out.println(sortBy.equals("lastName"));

		// will be weird if "" is passed in

		//String orderBy = "";

		// Pageable does not work with queires
		List<AdminResidentEntity> residents = new ArrayList();
		//ASC and DESC are keywords and can not be parameterized
		// JDBC doesn't support Pageable like JPA

		// TODO - error handling if pagabel is wrong - or if the sort by param is wrong.
		if(sortBy.equals("lastName")){
			if(direction.equals("ASC"))
			residents = residentRepository.findAllByBuildingIdAndVerificationSortByLastName(buildingId, verified, limit, offset);
			else
			 residents = residentRepository.findAllByBuildingIdAndVerificationSortByLastNameDesc(buildingId, verified, limit, offset);
				
		}
		else if(sortBy.equals("floor")){
			if(direction.equals("ASC"))
			 residents = residentRepository.findAllByBuildingIdAndVerificationSortByFloor(buildingId, verified, limit, offset);
			 else
			  residents = residentRepository.findAllByBuildingIdAndVerificationSortByFloorDesc(buildingId, verified, limit, offset);
		}
		else{
			residents = residentRepository.findAllByBuildingIdAndVerification(buildingId, verified, limit, offset);
		}
		

		//String orderBy = pageable.getSort().toString().replace(":", "");
		//System.out.println(orderBy);
		//System.out.println(direction);
		

		if(residents.isEmpty()){
			throw new Exception("Not found. No residents found.");
		}

		return residents;
	}

	public AdminResidentEntity findResident(UUID residentId) throws Exception {
		
		Optional<AdminResidentEntity> resident = residentRepository.findResidentById(residentId);
		if(resident.isEmpty()){
			throw new Exception("Not found. No resident found.");
		}

		return resident.get();
	}

	public Boolean activateResident(UUID residentId) throws Exception {
		
		int rowsUpdated = residentRepository.activate(residentId);
		if(rowsUpdated != 1){
			throw new Exception("Error updating resident.");
		}

		return true;
	}

	public Boolean deleteResident(UUID residentId) throws Exception {
		
		residentRepository.deleteById(residentId);

		Optional<AdminResidentEntity> checkDeleted = residentRepository.findResidentById(residentId);
		if(checkDeleted.isPresent()){
			throw new Exception("Did not delete.");
		}

		return true;
	}

	public int getNumberOfResidents(UUID buildingId, boolean verified) throws Exception {
		int pages = residentRepository.retrieveNumberOfResidents(buildingId, verified);

		System.out.println(pages);
		if(pages == 0){
			throw new Exception("Not found. Problem retrieving pages.");
		}

		return pages;
	}
}
