package com.knockk.api.data.service;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import javax.security.auth.login.CredentialException;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.knockk.api.data.repository.BuildingRepository;
import com.knockk.api.data.repository.LeaseRepository;
import com.knockk.api.data.repository.ResidentRepository;
import com.knockk.api.data.repository.UnitRepository;
import com.knockk.api.data.repository.UserRepository;
import com.knockk.api.entity.BuildingEntity;
import com.knockk.api.entity.ResidentEntity;
import com.knockk.api.entity.UnitEntity;
import com.knockk.api.data.mapper.ResidentMapper;

// Exception reference: //https://docs.oracle.com/cd/E37115_01/apirefs.1112/e28160/org/identityconnectors/framework/common/exceptions/InvalidCredentialException.html

/**
 * Service class for the residents data
 * 
 * @author graceradlund
 */
@Service
public class ResidentDataService {

	private BuildingRepository buildingRepository;
	private LeaseRepository leaseRepository;
	private UnitRepository unitRepository;
	private UserRepository userRepository;
	private ResidentRepository residentRepository;
	private JdbcTemplate jdbcTemplateObject;

	/**
	 * Constructor for dependency injection
	 * 
	 * @param buildingRepository : building repository being injected
	 * @param dataSource         : data source being injected
	 * @param leaseRepository:   lease repository being injected
	 * @param unitRepository     : unit repository being injected
	 * @param userRepository     : user repository being injected
	 * @param residentRepository : resident repository being injected
	 */
	public ResidentDataService(BuildingRepository buildingRepository, DataSource dataSource,
			LeaseRepository leaseRepository, UnitRepository unitRepository, UserRepository userRepository,
			ResidentRepository residentRepository) {
		this.buildingRepository = buildingRepository;
		this.leaseRepository = leaseRepository;
		this.unitRepository = unitRepository;
		this.userRepository = userRepository;
		this.residentRepository = residentRepository;
		this.jdbcTemplateObject = new JdbcTemplate(dataSource);
	}

	// TODO - logic won't work if 329 is in system, change lowest level to 4 - it
	// will still return rooms to the right
	// like 330, but not any floors above. wait that's what I want....

	/**
	 * Checks to see if the building has a room above that the resident is neighbors
	 * with
	 * 
	 * @param buildingId : id of the building the unit is in
	 * @param floor      : floor that is being checked (already has the +/- 1)
	 * @return a boolean if the neighbor has a neighboring floor
	 */
	public boolean checkNeighborFloor(UUID buildingId, int floor) {
		// Get the top and bottom floor and make sure it's inbetween
		// TODO: probably a better way to do this than getting the whole entity when one
		// property is fine
		// TODO: could do complex sql query instead
		Optional<BuildingEntity> optionalBuilding = buildingRepository.findById(buildingId);

		// Check for optional
		if (!optionalBuilding.isPresent())
			throw new NoSuchElementException("Building does not exist.");

		// Unwrap optional
		BuildingEntity building = optionalBuilding.get();

		int topFloor = building.getTopFloor();
		int bottomFloor = building.getBottomFloor();

		// +/- 1 was already added, meaning should not be equal to
		// Make sure top and bottom floors are valid
		if (floor < topFloor && floor > bottomFloor) {
			return true;
		}
		return false;
	}

	// TODO: should probably have seperate checks for neighbor right and neighbor
	// left to seperate instead of using this function for both
	// because it's duplicate work in the business service class
	/**
	 * Checks to see if the unit has neighbors to the right or left
	 * 
	 * @param buildingId : id of the building the unit is in
	 * @param room       : room that is being checked (already has the +/- 1)
	 * @return a boolean if the neighboring room exists
	 */
	public boolean checkNeighborRoom(UUID buildingId, int room) {
		// Get the list rooms with no rooms to left and right and verifiy the room isn't
		// on the list
		Optional<BuildingEntity> optionalBuilding = buildingRepository.findById(buildingId);

		// Check for optional
		if (!optionalBuilding.isPresent())
			throw new NoSuchElementException("Building does not exist.");

		BuildingEntity building = optionalBuilding.get();

		// TODO: add highest number room and lowest in table? ex : rooms start at 0?. -
		// no not necessarily because a building could be a rectangle
		// ^ but it could make sure it's in range of possible rooms.
		// TODO: need a new table probably with foreigns to floors and their highest and
		// lowest rooms
		// Room will have the + or - 1 added to it already
		ArrayList<Integer> noRoomsRight = building.getNoRoomsRight();
		ArrayList<Integer> noRoomsLeft = building.getNoRoomsLeft();

		// Make sure room is not a room that has no neighbors right
		for (Integer roomToCheck : noRoomsRight) {
			if (roomToCheck == room) {
				return false;
			}
		}

		// Make sure room is not a room that has no neighbors left
		for (Integer roomToCheck : noRoomsLeft) {
			if (roomToCheck == room) {
				return false;
			}
		}

		return true;
	}

	// do not think this is neccesary. When a resident clicks on a room it will
	// return a list of residents - or null if no residents are registered
	public void findIfUnitHasRegisteredResident(int floor, int room) {
		// TODO: better naming conventions
		// return residentRepository.findIfUserExistsByUnit(floor, room);
	}

	/***
	 * Finds if the resident is verified.
	 * NOTE: does not use the repository because there is an error with the PGObject
	 * convertor...
	 * Have to use the convertor because some types in Supabase need to be converted
	 * (like jsonb)
	 * 
	 * @param residentId : id of the resident
	 * @return a boolean if the resident is verified
	 * @throws CredentialException if there is a problem getting the resident
	 */
	public boolean checkVerified(UUID residentId) throws CredentialException {
		// TODO: switch to using repository or using prepared statement
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

	// TODO: write tests and javadocs
	public UUID findUnitByUserId(UUID residentId) throws Exception {
		// Get the lease id
		Optional<UUID> leaseId = residentRepository.findLeaseIdByResidentId(residentId);

		// Check for optional
		if (!leaseId.isPresent()) {
			throw new Exception("Problem getting lease id.");
		}

		System.out.println(leaseId.get());

		// Find the unit id
		Optional<UUID> unitId = leaseRepository.findUnitByLeaseId(leaseId.get());

		// Check for optional
		if (!unitId.isPresent())
			throw new Exception("Unit not found.");

		// Return the unit id
		return unitId.get();
	}

	// public List<NeighborEntity> findNeighbors(int floor, int room){
	// //
	// }

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

		// Check for optional
		if (!id.isPresent())
			throw new CredentialException("Invalid credentials.");

		return id.get();
	}

	public UnitEntity findUnitById(UUID unitId) {
		Optional<UnitEntity> unit = unitRepository.findById(unitId);

		// TODO: do I need this, or can .get() throw this exception?
		// Check for optional
		if (!unit.isPresent())
			throw new NoSuchElementException("Unit does not exist.");

		return unit.get();
	}

}
