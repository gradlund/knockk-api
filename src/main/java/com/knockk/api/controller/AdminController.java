/**
 * Provides controller classes
 */
package com.knockk.api.controller;

import java.util.HashMap;
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

import com.knockk.api.business.AdminBusinessService;
import com.knockk.api.model.AdminModel;
import com.knockk.api.model.ResponseModel;

import jakarta.validation.Valid;

/**
 * This class is the rest controller the resident application consumes
 * @author graceradlund
 */
@RestController
@RequestMapping("/admin")
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
			ResponseModel<UUID> response = new ResponseModel<UUID>(data, "Login Successful", 204);
			return new ResponseEntity<ResponseModel<UUID>>(response, HttpStatus.OK); // using NO_CONTENT will not return
																						// a response body
		}
		//If there is an exception, throw an error
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

		ResponseModel<String> response = new ResponseModel<String>(data);

		// Bad request - request body is invalid
		if (e.getMessage().toLowerCase().contains("bad request")) {
			response.setMessage(e.getLocalizedMessage());
			response.setStatus(400);
			return new ResponseEntity<ResponseModel<String>>(response, HttpStatus.FORBIDDEN);
		}
		// Forbidden
		else if (e.getMessage().toLowerCase().contains("invalid credentials")) {
			// response.setMessage(e.getLocalizedMessage());
			response.setMessage("Forbidden. Invalid credentials.");
			response.setStatus(403);
			return new ResponseEntity<ResponseModel<String>>(response, HttpStatus.BAD_REQUEST);
		}
		// Internal Server Error
		else {
			response.setMessage(
					"Internal Service Error. The server was unable to complete your request. Please try again later.");
			response.setStatus(500);
			return new ResponseEntity<ResponseModel<String>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
