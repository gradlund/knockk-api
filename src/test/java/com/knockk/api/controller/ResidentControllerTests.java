// Grace Radlund
// 12-15-2024
// Tests generated with the help of ChatGPT 4o mini
package com.knockk.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.knockk.api.business.ResidentBusinessService;
import com.knockk.api.model.LoginModel;
import com.knockk.api.model.ResponseModel;
import com.knockk.api.model.UserModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.Errors;

import java.util.UUID;

/**
 * Unit tests for the Resident Controller
 */
//Tests a specific controller without starting Spring Boot application
@WebMvcTest(ResidentController.class)
public class ResidentControllerTests {

	// Inject the MockMvc instance to simulate HTTP requests to the controller.
    @Autowired
    private MockMvc mockMvc;

    // Mock the ResidentBusinessService, which is used by the controller.
    @MockBean
    private ResidentBusinessService service;

    // Mock the Errors object to simulate validation errors during the login process.
    @Mock
    private Errors errors;

    // Declare a valid AdminModel to use in tests.
    private UserModel validUserModel;

    @BeforeEach
    public void setUp() {
    	// Initialize the validAdminModel with sample valid credentials before each test.
        validUserModel = new UserModel("validUsername", "validPassword");
    }

    // Test case for successful login.
    // Checks the response body for the correct message and id.
    @Test
    public void testLoginSuccess() throws Exception {
        // Mock the successful login response, returning a LoginModel with a mock UUID and a 'verified' status.
        // This simulates the login behavior when the credentials are correct.
        LoginModel mockLogin = new LoginModel(UUID.randomUUID(), false);

        // When the service's login method is called, it will return the mock LoginModel.
        when(service.login(any(UserModel.class))).thenReturn(mockLogin);

        // Log the mock login ID for debugging purposes (could be removed in production code).
        System.out.println(mockLogin.getId());

        // Perform a POST request to the login endpoint with valid credentials and check the response.
        mockMvc.perform(post("/residents/login")
                .contentType("application/json")
                .content("{\"email\":\"validUsername\",\"password\":\"validPassword\"}"))
                // Expect HTTP OK (200) status for a successful login.
                .andExpect(status().isOk())
                // Verify that the message in the response is "Login Successful".
                .andExpect(jsonPath("$.message").value("Login Successful"))
                // Verify that the status code in the response body is 204 (No Content).
                .andExpect(jsonPath("$.status").value(204))
                // Verify that the returned user ID matches the mock ID.
                .andExpect(jsonPath("$.data.id").value(mockLogin.getId().toString()))
                // Verify that the 'verified' status is false in the response.
                .andExpect(jsonPath("$.data.verified").value(false));
    }

    // Simulates a bad request (request body is empty).
    // Checks the response body for the correct message and status code.
    @Test
    public void testLoginBadRequest() throws Exception {
        // Simulate validation errors by returning true when errors.hasErrors() is called.
        // This simulates a case where the request body is missing required fields or is malformed.
        when(errors.hasErrors()).thenReturn(true);

        // Perform a POST request with an empty request body and check the response.
        mockMvc.perform(post("/residents/login")
                .contentType("application/json")
                .content("{}"))
                // Expect FORBIDDEN (403) status for a bad request.
                .andExpect(status().isForbidden())
                // Verify that the message in the response is "Bad request".
                .andExpect(jsonPath("$.message").value("Bad request"));
    }

    // Simulates a request with invalid credentials.
    // Checks the response body for the correct message and status code.
    @Test
    public void testLoginInvalidCredentials() throws Exception {
        // Simulate the service throwing an exception when invalid credentials are provided.
        // This tests the scenario where the user enters incorrect login details.
        when(service.login(any(UserModel.class))).thenThrow(new IllegalArgumentException("Invalid credentials"));

        // Perform a POST request with invalid credentials and check the response.
        mockMvc.perform(post("/residents/login")
                .contentType("application/json")
                .content("{\"email\":\"invalid\",\"password\":\"wrongPassword\"}"))
                // Expect BAD REQUEST (400) status due to invalid credentials.
                .andExpect(status().isBadRequest())
                // Verify that the response message indicates "Forbidden. Invalid credentials."
                .andExpect(jsonPath("$.message").value("Forbidden. Invalid credentials."));
    }
    
    // Test case for handling an internal server error.
    // This test simulates an unexpected server error during an API call.
    @Test
    public void testHandleErrorResponse_InternalServerError() {
        // Simulate an internal server error by throwing a RuntimeException.
        Exception exception = new RuntimeException("Internal service error");

        // Call the controller's error handler method to simulate error response.
        ResponseEntity<?> response = new ResidentController().handleErrorResponse(exception);

        // Verify that the response status is INTERNAL SERVER ERROR (500).
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        // Verify that the response message matches the expected error message.
        assertEquals("Internal Service Error. The server was unable to complete your request. Please try again later.",
                     ((ResponseModel<String>) response.getBody()).getMessage());
    }

    // Test case for handling a bad request error.
    // This test simulates a bad request error and checks the corresponding response.
    @Test
    public void testHandleErrorResponse_BadRequest() {
        // Simulate a BadRequestException by throwing an IllegalArgumentException.
        Exception exception = new IllegalArgumentException("Bad request");

        // Call the controller's error handler method to simulate error response.
        ResponseEntity<?> response = new ResidentController().handleErrorResponse(exception);

        // Verify that the response status is FORBIDDEN (403) as defined for bad requests.
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        // Verify that the response message matches the expected "Bad request" message.
        assertEquals("Bad request", ((ResponseModel<String>) response.getBody()).getMessage());
    }
}
