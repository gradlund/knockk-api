package com.knockk.api.business;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.security.auth.login.CredentialException;

import org.springframework.stereotype.Service;

import com.knockk.api.data.service.ResidentDataService;
import com.knockk.api.entity.UnitEntity;
import com.knockk.api.model.LoginModel;
import com.knockk.api.model.NeighborRoomModel;
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

	/**
	 * Uses the resident id to find the unit id from the lease, then find the unit.
	 * @param residentId : id of the resident who is finding their neighbor
	 * @return a list of NeighborRoomsModel (s) which has details about the resident's neighbors
	 * @throws Exception exception thrown if there is an exception in the data service
	 */
	public List<NeighborRoomModel> getNeighborUnits(UUID residentId) throws Exception{
		ArrayList<NeighborRoomModel> neighborRooms = new ArrayList<NeighborRoomModel>();
		
		// TODO: minimize number of calls?
		// Retrieve a residents unit's id from the lease using the foreign key in the resident's table
		UUID unitId = dataService.findUnitByUserId(residentId);

		// Retrieve unit details
		UnitEntity unit = dataService.findUnitById(unitId);

		// Retrieve the building id
		UUID buildingId = unit.getBuildingId();

		// TODO: make enum for direction
		// Check the building if there are rooms up
		boolean hasAbove = dataService.checkNeighborFloor(buildingId, (unit.getFloor() + 1));
		if(hasAbove) neighborRooms.add(new NeighborRoomModel("top", (unit.getFloor() + 1), unit.getRoom()));

		// Check if there is a neighbor to the right
		boolean hasRight = dataService.checkNeighborRoom(buildingId, (unit.getRoom() + 1));
		if(hasRight) neighborRooms.add(new NeighborRoomModel("right", unit.getFloor(), (unit.getRoom() + 1)));

		// Check if there is a neighbor below
		boolean hasBelow = dataService.checkNeighborFloor(buildingId, (unit.getFloor() - 1));
		if(hasBelow) neighborRooms.add(new NeighborRoomModel("below", (unit.getFloor() - 1), unit.getRoom()));

		// Check if there is a neighbor to the left
		boolean hasLeft = dataService.checkNeighborRoom(buildingId, (unit.getRoom() - 1));
		if(hasLeft) neighborRooms.add(new NeighborRoomModel("left", unit.getFloor(), (unit.getRoom() - 1)));

		for(NeighborRoomModel n : neighborRooms){
			System.out.println(n.getDirection());
		}

		return neighborRooms;
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

	// TODO: get unit - show an error if no units are registered
}
