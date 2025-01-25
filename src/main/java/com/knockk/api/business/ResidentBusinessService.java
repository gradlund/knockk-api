package com.knockk.api.business;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import javax.security.auth.login.CredentialException;

import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.stereotype.Service;

import com.knockk.api.data.service.ResidentDataService;
import com.knockk.api.entity.FriendshipEntity;
import com.knockk.api.entity.ResidentEntity;
import com.knockk.api.entity.UnitEntity;
import com.knockk.api.model.FriendshipModel;
import com.knockk.api.model.LoginModel;
import com.knockk.api.model.NeighborRoomModel;
import com.knockk.api.model.OptionalResidentModel;
import com.knockk.api.model.ResidentModel;
import com.knockk.api.model.UnitResidentModel;
import com.knockk.api.model.UserModel;

/**
 * This class implements the business service for residents
 * 
 * @author graceradlund
 */
@Service
public class ResidentBusinessService {
	ResidentDataService dataService;

	/**
	 * Constructor used for dependency injection
	 * 
	 * @param dataService data service used in for residents
	 */
	public ResidentBusinessService(ResidentDataService dataService) {
		this.dataService = dataService;
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

	// TODO: Wrap in try catch
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
		UUID id = dataService.findResidentByEmailAndPassword(email, password);

		// Find if verified
		Boolean verified = dataService.checkVerified(id);

		return new LoginModel(id, verified);
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
			FriendshipEntity friendship = optionalFriendship.get(); //error handling

			return new FriendshipModel(friendship.getInvitorId(), friendship.getInviteeId(), friendship.isAccepted());
			// // Return false if accepted, because it means it isn't pending
			// if (friendship.get().isAccepted()) {
			// 	return new FriendshipModel(friendship.get().getInvitorId(), friendship.get().getInviteeId(), false);
			// }
			// // Return a friendship, where pending is true because someone hasn't accepted
			// return new FriendshipModel(friendship.get().getInvitorId(), friendship.get().getInviteeId(), true);
		}
		// If friendship doesn't exist, throw an exception
		catch (Exception e) {
			throw new NoSuchElementException("Not Found. Friendship does not exist.");
		}
	}

	public ResidentModel getResident(UUID residentId) {
		ResidentEntity residentEntity = dataService.findResidentById(residentId);

		ResidentModel resident = new ResidentModel(
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

			return resident;
	}

	public boolean updateResident(UUID residentId, OptionalResidentModel residentInfo) {
		
			// If not in the response, string will be null, number will be 0
			//check to see if the database matches the request
			ResidentEntity resident = dataService.findResidentById(residentId);

			if(resident.getAge() != 0){
			resident.setAge(resident.getAge());
			}
			if(resident.getHometown() != null){
			resident.setHometown(resident.getHometown());
			}
			if(resident.getProfilePhoto() != null){
				resident.setProfilePhoto(resident.getProfilePhoto());
			}
			if(resident.getBackgroundPhoto() != null){
				resident.setBackgroundPhoto(resident.getBackgroundPhoto());
			}
			if(resident.getInstagram() != null){
				resident.setInstagram(resident.getInstagram());
			}
			if(resident.getSnapchat() != null){
				resident.setSnapchat(resident.getSnapchat());
			}
			if(resident.getX() != null){
				resident.setX(resident.getX());
			}
			if(resident.getFacebook() != null){
				resident.setFacebook(resident.getFacebook());
			}

			// Could I try to return boolean
			ResidentEntity updated = dataService.updateResident(resident);

			if(updated == resident){
				return true;
			}

			return false;

			//resident.save(resident)

			//ResidentEntity res = dataService.updateResidentById(residentId, )
	}

	public FriendshipModel updateFriendship(UUID invitorId, UUID inviteeId, boolean isAccepted) throws Exception {
		// TODO: make sure they are valid neighbors
		FriendshipEntity friendship = dataService.updateFriendship(invitorId, inviteeId, isAccepted);
		return new FriendshipModel(friendship.getInvitorId(), friendship.getInviteeId(), friendship.isAccepted());
	}

	// TODO: get unit - show an error if no units are registered
}
