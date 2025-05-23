package com.knockk.api.business;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import javax.security.auth.login.CredentialException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.knockk.api.data.service.ResidentDataService;
import com.knockk.api.util.Gender;
import com.knockk.api.util.entity.BuildingEntity;
import com.knockk.api.util.entity.FriendshipEntity;
import com.knockk.api.util.entity.ResidentEntity;
import com.knockk.api.util.entity.UnitEntity;
import com.knockk.api.util.entity.UserEntity;
import com.knockk.api.util.model.FriendshipModel;
import com.knockk.api.util.model.LoginModel;
import com.knockk.api.util.model.NeighborRoomModel;
import com.knockk.api.util.model.OptionalResidentModel;
import com.knockk.api.util.model.RegisterModel;
import com.knockk.api.util.model.ResidentModel;
import com.knockk.api.util.model.UnitResidentModel;
import com.knockk.api.util.model.UserModel;

/**
 * This class implements the business service for residents
 * 
 * @author graceradlund
 */
@Service
public class ResidentBusinessService {
	ResidentDataService dataService;

	// @Autowired
	private BCryptPasswordEncoder passwordEncoder;

	/**
	 * Constructor used for dependency injection
	 * 
	 * @param dataService data service used in for residents
	 */
	public ResidentBusinessService(ResidentDataService dataService,
			BCryptPasswordEncoder passwordEncoder) {
		this.dataService = dataService;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * Retrieves a list of building names based on their address
	 * 
	 * @param street : street address of the building
	 * @return a list of building names
	 * @throws Exception thrown if no buildings are found
	 */
	public List<String> getBuildings(String street) throws Exception {
		List<BuildingEntity> buildingEntities = dataService.findBuilding(street);
		List<String> buildings = new ArrayList<String>();
		for (BuildingEntity building : buildingEntities) {
			buildings.add(building.getName());
		}
		return buildings;
	}

	/**
	 * Creates an account with the credentials given
	 * 
	 * @param credentials : email and password of the resident
	 * @return the id of the resident registered
	 * @throws Exception thrown if there is a problem creating the account
	 */
	public UUID createAccount(UserModel credentials) throws Exception {
		// Check if the email exists throw an error, otherwise create the account
		// Convert to entity
		return dataService.createAccount(new UserEntity(null, credentials.getEmail(),
				passwordEncoder.encode(credentials.getPassword())));
	}

	/**
	 * Deletes a friendship.
	 * 
	 * @param residentId : id of the resident who wants to delete the friendship.
	 * @param friendId   : id of the friend who the resident no longer wants to be
	 *                   friends with
	 * @return a boolean if the friendship has successfully been deleted.
	 * @throws Exception if there was a problem deleting the friendship
	 */
	public boolean deleteFriendship(UUID residentId, UUID friendId) throws Exception {
		return dataService.deleteFriendship(residentId, friendId);
	}

	/**
	 * Retrieves information about a friendship. Used to see if a friendship is
	 * pending.
	 * 
	 * NOTE: friendship will not be made in the db until someone makes a request. It
	 * will be deleted if they are connected, and someone removes the connection.
	 * NOTE: will throw an exception if a friendship does not exist.
	 * NOTE: usually won't make this call if you know a friendship is already
	 * accepted
	 * 
	 * @param residentId : id of the resident (user)
	 * @param friendId   : if of the friend (neighbor)
	 * @return a friendship model which contains details about the friendship
	 */
	public FriendshipModel getFriendship(UUID residentId, UUID friendId) {
		// Retrieve the friendship
		Optional<FriendshipEntity> optionalFriendship = dataService.findFriendship(residentId, friendId);

		try {
			FriendshipEntity friendship = optionalFriendship.get(); // error handling

			// Return the friendship model - convert the entity into a model
			return new FriendshipModel(friendship.getInvitorId(), friendship.getInviteeId(), friendship.isAccepted());
		}
		// If friendship doesn't exist, throw an exception
		catch (Exception e) {
			throw new NoSuchElementException("Not Found. Friendship does not exist.");
		}
	}

	/**
	 * Updates a friendship. Used for creating or updating a friendship.
	 * 
	 * @param invitorId  : id of the resident who sent the request
	 * @param inviteeId  : id of the resident who is receiving the friendship
	 *                   request
	 * @param isAccepted : if the invitee has accepted the friendship (will always
	 *                   be false if creating a friendship)
	 * @return a friendship model that contains details about the friendship
	 * @throws Exception
	 */
	public FriendshipModel updateFriendship(UUID invitorId, UUID inviteeId, boolean isAccepted) throws Exception {
		// TODO: make sure they are valid neighbors

		// Use the data service class to update the friendship and store the response as
		// a friendship entity
		// If a friendship does not exist, create one
		try {
			FriendshipEntity friendship = dataService.updateFriendship(invitorId, inviteeId, isAccepted);

			// Convert the entity to a model to return
			return new FriendshipModel(friendship.getInvitorId(), friendship.getInviteeId(), friendship.isAccepted());
		} catch (Exception e) {
			FriendshipEntity friendship = dataService.createFriendship(invitorId, inviteeId);

			// Convert the entity to a model to return
			return new FriendshipModel(friendship.getInvitorId(), friendship.getInviteeId(), friendship.isAccepted());
		}
	}

	// TODO: Wrap in try catch - may not need because I have error checking in
	// request
	/**
	 * Get the credentials from the AdminModel sent in the request body and use the
	 * data service to login
	 * 
	 * @param credential AdminModel that was sent in the request body
	 * @return response back from the data service
	 * @throws CredentialException exception thrown if the credentials are invalid
	 */
	public LoginModel login(UserModel credential) throws CredentialException {
		String email = credential.getEmail();
		String password = credential.getPassword();

		// Find id
		UserEntity user = dataService.findResidentByEmailAndPassword(email, password);

		// If the password matches the encoded one in the database, check if the user is
		// verified
		if (passwordEncoder.matches(password, user.getPassword())) {
			// Find if verified
			Boolean verified = dataService.checkVerified(user.getResidentId());

			return new LoginModel(user.getResidentId(), verified);
		} else {
			throw new CredentialException("Invalid credentials.");
		}
	}

	// TODO: remove is connected (possibly)
	/**
	 * Retrieves a list of residents in a neighboring unit by resident unit floor
	 * and room.
	 * 
	 * @param residentId : id of the resident (user) who is checking for neighboring
	 *                   residents
	 * @param floor      : floor the unit is on
	 * @param room       : room the unit is in
	 * @return a list of unit residents
	 */
	public ArrayList<UnitResidentModel> getNeighborResidents(UUID residentId, int floor, int room) {

		// Empty array that will be returned with residents (will be empty if there are
		// no residents)
		ArrayList<UnitResidentModel> unitResidents = new ArrayList<UnitResidentModel>();

		// Retrieve a list of resident UUIDs
		List<UUID> neighborIds = dataService.findNeighborsByUnit(floor, room);

		// Check if the resident is connected with the user resident are connected
		for (UUID neighborId : neighborIds) {
			// If yes, get their full name (if applicable) and profile picture
			if (dataService.checkConnected(residentId, neighborId)) {
				String name = dataService.getResidentName(neighborId, true);
				String profilePhoto = dataService.getProfilePhoto(neighborId);

				// If they don't have a profile photo, use the constructor without the profile
				// photo (which sets it to null)
				if (profilePhoto == null) {
					unitResidents.add(new UnitResidentModel(neighborId, name, true));
				} else {
					unitResidents.add(new UnitResidentModel(neighborId, name, profilePhoto, true));
				}
			}
			// If not connected, check if there is a pending invite
			// Get their first name (no profile photo)
			else {
				String name = dataService.getResidentName(neighborId, false);
				unitResidents.add(new UnitResidentModel(neighborId, name, false));
			}
		}

		// Return the list
		return unitResidents;
	}

	/**
	 * Uses the resident id to find the unit id from the lease, then find the unit.
	 * 
	 * @param residentId : id of the resident who is finding their neighbor
	 * @return a list of NeighborRoomsModel (s) which has details about the
	 *         resident's neighbors
	 * @throws Exception exception thrown if there is an exception in the data
	 *                   service
	 */
	public List<NeighborRoomModel> getNeighborUnits(UUID residentId) throws Exception {
		ArrayList<NeighborRoomModel> neighborRooms = new ArrayList<NeighborRoomModel>();

		// TODO: minimize number of calls?
		// Retrieve a residents unit's id from the lease using the foreign key in the
		// resident's table
		UUID unitId = dataService.findUnitByUserId(residentId);

		// Retrieve unit details
		UnitEntity unit = dataService.findUnitById(unitId);

		// Retrieve the building id
		UUID buildingId = unit.getBuildingId();

		// TODO: make enum for direction
		// Check the building if there are rooms up
		boolean hasAbove = dataService.checkNeighborFloor(buildingId, (unit.getFloor() + 1));
		if (hasAbove)
			neighborRooms.add(new NeighborRoomModel("top", (unit.getFloor() + 1), unit.getRoom()));

		// Check if there is a neighbor to the right
		boolean hasRight = dataService.checkNeighborRoom(buildingId, (unit.getRoom() + 1));
		if (hasRight)
			neighborRooms.add(new NeighborRoomModel("right", unit.getFloor(), (unit.getRoom() + 1)));

		// Check if there is a neighbor below
		boolean hasBelow = dataService.checkNeighborFloor(buildingId, (unit.getFloor() - 1));
		if (hasBelow)
			neighborRooms.add(new NeighborRoomModel("below", (unit.getFloor() - 1), unit.getRoom()));

		// Check if there is a neighbor to the left
		boolean hasLeft = dataService.checkNeighborRoom(buildingId, (unit.getRoom() - 1));
		if (hasLeft)
			neighborRooms.add(new NeighborRoomModel("left", unit.getFloor(), (unit.getRoom() - 1)));

		for (NeighborRoomModel n : neighborRooms) {
			System.out.println(n.getDirection());
		}

		return neighborRooms;
	}

	/**
	 * Retrieve a resident by their id.
	 * 
	 * @param residentId : id of the resident
	 * @return a resident model which contains the resident's information
	 */
	public ResidentModel getResident(UUID residentId) {
		// Retrieve the resident by id
		ResidentEntity residentEntity = dataService.findResidentById(residentId);

		// Convert the entity into a model
		return new ResidentModel(
				residentEntity.getAge(),
				residentEntity.getHometown(),
				residentEntity.getBiography(),
				residentEntity.getProfilePhoto(),
				residentEntity.getBackgroundPhoto(),
				residentEntity.getInstagram(),
				residentEntity.getSnapchat(),
				residentEntity.getX(),
				residentEntity.getFacebook(),
				residentEntity.getId(),
				residentEntity.getFirstName(),
				residentEntity.getLastName(),
				residentEntity.getGender());
	}

	/**
	 * Updates a resident
	 * 
	 * @param residentId   : id of the resident
	 * @param residentInfo : model that has information of the resident that will be
	 *                     updated
	 * @return a boolean if the resident was successfully updated
	 * @throws Exception if there is a problem updating the resident
	 */
	public boolean updateResident(UUID residentId, OptionalResidentModel residentInfo) throws Exception {

		// If not in the response, string will be null, number will be 0
		// check to see if the database matches the request
		ResidentEntity resident = dataService.findResidentById(residentId);

		// Update the age if sent in the request
		if (residentInfo.getAge() != 0) {
			resident.setAge(residentInfo.getAge());
		}
		// Update the hometown if sent in the request
		if (residentInfo.getHometown() != null) {
			resident.setHometown(residentInfo.getHometown());
		}
		// Update the biography if sent in the request
		if (residentInfo.getBiography() != null) {
			resident.setBiography(residentInfo.getBiography());
		}
		// Update the profile photo if sent in the request
		if (residentInfo.getProfilePhoto() != null) {
			resident.setProfilePhoto(residentInfo.getProfilePhoto());
		}
		// Update the background photo if sent in the request
		if (residentInfo.getBackgroundPhoto() != null) {
			resident.setBackgroundPhoto(residentInfo.getBackgroundPhoto());
		}
		// Update instagram if sent in the request
		if (residentInfo.getInstagram() != null) {
			resident.setInstagram(residentInfo.getInstagram());
		}
		// Update snapchat if sent in the request
		if (residentInfo.getSnapchat() != null) {
			resident.setSnapchat(residentInfo.getSnapchat());
		}
		// Update x if sent in the request
		if (residentInfo.getX() != null) {
			System.out.println(residentInfo.getX() + "to update");
			resident.setX(residentInfo.getX());
		}
		// Update facebook if sent in the request
		if (residentInfo.getFacebook() != null) {
			resident.setFacebook(residentInfo.getFacebook());
		}

		// Update the resident
		dataService.updateResident(resident);

		// Updated resident
		ResidentEntity residentUpdated = dataService.findResidentById(residentId);

		// If the resident matches the entity sent back by the data service, return true
		// that the resident was updated
		if (residentUpdated == resident) {
			return true;
		}
		// Else return false
		return false;
	}

	// public boolean register(ResidentModel resident, UUID leaseId){
	/**
	 * Saves a resident
	 * 
	 * @param resident : model sent in the request that contains information about
	 *                 the resident
	 * @return a boolean if the resident was successfully registered
	 * @throws Exception thrown if there is an error in the data service
	 */
	public boolean register(RegisterModel resident) throws Exception {
		// Convert the model to a entity to be passed to the data service
		ResidentEntity residentEntity = new ResidentEntity(UUID.fromString(resident.getId()), resident.getFirstName(),
				resident.getLastName(), Gender.valueOf(resident.getGender()), resident.getAge(), resident.getHometown(),
				resident.getBiography(), resident.getProfilePhoto(), resident.getBackgroundPhoto(),
				resident.getInstagram(), resident.getSnapchat(), resident.getX(), resident.getFacebook(),
				UUID.fromString(resident.getLeaseId()), false);

		// Use the data service to create the resident and return the boolean response
		return dataService.createResident(residentEntity);
	}

	/**
	 * Retrieves the id of the lease given the building address, name, floor, room,
	 * start date, and end date.
	 * 
	 * @param address   : street address of the building on the lease
	 * @param name      : name of the building on the lease
	 * @param floor     : floor the unit of the lease is on
	 * @param room      : room number the unit of the lease is in
	 * @param startDate : start date of the lease
	 * @param endDate   : end date of the lease
	 * @return the id of the lease
	 * @throws Exception if the lease could not be found
	 */
	public UUID getLease(String address, String name, int floor, int room, String startDate, String endDate)
			throws Exception {
		// Retrieve the lease given lease details
		return dataService.getLeaseId(address, name, floor, room, startDate, endDate);

	}
}
