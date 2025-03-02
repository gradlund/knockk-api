/**
 * Provides controller classes
 */
package com.knockk.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties.Build;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.knockk.api.business.AdminBusinessService;
import com.knockk.api.model.AdminModel;
import com.knockk.api.model.AdminResidentModel;
import com.knockk.api.model.BuildingModel;
import com.knockk.api.model.ResidentModel;
import com.knockk.api.model.ResponseModel;
import com.knockk.api.model.UserModel;

import com.knockk.api.model.RequestWithId;

import jakarta.validation.Valid;

/**
 * This class is the rest controller the resident application consumes
 * @author graceradlund
 */
@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:8081")
public class AdminController {

	private @Autowired AdminBusinessService service;

	/**
	 * Logs the user in
	 * @param credentials : credentials passed by the resident in the request
	 * @param errors : data-binding and validation errors relating to the request body
	 * @return a ResponseEntity with a status code, message, and data
	 */
	@PostMapping( "/login")
	@ResponseBody
	public ResponseEntity<?> login(@Valid @RequestBody AdminModel credentials, Errors errors) {
		try {
			//If there are errors with the request, throw an exception
			if (errors.hasErrors())
				throw new IllegalArgumentException("Bad request");

			//Login the user with their credentials and receive the users id
			UUID id = service.login(credentials);

			//Initialize a HashMap for the data in the response with the id
			HashMap<String, UUID> data = new HashMap<String, UUID>();
			data.put("Id", id);

			//Return the response
			ResponseModel<HashMap<String, UUID>> response = new ResponseModel<HashMap<String, UUID>>(data, "Login Successful", 204);
			return new ResponseEntity<ResponseModel<HashMap<String, UUID>>>(response, HttpStatus.OK); // using NO_CONTENT will not return
																						// a response body
		}
		//If there is an exception, throw an error
		catch (Exception e) {
			e.printStackTrace();

			return handleErrorResponse(e);
		}
	}

	@GetMapping("/buildings/{adminId}")
	public ResponseEntity<?> getBuildings(@PathVariable("adminId") String id) {
		try {
			System.out.println(id);
			// Retrieve the admin's id from the path variable
			UUID adminId = UUID.fromString(id);

			// Retrieve the buildings the admin manages
			List<BuildingModel> buildings = service.getBuildings(adminId);

			// ResponseModel with a list of neighboring rooms, message, and status code
			ResponseModel<List<BuildingModel>> response = new ResponseModel<List<BuildingModel>>(buildings,
					"Success", 200);
			// Return response
			return new ResponseEntity<ResponseModel<List<BuildingModel>>>(response, HttpStatus.OK);
		}
		// Handle errors
		catch (Exception e) {
			e.printStackTrace();

			return handleErrorResponse(e);
		}
	}

	// What would default request param be?
	@GetMapping("/{buildingId}/residents")
	public ResponseEntity<?> getResidents(@PathVariable("buildingId") String id, @RequestParam(defaultValue = "false") boolean verified, Pageable pageable) {
		try {
			// Retrieve the building's id from the path variable
			UUID buildingId = UUID.fromString(id);

			// Spring boot maps query params page, size, and sort to Pageable object

			// Retrieve the residents of the building
			List<AdminResidentModel> residents = service.getResidents(buildingId, verified, pageable);

			// ResponseModel with a list of neighboring rooms, message, and status code
			ResponseModel<List<AdminResidentModel>> response = new ResponseModel<List<AdminResidentModel>>(residents,
					"Success", 200);
			// Return response
			return new ResponseEntity<ResponseModel<List<AdminResidentModel>>>(response, HttpStatus.OK);
		}
		// Handle errors
		catch (Exception e) {
			e.printStackTrace();

			return handleErrorResponse(e);
		}
	}

		// What would default request param be?
		@GetMapping("/{buildingId}/")
		public ResponseEntity<?> getPageInfo(@PathVariable("buildingId") String id, @RequestParam(defaultValue = "false") boolean areVerified) {
			try {

				//TODO: have my own custom try catch to throw error, otherwise server will

				// Retrieve the building's id from the path variable
			UUID buildingId = UUID.fromString(id);
	
				// Retrieve the number of pages of the building
				int residents = service.getNumberOfResidents(buildingId, areVerified);
	
				// ResponseModel with a list of neighboring rooms, message, and status code
				ResponseModel<Integer> response = new ResponseModel<Integer>(residents,
						"Success", 200);
				// Return response
				return new ResponseEntity<ResponseModel<Integer>>(response, HttpStatus.OK);
			}
			// Handle errors
			catch (Exception e) {
				e.printStackTrace();
	
				return handleErrorResponse(e);
			}
		}

	@GetMapping("/{residentId}")
	public ResponseEntity<?> getResident(@PathVariable("residentId") String id) {
		try {
			// Retrieve the resident's id from the path variable
			UUID residentId = UUID.fromString(id);

			// Retrieve resident by id
			AdminResidentModel resident = service.getResident(residentId);

			// ResponseModel with a list of neighboring rooms, message, and status code
			ResponseModel<AdminResidentModel> response = new ResponseModel<AdminResidentModel>(resident,
					"Success", 200);
			// Return response
			return new ResponseEntity<ResponseModel<AdminResidentModel>>(response, HttpStatus.OK);
		}
		// Handle errors
		catch (Exception e) {
			e.printStackTrace();

			return handleErrorResponse(e);
		}
	}

	@PostMapping("/activate")
	@ResponseBody
	public ResponseEntity<?> activateResident(@Valid @RequestBody RequestWithId request, Errors errors) {
		try {
			// If the request does not contain the valid model, throw an exception
			if (errors.hasErrors())
				throw new IllegalArgumentException("Bad request");

			// Otherwise, activate the account
			System.out.println(request.getResidentId());

			// Retrieve the resident's id from the path variable
			UUID residentId = UUID.fromString(request.getResidentId());
			System.out.println(residentId);

			Boolean updated = service.activateResident(residentId);

			// Return a response
			ResponseModel<Boolean> response = new ResponseModel<Boolean>(updated, "Account Creation Successful", 204);
			return new ResponseEntity<ResponseModel<Boolean>>(response, HttpStatus.OK); // using NO_CONTENT will not
																						// return a response body
		}
		// Handle errors
		catch (Exception e) {
			e.printStackTrace();

			return handleErrorResponse(e);
		}
	}

	// TODO: add buildingid?
	@DeleteMapping("/residents/{residentId}")
	@ResponseBody
	public ResponseEntity<?> deleteResident(@PathVariable("residentId") String id) {
		try {

			// Retrieve the resident's id from the path variable
			UUID residentId = UUID.fromString(id);
			System.out.println(residentId);

			Boolean deleted = service.deleteResident(residentId);

			// Return a response
			ResponseModel<Boolean> response = new ResponseModel<Boolean>(deleted, "Account Creation Successful", 204);
			return new ResponseEntity<ResponseModel<Boolean>>(response, HttpStatus.NO_CONTENT); // using NO_CONTENT will not
																						// return a response body
		}
		// Handle errors
		catch (Exception e) {
			e.printStackTrace();

			return handleErrorResponse(e);
		}
	}


	/**
	 * Method to handle the responses of errors
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
			System.out.println("Not found");
			response.setMessage("Not Found");
			response.setStatus(404);
			return new ResponseEntity<ResponseModel<HashMap<String, String>>>(response, HttpStatus.NOT_FOUND);
		}
		else if(e.getMessage().toLowerCase().contains("could not")){
			response.setMessage(e.getMessage());
			response.setStatus(404);
			return new ResponseEntity<ResponseModel<HashMap<String, String>>>(response, HttpStatus.NOT_FOUND);
		}
		// Internal Server Error
		else {
			response.setMessage(
					"Internal Service Error. The server was unable to complete your request. Please try again later.");
			response.setStatus(500);
			return new ResponseEntity<ResponseModel<HashMap<String, String>>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
