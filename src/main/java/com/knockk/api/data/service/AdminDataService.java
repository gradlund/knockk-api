/**
 * Subdirectory of the data package that contains data service classes
 */
package com.knockk.api.data.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.security.auth.login.CredentialException;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.knockk.api.data.repository.AdminRepository;
import com.knockk.api.data.repository.BuildingRepository;
import com.knockk.api.data.repository.ResidentRepository;
import com.knockk.api.data.repository.UserRepository;
import com.knockk.api.util.entity.AdminEntity;
import com.knockk.api.util.entity.AdminResidentEntity;
import com.knockk.api.util.entity.BuildingEntity;

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
	private UserRepository userRepository;
	private ResidentRepository residentRepository;

	/**
	 * Constructor used for dependency injection
	 * 
	 * @param adminRepository : admin repository being injected
	 */
	public AdminDataService(AdminRepository adminRepository, BuildingRepository buildingRepository,
			UserRepository userRepository,
			ResidentRepository residentRepository) {
		this.adminRepository = adminRepository;
		this.buildingRepository = buildingRepository;
		this.userRepository = userRepository;
		this.residentRepository = residentRepository;
	}

	/**
	 * Updates a resident in the database to verified
	 * 
	 * @param residentId : id of the resident being updated
	 * @return a boolean if the resident was successfully updated
	 * @throws Exception thrown if there is a problem updating the resident
	 */
	public Boolean activateResident(UUID residentId) throws Exception {
		// Update the residents verification status to true
		int rowsUpdated = residentRepository.activate(residentId);

		// If the number of rows does not equal one, throw an exception because there
		// was a problem updating the resident
		if (rowsUpdated != 1) {
			throw new Exception("Error updating resident.");
		}

		return true;
	}

	/**
	 * Deletes a resident from the database
	 * 
	 * TODO: eliminate a race condition!
	 * 
	 * @param residentId : id of the resident being deleted
	 * @return a boolean if the resident was successfully deleted
	 * @throws Exception thrown if there is a problem deleting the resident
	 */
	public Boolean deleteResident(UUID residentId) throws Exception {
		// TODO: could minimize calls by having the delete return the number of rows
		// modified; also could current implementation be a race condition?
		// Delete the resident by id
		// TODO: TODO: race condition!!
		residentRepository.deleteById(residentId);
		userRepository.deleteById(residentId);

		// Try to retrieve the resident by their id
		Optional<AdminResidentEntity> checkDeleted = residentRepository.findResidentById(residentId);

		// If the resident exists, throw an error because it should have been deleted
		if (checkDeleted.isPresent()) {
			throw new Exception("Did not delete.");
		}

		return true;
	}

	/**
	 * Finds the admin by username. Uses the admin repository.
	 * 
	 * @param username : username of the admin
	 * @return the id of the admin if the credentials are valid
	 * @throws CredentialException if the credentials are not valid
	 */
	public AdminEntity findAdminByUsername(String username) throws CredentialException {

		// Retrieve the admin from their username handle
		Optional<AdminEntity> admin = adminRepository.findByUsername(username);

		// If the username doesn't exist, throw an error
		if (!admin.isPresent()) {
			throw new CredentialException("Invalid username.");
		}

		return admin.get();
	}

	/**
	 * Retrieves a list of buildings the admin manages
	 * 
	 * @param adminId : id of the admin
	 * @return a list of building entities
	 * @throws Exception exception thrown if no buildings could be found by the
	 *                   admin id
	 */
	public List<BuildingEntity> findBuildingsByAdminId(UUID adminId) throws Exception {
		// Retrieve a list of building entities by admin id
		List<BuildingEntity> buildings = buildingRepository.findAllByAdminId(adminId);

		// If no buildings were returned, throw an exception
		if (buildings.isEmpty()) {
			throw new Exception("Not Found. No buildings found.");
		}

		// Else return the list
		return buildings;
	}

	/**
	 * Retrieve a resident by resident id.
	 * 
	 * @param residentId : id of the resident
	 * @return the admin resident entity
	 * @throws Exception thrown if the resident could not be found
	 */
	public AdminResidentEntity findResident(UUID residentId) throws Exception {
		// Retrieve the resident by id
		Optional<AdminResidentEntity> resident = residentRepository.findResidentById(residentId);

		// If the resident is present, throw an exception
		if (resident.isEmpty()) {
			throw new Exception("Not found. No resident found.");
		}

		return resident.get();
	}

	/**
	 * Retrieve a list of admin resident entities
	 * 
	 * TODO: handle if buildingId is null
	 * 
	 * @param buildingId : id of the building
	 * @param verified   : boolean if the residents of that building are verified or
	 *                   not
	 * @param pageable   : pagination information
	 * @return a list of admin resident entities
	 * @throws Exception thrown if there no residents could be found
	 */
	public List<AdminResidentEntity> findResidents(UUID buildingId, boolean verified, Pageable pageable)
			throws Exception {
		List<AdminResidentEntity> residents = new ArrayList();

		// Get pagination properties
		int limit = pageable.getPageSize();
		String sort = pageable.getSort().toString();
		long offset = pageable.getOffset();

		String sortBy;
		String direction;

		// Variables used for sorting
		// NOTE : pageable does not work within queries, which is why these variables
		// have to be seperated out
		// ASC and DESC are keywords and can not be parameterized
		// JDBC doesn't support Pageable like JPA
		if (sort.isEmpty()) { // sort.isEmpty() doesn't work
			System.out.println("Sort is empty");
			sortBy = "lastName";
			direction = "ASC";
		} else {
			// Get the sort string and parse it safely
			String sortString = sort.toString(); // e.g., "lastName: ASC" or "lastName, ASC"
			if (sortString.contains(":")) {
				String[] parts = sortString.split(":");
				sortBy = parts[0].trim();
				direction = parts[1].trim().replace(",", "").toUpperCase(); // Normalize to ASC/DESC
			} else if (sortString.contains(",")) {
				String[] parts = sortString.split(",");
				sortBy = parts[0].trim();
				direction = parts[1].trim().toUpperCase(); // Normalize to ASC/DESC
			} else {
				// Fallback
				sortBy = "lastName";
				direction = "ASC";
			}
		}

		// TODO - error handling if pagabel is wrong - or if the sort by param is wrong.
		// If sort is by last name, sort by last name
		if (sortBy.equals("lastName")) {
			if (direction.equals("ASC"))
				residents = residentRepository.findAllByBuildingIdAndVerificationSortByLastName(buildingId, verified,
						limit, offset);
			else
				residents = residentRepository.findAllByBuildingIdAndVerificationSortByLastNameDesc(buildingId,
						verified, limit, offset);

		}
		// If sort is by floor, sort by floor
		else if (sortBy.equals("floor")) {
			if (direction.equals("ASC"))
				residents = residentRepository.findAllByBuildingIdAndVerificationSortByFloor(buildingId, verified,
						limit, offset);
			else
				residents = residentRepository.findAllByBuildingIdAndVerificationSortByFloorDesc(buildingId, verified,
						limit, offset);
		}
		// Default sort, if the sort passed by the api doesn't match
		else {
			System.out.println("Sort from request was not valid. Default sorting.");
			residents = residentRepository.findAllByBuildingIdAndVerification(buildingId, verified, limit, offset);
		}

		// If the returned sort is empty, throw an exception.
		if (residents.isEmpty()) {
			throw new Exception("Not found. No residents found.");
		}

		return residents;
	}

	/**
	 * Does not throw an exception... okay if no residents are found because this
	 * method is used to check
	 * the number of unverified residents
	 * 
	 * @param buildingId : id of the building
	 * @param verified   : if the residents are verified
	 * @return the number of residents
	 * @throws Exception
	 */
	public int getNumberOfResidents(UUID buildingId, boolean verified) throws Exception {
		int numOfResidents = residentRepository.retrieveNumberOfResidents(buildingId, verified);
		return numOfResidents;
	}
}
