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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.knockk.api.business.ResidentBusinessService;
import com.knockk.api.model.FriendshipModel;
import com.knockk.api.model.LoginModel;
import com.knockk.api.model.NeighborRoomModel;
import com.knockk.api.model.ResponseModel;
import com.knockk.api.model.UnitResidentModel;
import com.knockk.api.model.UserModel;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
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
	 * Shows details of a friendship, by resident id and friend's resident id.
	 * 
	 * Used to see if the resident is connected with the neighbor (to see if it's
	 * pending).
	 * A friendship that doesn't exist will throw a 404 error.
	 * 
	 * NOTE: Should ensure that the friend is a neighbor of the resident. May not be
	 * necessary because
	 * viewing a room fetches if there is a friendship or not.
	 * 
	 * @param id       : id of the resident
	 * @param friendId : if of the neighboring resident
	 * @return a ResponseEntity with a status code, message, and data
	 */
	@GetMapping("/{residentId}/friendship/{friendId}")
	public ResponseEntity<?> getMethodName(@PathVariable("residentId") String id,
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
		// Get neighboring units
		try {
			UUID residentId = UUID.fromString(id);
			// TODO: Should return error if resident id is wrong?
			List<NeighborRoomModel> neighbors = service.getNeighborUnits(residentId);

			// ResponseModel with a list of neighboring rooms, message, and status code
			ResponseModel<List<NeighborRoomModel>> response = new ResponseModel<List<NeighborRoomModel>>(neighbors,
					"Success", 200);
			// Return response
			return new ResponseEntity<ResponseModel<List<NeighborRoomModel>>>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();

			return handleErrorResponse(e);
		}
	}

	/**
	 * Logs the user in
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
			if (errors.hasErrors())
				throw new IllegalArgumentException("Bad request");

			LoginModel user = service.login(credentials);

			ResponseModel<LoginModel> response = new ResponseModel<LoginModel>(user, "Login Successful", 204);
			return new ResponseEntity<ResponseModel<LoginModel>>(response, HttpStatus.OK); // using NO_CONTENT will not
																							// return a response body
		} catch (Exception e) {
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

		// Bad request - request body is invalid
		if (e.getMessage().toLowerCase().contains("bad request")) {
			response.setMessage(e.getLocalizedMessage());
			response.setStatus(400);
			return new ResponseEntity<ResponseModel<HashMap<String, String>>>(response, HttpStatus.FORBIDDEN);
		}
		// Forbidden
		else if (e.getMessage().toLowerCase().contains("invalid credentials")) {
			// response.setMessage(e.getLocalizedMessage());
			response.setMessage("Forbidden. Invalid credentials.");
			response.setStatus(403);
			return new ResponseEntity<ResponseModel<HashMap<String, String>>>(response, HttpStatus.BAD_REQUEST);
		}
		// Not Found
		else if (e.getMessage().toLowerCase().contains("not found")) {
			response.setMessage("Not Found");
			response.setStatus(404);
			return new ResponseEntity<ResponseModel<HashMap<String, String>>>(response, HttpStatus.NOT_FOUND);
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
