package com.knockk.api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.knockk.api.business.ResidentBusinessService;
import com.knockk.api.model.FriendshipModel;
import com.knockk.api.model.LoginModel;
import com.knockk.api.model.NeighborRoomModel;
import com.knockk.api.model.OptionalResidentModel;
import com.knockk.api.model.RegisterModel;
import com.knockk.api.model.ResidentModel;
import com.knockk.api.model.ResponseModel;
import com.knockk.api.model.UnitResidentModel;
import com.knockk.api.model.UserModel;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * This class is the rest controller the admin application consumes
 * 
 * @author graceradlund
 */
@RestController
@RequestMapping("/residents")
public class ResidentController {

	private @Autowired ResidentBusinessService service;

	/**
	 * Creates a friendship between two residents, by resident id and neighbor's
	 * resident id.
	 * 
	 * Used when a resident makes a request to connect with their neighbor. Defaults
	 * accepted to false.
	 * 
	 * @param friendshipRequest : model used in the request
	 * @return a ResponseEntity with a status code, message, and data
	 */
	@PostMapping("/friendship")
	public ResponseEntity<?> updateFriendship(@RequestBody FriendshipModel friendshipRequest, Errors errors) {
		try {
			// If the request does not contain the valid model, throw an exception
			if (errors.hasErrors())
				throw new IllegalArgumentException("Bad request");

			// Update friendship (create one) using the invitor's id, invitee's id, and if
			// accepted (which should be false if creating one)
			// isAccepted field is not used from the request
			FriendshipModel friendship = service.updateFriendship(friendshipRequest.getInvitorId(),
					friendshipRequest.getInviteeId(), friendshipRequest.isAccepted());

			// ResponseModel with the friendship model, message, and status code
			ResponseModel<FriendshipModel> response = new ResponseModel<FriendshipModel>(friendship,
					"Success", 200);
			// Return response
			return new ResponseEntity<ResponseModel<FriendshipModel>>(response, HttpStatus.OK);

		}
		// Handle errors
		catch (Exception e) {
			e.printStackTrace();

			return handleErrorResponse(e);
		}
	}

	@PostMapping("/create-account")
	@ResponseBody
	public ResponseEntity<?> createAccount(@Valid @RequestBody UserModel credentials, Errors errors) {
		try {
			// If the request does not contain the valid model, throw an exception
			if (errors.hasErrors())
				throw new IllegalArgumentException("Bad request");

			// Otherwise, register the account. If the email already exists, it will throw
			// an error
			UUID userId = service.createAccount(credentials);

			// Return a response
			ResponseModel<UUID> response = new ResponseModel<UUID>(userId, "Account Creation Successful", 204);
			return new ResponseEntity<ResponseModel<UUID>>(response, HttpStatus.OK); // using NO_CONTENT will not
																						// return a response body
		}
		// Handle errors
		catch (Exception e) {
			e.printStackTrace();

			return handleErrorResponse(e);
		}
	}

	@GetMapping("/lease")
	@ResponseBody
	public ResponseEntity<?> getLease(@RequestParam(name = "address") String address,
			@RequestParam(name = "buildingName") String name, @RequestParam(name = "floor") int floor,
			@RequestParam(name = "room") int room, @RequestParam(name = "startDate") String startDate,
			@RequestParam(name = "endDate") String endDate) {
		try {
			UUID leaseId = service.getLease(address, name, floor, room, startDate, endDate);

			// Return a response
			ResponseModel<UUID> response = new ResponseModel<UUID>(leaseId, "Account Creation Successful", 204);
			return new ResponseEntity<ResponseModel<UUID>>(response, HttpStatus.OK); // using NO_CONTENT will not
																						// return a response body
		}
		// Handle errors
		catch (Exception e) {
			e.printStackTrace();

			return handleErrorResponse(e);
		}
	}

	/**
	 * Shows details of a friendship, by resident id and friend's resident id.
	 * 
	 * Used to see if the resident is connected with the neighbor (to see if it's
	 * pending).
	 * A friendship that doesn't exist will throw a 404 error.
	 * 
	 * NOTE: Should ensure that the friend is a neighbor of the resident. May not be
	 * necessary because viewing a room fetches if there is a friendship or not.
	 * 
	 * @param id       : id of the resident
	 * @param friendId : if of the neighboring resident
	 * @return a ResponseEntity with a status code, message, and data
	 */
	@GetMapping("/{residentId}/friendship/{friendId}")
	public ResponseEntity<?> getFriendship(@PathVariable("residentId") String id,
			@PathVariable("friendId") String friendId) {
		try {
			// Retrieve the id's from the parameters
			UUID residentId = UUID.fromString(id);
			UUID neighborId = UUID.fromString(friendId);

			// Retrieve the friendship (if one exists)
			FriendshipModel friendship = service.getFriendship(residentId, neighborId);

			// ResponseModel with a list of neighboring residents of the unit, message, and
			// status code
			ResponseModel<FriendshipModel> response = new ResponseModel<FriendshipModel>(friendship,
					"Success", 200);
			// Return response
			return new ResponseEntity<ResponseModel<FriendshipModel>>(response, HttpStatus.OK);
		}
		// Handle errors
		catch (Exception e) {
			e.printStackTrace();

			return handleErrorResponse(e);
		}
	}

	/**
	 * Removes a friendship, by resident id and freind's resident id.
	 * 
	 * Used when a resident unconnects with a friend.
	 * 
	 * @param id       : id of the resident trying to remove the friendship
	 * @param friendId : id of the friend the resident no longer wants to be friends
	 *                 with
	 * @return a ResponseEntity with a status code, message, and data
	 */
	@DeleteMapping("/{residentId}/friendship/{friendId}")
	public ResponseEntity<?> deleteFriendship(@PathVariable("residentId") String id,
			@PathVariable("friendId") String friendId) {
		try {
			// Retrieve the id's from the parameters
			UUID residentId = UUID.fromString(id);
			UUID neighborId = UUID.fromString(friendId);

			// Delete the friendship (if one exists)
			boolean isDeleted = service.deleteFriendship(residentId, neighborId);

			// If it has been deleted, return response
			if (isDeleted) {
				ResponseModel<String> response = new ResponseModel<String>("Success. Deleted friendship.",
						"Success", 204);

				// Return response
				return new ResponseEntity<ResponseModel<String>>(response, HttpStatus.OK);
			}
			// If friendship was not deleted, throw an exception
			else {
				throw new Exception("Error deleting friendship.");
			}
		}
		// Handle errors
		catch (Exception e) {
			e.printStackTrace();

			return handleErrorResponse(e);
		}
	}

	@GetMapping("/building/{street}")
	public ResponseEntity<?> getBuilding(@PathVariable("street") String street) {
		try {

			List<String> buildings = service.getBuildings(street);

			// ResponseModel with a list of neighboring residents of the unit, message, and
			// status code
			ResponseModel<List<String>> response = new ResponseModel<List<String>>(buildings,
					"Success", 200);
			// Return response
			return new ResponseEntity<ResponseModel<List<String>>>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();

			return handleErrorResponse(e);
		}
	}

	/**
	 * Retrieve a list of residents in a neighboring unit
	 * 
	 * TODO: make sure params are valid. make sure they are acutally neighbors? do
	 * something with the id
	 * 
	 * @param id:   id of the resident (user)
	 * @param floor : floor that the neighboring unit is on
	 * @param room  : room that the neighboring unit is
	 * @return a ResponseEntity with a status code, message, and data
	 */
	@GetMapping("/{residentId}/neighbor-units/{floor}-{room}")
	public ResponseEntity<?> getNeighborResidents(@PathVariable("residentId") String id,
			@PathVariable("floor") int floor, @PathVariable("room") int room) {
		try {
			UUID residentId = UUID.fromString(id);

			// TODO: make a check to make sure they aren't veiwing their unit for some
			// reason
			// Get the residents of the unit
			// NOTE: will return nothing if there are no residents in that unit, or the unit
			// does not yet exist in the system
			ArrayList<UnitResidentModel> residents = service.getNeighborResidents(residentId, floor, room);

			// ResponseModel with a list of neighboring residents of the unit, message, and
			// status code
			ResponseModel<List<UnitResidentModel>> response = new ResponseModel<List<UnitResidentModel>>(residents,
					"Success", 200);
			// Return response
			return new ResponseEntity<ResponseModel<List<UnitResidentModel>>>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();

			return handleErrorResponse(e);
		}
	}

	// TODO: make sure resident id is valid?
	// TODO: make sure resident is actually neighbors with them. (that a hacker is
	// not trying to visit a room they're not neighbors with)
	// ^^ will that be solved with a jwt?
	/**
	 * Retrieve neighboring rooms (getResidentResidentIdNeighboringRooms)
	 * Shows details of rooms that the resident is neighbors with, by resident id.
	 * Used when a resident is at the root page. Will display all directions the
	 * resident has a neighbor. For example, if there is no neighbor above,
	 * there will be arrows point to the right, down, and left - but not up.
	 * 
	 * @param id : id of the resident making the request
	 * @return a ResponseEntity with a status code, message, and data
	 */
	@GetMapping("/{residentId}/neighbor-units")
	public ResponseEntity<?> getNeighborUnits(@PathVariable("residentId") String id) {
		try {
			// Retrieve the resident's id from the path parameters
			UUID residentId = UUID.fromString(id);

			// TODO: Should return error if resident id is wrong?
			// Retrieve a list of neighboring units
			List<NeighborRoomModel> neighbors = service.getNeighborUnits(residentId);

			// ResponseModel with a list of neighboring rooms, message, and status code
			ResponseModel<List<NeighborRoomModel>> response = new ResponseModel<List<NeighborRoomModel>>(neighbors,
					"Success", 200);
			// Return response
			return new ResponseEntity<ResponseModel<List<NeighborRoomModel>>>(response, HttpStatus.OK);
		}
		// Handle errors
		catch (Exception e) {
			e.printStackTrace();

			return handleErrorResponse(e);
		}
	}

	/**
	 * Show details of a resident, by resident id.
	 * 
	 * Used when displaying the resident profile, or when viewing a connected
	 * neighbor. Not necessary for displaying an unconnected neighbor because that
	 * only displays their name, which is fetched in the
	 * /residents/{residentId}/neighbor-rooms/{unit-id} call.
	 * 
	 * @param id : id of the resident
	 * @return a ResponseEntity with a status code, message, and data
	 */
	@GetMapping("/{residentId}")
	public ResponseEntity<?> getResident(@PathVariable("residentId") String id) {
		try {
			// Retrieve the resident's id from the path variable
			UUID residentId = UUID.fromString(id);

			// Retrieve resident by id
			ResidentModel resident = service.getResident(residentId);

			// ResponseModel with a list of neighboring rooms, message, and status code
			ResponseModel<ResidentModel> response = new ResponseModel<ResidentModel>(resident,
					"Success", 200);
			// Return response
			return new ResponseEntity<ResponseModel<ResidentModel>>(response, HttpStatus.OK);
		}
		// Handle errors
		catch (Exception e) {
			e.printStackTrace();

			return handleErrorResponse(e);
		}
	}

	/**
	 * Update a resident, by resident id. Only voluntary information upon
	 * registration can be updated.
	 * 
	 * Used when the resident wants to update their profile. Resident should not be
	 * able to update their name, gender, or information pertaining to their lease.
	 * Resident should only be able to update their own profile.
	 * 
	 * @param id           : id of the resident
	 * @param residentInfo : model sent in the request that has optional fields the
	 *                     resident can update
	 * @return a ResponseEntity with a status code, message, and data
	 */
	@PostMapping("/{residentId}")
	public ResponseEntity<?> updateResident(@PathVariable("residentId") String id,
			@RequestBody OptionalResidentModel residentInfo) {
		try {
			// Retrieve the resident's id from the path variable
			UUID residentId = UUID.fromString(id);

			// Update the resident using the business service class
			// Data service class throws an error if resident could not be updated
			boolean updated = service.updateResident(residentId, residentInfo);

			// If updated, send back a response
			// if (updated) {
			// ResponseModel with a list of neighboring rooms, message, and status code
			ResponseModel<OptionalResidentModel> response = new ResponseModel<OptionalResidentModel>(residentInfo,
					"Success", 200);
			// Return response
			return new ResponseEntity<ResponseModel<OptionalResidentModel>>(response, HttpStatus.OK);
			// }
			// // Else, throw an exception
			// else {
			// throw new Exception("Resident was not updated.");
			// }
		}
		// Handle errors
		catch (Exception e) {
			e.printStackTrace();

			return handleErrorResponse(e);
		}
	}

	/**
	 * Logs the user in
	 * 
	 * Used when the resident logs in to the app. Verifies the credentials in the
	 * database. Important to note that the password will be hashed.
	 * 
	 * @param credentials : credentials passed by the admin in the request
	 * @param errors      : data-binding and validation errors relating to the
	 *                    request body
	 * @return a ResponseEntity with a status code, message, and data
	 */
	@PostMapping("/login")
	@ResponseBody
	public ResponseEntity<?> login(@Valid @RequestBody UserModel credentials, Errors errors) {
		try {
			// If the request does not contain the valid model, throw an exception
			if (errors.hasErrors())
				throw new IllegalArgumentException("Bad request");

			System.out.println("hi");
			System.out.println(credentials.getEmail());

			// Otherwise, log the user in using the credentials
			LoginModel user = service.login(credentials);

			// Return a response
			ResponseModel<LoginModel> response = new ResponseModel<LoginModel>(user, "Login Successful", 204);
			return new ResponseEntity<ResponseModel<LoginModel>>(response, HttpStatus.OK); // using NO_CONTENT will not
																							// return a response body
		}
		// Handle errors
		catch (Exception e) {
			e.printStackTrace();

			return handleErrorResponse(e);
		}
	}

	@PostMapping("/")
	@ResponseBody // Could use ResidentModel
	public ResponseEntity<?> register(@Valid @RequestBody RegisterModel registerInfo, Errors errors) {
		try {
			// If the request does not contain the valid model, throw an exception
			if (errors.hasErrors()) {
				System.out.println(errors);
				throw new IllegalArgumentException("Bad request");
			}

			System.out.println(registerInfo.getLeaseId());
			Boolean registered = service.register(registerInfo);
			// Boolean registered = true;

			// ResponseModel with boolean, message, and status code
			ResponseModel<Boolean> response = new ResponseModel<Boolean>(registered,
					"Success", 201);
			// Return response
			return new ResponseEntity<ResponseModel<Boolean>>(response, HttpStatus.OK);

		}
		// Handle errors
		catch (Exception e) {
			e.printStackTrace();

			return handleErrorResponse(e);
		}
	}

	/**
	 * Method to handle the responses of errors
	 * 
	 * @param e : error that occured
	 * @return a ResponseEntity with a status code message, and data
	 */
	public ResponseEntity<?> handleErrorResponse(Exception e) {
		HashMap<String, String> data = new HashMap<>();
		data.put("Error", e.getMessage());

		ResponseModel<HashMap<String, String>> response = new ResponseModel<HashMap<String, String>>(data);
		// TODO: ADD MORE CONTAINS FOR OTHER ERROR MESSAGES

		// Bad request - request body is invalid
		if (e.getMessage().toLowerCase().contains("bad request")) {
			response.setMessage(e.getLocalizedMessage());
			response.setStatus(400);
			return new ResponseEntity<ResponseModel<HashMap<String, String>>>(response, HttpStatus.FORBIDDEN);
		}
		// Automatically does this for request param
		// Bad request - request parameter is invalid
		// if (e.getMessage().toLowerCase().contains("failed to convert value of type"))
		// {
		// response.setMessage(e.getLocalizedMessage());
		// response.setStatus(400);
		// return new ResponseEntity<ResponseModel<HashMap<String, String>>>(response,
		// HttpStatus.BAD_REQUEST);
		// }
		// Forbidden
		else if (e.getMessage().toLowerCase().contains("invalid credentials")) {
			// response.setMessage(e.getLocalizedMessage());
			response.setMessage("Forbidden. Invalid credentials.");
			response.setStatus(403);
			return new ResponseEntity<ResponseModel<HashMap<String, String>>>(response, HttpStatus.BAD_REQUEST);
		}
		// Not Found
		else if (e.getMessage().toLowerCase().contains("not found")) {
			System.out.println("Not found");
			response.setMessage("Not Found");
			response.setStatus(404);
			return new ResponseEntity<ResponseModel<HashMap<String, String>>>(response, HttpStatus.NOT_FOUND);
		}
		// Already exists
		else if (e.getMessage().toLowerCase().contains("already exists")) {
			response.setMessage("Already Exists");
			response.setStatus(302);
			return new ResponseEntity<ResponseModel<HashMap<String, String>>>(response, HttpStatus.FOUND);
		}
		// Does not exist
		else if (e.getMessage().toLowerCase().contains("does not exist")) {
			response.setMessage("Does not exist");
			response.setStatus(404);
			return new ResponseEntity<ResponseModel<HashMap<String, String>>>(response, HttpStatus.NOT_FOUND);
		}
		// Problem retrieving
		else if (e.getMessage().toLowerCase().contains("problem getting")) {
			response.setMessage("Problem retrieving. May not exist.");
			response.setStatus(404); // TODO - should be a diffenent stat
			return new ResponseEntity<ResponseModel<HashMap<String, String>>>(response, HttpStatus.NOT_FOUND);
		}
		// Problem updating or saving
		else if (e.getMessage().toLowerCase().contains("could not update")
				|| e.getMessage().toLowerCase().contains("could not save")) { // thrown in data service for updating
			// resident
			response.setMessage("Problem updating or saving.");
			response.setStatus(500); // TODO - should be a diffenent stat
			return new ResponseEntity<ResponseModel<HashMap<String, String>>>(response,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// Internal Server Error
		else {
			response.setMessage(
					"Internal Service Error. The server was unable to complete your request. Please try again later.");
			response.setStatus(500);
			return new ResponseEntity<ResponseModel<HashMap<String, String>>>(response,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
