package com.knockk.api.data.service;

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
import com.knockk.api.data.repository.FriendshipRepository;
import com.knockk.api.data.repository.LeaseRepository;
import com.knockk.api.data.repository.ResidentRepository;
import com.knockk.api.data.repository.UnitRepository;
import com.knockk.api.data.repository.UserRepository;
import com.knockk.api.entity.BuildingEntity;
import com.knockk.api.entity.FriendshipEntity;
import com.knockk.api.entity.ResidentEntity;
import com.knockk.api.entity.UnitEntity;
import com.knockk.api.entity.UserEntity;
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
	private FriendshipRepository friendshipRepository;
	private LeaseRepository leaseRepository;
	private UnitRepository unitRepository;
	private UserRepository userRepository;
	private ResidentRepository residentRepository;
	private JdbcTemplate jdbcTemplateObject;

	/**
	 * Constructor for dependency injection
	 * 
	 * @param buildingRepository   : building repository being injected
	 * @param dataSource           : data source being injected
	 * @param friendshipRepository : friendship repository being injected
	 * @param leaseRepository:     lease repository being injected
	 * @param unitRepository       : unit repository being injected
	 * @param userRepository       : user repository being injected
	 * @param residentRepository   : resident repository being injected
	 */
	public ResidentDataService(BuildingRepository buildingRepository, DataSource dataSource,
			FriendshipRepository friendshipRepository,
			LeaseRepository leaseRepository, UnitRepository unitRepository, UserRepository userRepository,
			ResidentRepository residentRepository, JdbcTemplate jdbcTemplateObject) {
		this.buildingRepository = buildingRepository;
		this.friendshipRepository = friendshipRepository;
		this.leaseRepository = leaseRepository;
		this.unitRepository = unitRepository;
		this.userRepository = userRepository;
		this.residentRepository = residentRepository;
		// this.jdbcTemplateObject = new JdbcTemplate(dataSource);
		this.jdbcTemplateObject = jdbcTemplateObject;
	}

	public UUID createAccount(UserEntity user) throws Exception {
		// Check if email exists, if it does, throw error
		Optional<UUID> userId = userRepository.findByEmail(user.getEmail());
		if (userId.isPresent()) {
			throw new Exception("User already exists with that email.");
		}
		// Else insert the user and return the id
		return userRepository.saveAccount(user.getEmail(), user.getPassword()); // TODO; combine calls

	}

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

	/**
	 * Creates a friendship.
	 * 
	 * @param invitorId : id of the resident who sent the request.
	 * @param inviteeId : id of the resident receiving the request.
	 * @return a friendship entity containing details about the friendship.
	 * @throws Exception thrown if there is a problem creating
	 */
	public FriendshipEntity createFriendship(UUID invitorId, UUID inviteeId) throws Exception {
		// Retrieve a friendship (should not exist because we will be creating one)
		Optional<FriendshipEntity> friendship = friendshipRepository.findByResidentIdAndNeighborId(invitorId,
				inviteeId);

		// Make sure a friendship doesn't exist
		if (!friendship.isPresent()) {
			// Is accepted will always be false when creating a friendship
			UUID rows = friendshipRepository.addFriendship(invitorId, inviteeId, false);
			System.out.println(rows);
			return friendshipRepository.findByResidentIdAndNeighborId(invitorId, inviteeId).get();
		}
		// Throw an exception if the friendship could not be created
		else {
			throw new Exception("Problem creating a friendship.");
		}
	}

	/**
	 * Checks to see if two residents are friends.
	 * 
	 * NOTE: neighbors don't always have a friendship relationship.
	 * 
	 * @param residentId : id of the resident (user)
	 * @param friendId   : id of the friend (neighbor)
	 * @return a boolean - true if connected, false if not connected or a friendship
	 *         doesn't exist
	 */
	public boolean checkConnected(UUID residentId, UUID friendId) {
		// TO-DO : there will be an error if there are two friendships with the same
		// id's, be careful when manually doing this
		// Retrieve a frindship given the resident and friend is
		Optional<FriendshipEntity> friendship = friendshipRepository.findByResidentIdAndNeighborId(residentId,
				friendId);

		// Check for optional, meaning no friendship exists
		if (!friendship.isPresent()) {
			return false;
		}
		// Return accepted
		return friendship.get().isAccepted();
	}

	public boolean createResident(ResidentEntity resident) throws Exception {

		int rows = residentRepository.register(resident.getId(), resident.getFirstName(), resident.getLastName(),
				resident.getAge(), resident.getHometown(), resident.getBiography(), resident.getProfilePhoto(),
				resident.getBackgroundPhoto(), resident.getInstagram(), resident.getSnapchat(), resident.getX(),
				resident.getFacebook(), resident.getGender(), resident.getLeaseId(), resident.isVerified());
		// System.out.println(created);

		if (rows != 1) {
			throw new Exception("Could not save the resident.");
		}
		System.out.println(rows);
		return true;
	}

	/**
	 * Deletes a friendship.
	 * 
	 * @param residentId : id of the resident who no longer wants a friendship.
	 * @param friendId   : id of the resident who the resident no longer wants a
	 *                   friendship with.
	 * @return a boolean if the friendship has been successfully deleted.
	 * @throws Exception thrown if there is a problem deleting the friendship.
	 */
	public boolean deleteFriendship(UUID residentId, UUID friendId) throws Exception {
		// Use the friendship repository to delete a friendship
		int rows = friendshipRepository.deleteFriendship(residentId, friendId);

		System.out.println(rows);
		// If it returns 1, the row has successfully been deleted
		if (rows == 1) {
			return true;
		}
		// Otherwise throw an error because there was a problem deleting the friendship
		else {
			throw new Exception("Problem deleting friendship.");
		}
	}

	public List<BuildingEntity> findBuilding(String street) throws Exception {
		List<BuildingEntity> buildings = buildingRepository.findByAddress(street);

		if (buildings.isEmpty()) {
			throw new Exception("Invalid address (case sensitive)."); // Or building hasn't been added by admin yet

		} else {
			return buildings;
		}
	}

	/**
	 * Retrieves the friendship of two residents
	 * 
	 * @param residentId : id of the resident (user)
	 * @param friendId   : if of the friend (neighbor)
	 * @return an optional friendship entity
	 */
	public Optional<FriendshipEntity> findFriendship(UUID residentId, UUID friendId) {
		// Retrieve the friendship
		return friendshipRepository.findByResidentIdAndNeighborId(residentId,
				friendId);
	}

	/**
	 * Retrieve a list of resident ids who live in a unit.
	 * 
	 * @param floor : floor the unit is on
	 * @param room  : room the unit is in
	 * @return a list of ids
	 */
	public List<UUID> findNeighborsByUnit(int floor, int room) {
		// Return the list of ids
		return residentRepository.findResidentsByUnit(floor, room);
	}

	/**
	 * Retrieves the id of the unit by the id of the resident
	 * 
	 * @param residentId : id of the resident
	 * @return the id of the unit
	 * @throws Exception : if there is a problem getting the lease id (i.e. one may
	 *                   not exist)
	 */
	public UUID findUnitByUserId(UUID residentId) throws Exception {
		// Retrieve the lease id
		Optional<UUID> leaseId = residentRepository.findLeaseIdByResidentId(residentId);

		// Check for optional
		if (!leaseId.isPresent()) {
			throw new Exception("Problem getting lease id.");
		}

		// Find the unit id by lease id
		Optional<UUID> unitId = leaseRepository.findUnitByLeaseId(leaseId.get());

		// Check for optional
		if (!unitId.isPresent())
			throw new Exception("Unit not found.");

		// Return the unit id
		return unitId.get();
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

		// Check for optional
		if (!id.isPresent())
			throw new CredentialException("Invalid credentials.");

		return id.get();
	}

	/**
	 * Finds a resident by their id
	 * 
	 * @param residentId : id of the resident
	 * @return a resident entity
	 */
	public ResidentEntity findResidentById(UUID residentId) {
		return getResidentEntity(residentId);
	}

	/**
	 * Retrieves a unit by the id of the unit
	 * 
	 * @param unitId : id of the unit
	 * @return a UnitEntity
	 */
	public UnitEntity findUnitById(UUID unitId) {
		Optional<UnitEntity> unit = unitRepository.findById(unitId);

		// TODO: do I need this, or can .get() throw this exception?
		// Check for optional
		if (!unit.isPresent())
			throw new NoSuchElementException("Unit does not exist.");

		// Return the unit
		return unit.get();
	}

	public UUID getBuildingIdByAddressAndName(String address, String name) throws Exception {

		// Find the building id
		Optional<UUID> buildingId = buildingRepository.findByAddressAndName(address, name);

		// If the buidlingn exsits by address and name, return the id
		if (buildingId.isPresent()) {
			return buildingId.get();
		}
		// Else throw exception
		throw new Exception("Building does not exist with given street and address.");
	}

	public UUID getLeaseId(String address, String buildingName, int floor, int room, String startDate, String endDate)
			throws Exception {

		System.out.println(address + buildingName + floor + room + startDate + endDate);
		// Find the lease id
		Optional<UUID> leaseId = leaseRepository.findLeaseId(address, buildingName, floor, room, startDate, endDate);

		// If the buidlingn exsits by address and name, return the id
		if (leaseId.isPresent()) {
			System.out.println(leaseId.get());
			return leaseId.get();
		}
		// Else throw exception
		throw new Exception("Lease not found.");
	}

	// public UUID getUnitId(UUID buildingId, int floor, int room) throws Exception{
	// // Find unit id
	// Optional<UUID> unitId = unitRepository.findUnitId(buildingId, floor, room);

	// // If the unit exists with that criteria, return the id
	// if(unitId.isPresent()){
	// return unitId.get();
	// }
	// // Else throw exception
	// throw new Exception("Unit does not exist.");
	// }

	// private UUID getLeaseId(UUID unitId, Date startDate, Date endDate) throws
	// Exception{
	// // Find lease id
	// Optional<UUID> leaseId = unitRepository.findUnitId(buildingId, floor, room);

	// // If the unit exists with that criteria, return the id
	// if(unitId.isPresent()){
	// return unitId.get();
	// }
	// // Else throw exception
	// throw new Exception("Lease does not exist.");
	// }

	// TODO: do I need to retrieve the whole residententity?
	/**
	 * Retrieves the name of the resident.
	 * 
	 * NOTE: isFullName will be true if a resident is connected or if it's the user
	 * resident retrieving their name.
	 * 
	 * @param id         : id of the resident
	 * @param isFullName : boolean to see if the full name should be returned.
	 * @return the name of the resident
	 */
	public String getResidentName(UUID id, boolean isFullName) {
		// Retrieve the resident
		ResidentEntity resident = getResidentEntity(id);

		// If full name, get first and last
		if (isFullName) {
			return resident.getFirstName() + " " + resident.getLastName();

		}
		// Else only get first
		else {
			return resident.getFirstName();
		}
	}

	// TODO: do I need to retrieve the whole residententity?
	/**
	 * Retrieves the profile photo of the resident
	 * 
	 * @param id : id of the resident
	 * @return the photo uri
	 */
	public String getProfilePhoto(UUID id) {
		// Retrieve the resident
		ResidentEntity resident = getResidentEntity(id);

		// Get the resident
		// Database will return null if a profile photo does not exist
		if (resident.getProfilePhoto() == null)
			return null;

		return resident.getProfilePhoto();
	}

	/**
	 * Retrieves a resident.
	 * 
	 * NOTE: does not use the repository because there is an error with the
	 * PGObject convertor...
	 * Have to use the convertor because some types in Supabase need to be converted
	 * (like jsonb)
	 * 
	 * @param id : id of the resident
	 * @return a resident entity
	 * @throws Exception
	 */
	public ResidentEntity getResidentEntity(UUID id) {
		// TODO: switch to using repository or using prepared statement
		String sql = "SELECT * FROM \"Resident\" WHERE resident_id = '" + id + "'";
		List<ResidentEntity> resident = jdbcTemplateObject.query(sql, new ResidentMapper());

		// Check if the list is empty (means there was a problem returning the resident
		// or they don't exist)
		if (resident.isEmpty()) {
			throw new NoSuchElementException("Problem retrieving resident.");
		}

		// Return the resident
		return resident.get(0);
	}

	/**
	 * Updates a friendship.
	 * 
	 * @param invitorId  : id of the resident who sent the request.
	 * @param inviteeId  : id of the resident receiving the request.
	 * @param isAccepted : boolean if the resident who received the request has
	 *                   accepted the invite.
	 * @return a friendship entity containing details about the friendship.
	 * @throws Exception thrown if there is a problem retrieving or updating the
	 *                   friendship
	 */
	public FriendshipEntity updateFriendship(UUID invitorId, UUID inviteeId, boolean isAccepted) throws Exception {

		// Retrieve the friendship
		Optional<FriendshipEntity> friendship = friendshipRepository.findByResidentIdAndNeighborId(invitorId,
				inviteeId);

		// If a friendship exists, update the friendship
		if (friendship.isPresent()) {
			FriendshipEntity updateFriendship = friendship.get();
			// Update if the invitee has accepted the invite
			updateFriendship.setAccepted(isAccepted);

			// Save the updated friendship
			return friendshipRepository.save(updateFriendship); // TODO: does this need error handling?
		}
		// Throw an exception if a friendship does not exist
		else {
			throw new Exception("Friendship could not be found. Try creating a friendship.");
		}
	}

	/**
	 * Updates a resident.
	 * 
	 * @param resident : resident entity used to update the resident
	 * @return a boolean if the resident has been successfully updated
	 * @throws Exception if the resident could not be updated
	 */
	public Boolean updateResident(ResidentEntity resident) throws Exception {
		// Update the resident
		int rows = residentRepository.update(resident.getAge(), resident.getHometown(), resident.getBiography(),
				resident.getProfilePhoto(), resident.getBackgroundPhoto(), resident.getInstagram(),
				resident.getSnapchat(), resident.getX(), resident.getFacebook(), resident.getId());

		// If one row was updated, return the resident
		if (rows == 1) {
			String sql = "SELECT * FROM \"Resident\" WHERE resident_id = '" + resident.getId() + "'";
			List<ResidentEntity> updatedResident = jdbcTemplateObject.query(sql, new ResidentMapper());
			if (!updatedResident.isEmpty())
				return true;
			// TODO - could I delete this extra call and
			// just return the entity passed intot the
			// function?
		}
		// Else throw exception
		throw new Exception("Could not update resident.");
	}
}
