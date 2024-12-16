// Grace Radlund
// 12-15-2024
// Tests generated with the help of ChatGPT 4o mini
package com.knockk.api.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.Errors;

import com.knockk.api.business.AdminBusinessService;
import com.knockk.api.model.AdminModel;
import com.knockk.api.model.ResponseModel;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(AdminController.class)
public class AdminControllerTests {

	// Inject the MockMvc instance to simulate HTTP requests to the controller.
    @Autowired
    private MockMvc mockMvc;

    // Mock the AdminBusinessService, which is used by the controller.
    @MockBean
    private AdminBusinessService service;

    // Mock the Errors object to simulate validation errors during the login process.
    @Mock
    private Errors errors;

    // Declare a valid AdminModel to use in tests.
    private AdminModel validAdminModel;

    @BeforeEach
    public void setUp() {
    	// Initialize the validAdminModel with sample valid credentials before each test.
        validAdminModel = new AdminModel("validUsername", "validPassword");
    }
    
    // Test case for successful login
    @Test
    public void testLoginSuccess() throws Exception {
    	// Simulate a successful login by returning a mock UUID when the service login method is called.
        UUID mockId = UUID.randomUUID();
        when(service.login(any(AdminModel.class))).thenReturn(mockId);

        // Perform a POST request to the login endpoint with valid credentials and check the response.
        mockMvc.perform(post("/admin/login")
                .contentType("application/json")
                .content("{\"username\":\"validUsername\",\"password\":\"validPassword\"}"))
                // Verify that the response status is HTTP OK (200).
                .andExpect(status().isOk())
                // Verify the response message is "Login Successful".
                .andExpect(jsonPath("$.message").value("Login Successful"))
                // Verify that the response contains the correct mock UUID in the "Id" field.
                .andExpect(jsonPath("$.data.Id").value(mockId.toString()));
    }

    // Test case for bad request scenario
    @Test
    public void testLoginBadRequest() throws Exception {
        // Simulate validation errors by making the Errors object return true when checked for errors.
        // This simulates a situation where the input data is invalid or incomplete.
        when(errors.hasErrors()).thenReturn(true);

        // Perform a POST request with invalid/empty JSON content and check the response.
        mockMvc.perform(post("/admin/login")
                .contentType("application/json")
                .content("{}"))
                // Expect a FORBIDDEN (403) status as the request is considered invalid.
                .andExpect(status().isForbidden())
                // Verify that the response message is "Bad request".
                .andExpect(jsonPath("$.message").value("Bad request"));
    }

    // Test case for invalid credential scenario
    @Test
    public void testLoginInvalidCredentials() throws Exception {
        // Simulate the service throwing an exception when invalid credentials are provided.
        // This mocks the behavior of the AdminBusinessService when the credentials are incorrect.
        when(service.login(any(AdminModel.class))).thenThrow(new IllegalArgumentException("Invalid credentials"));

        // Perform a POST request with invalid credentials and check the response.
        mockMvc.perform(post("/admin/login")
                .contentType("application/json")
                .content("{\"username\":\"invalid\",\"password\":\"wrongPassword\"}"))
                // Expect a BAD REQUEST (400) status due to invalid credentials.
                .andExpect(status().isBadRequest())
                // Verify the response message is "Forbidden. Invalid credentials."
                .andExpect(jsonPath("$.message").value("Forbidden. Invalid credentials."));
    }

    // Test for internal server error scenario
    @Test
    public void testHandleErrorResponse_InternalServerError() {
        // Simulate a generic internal server error by throwing a RuntimeException.
        // This tests the controller's ability to handle unhandled exceptions.
        Exception exception = new RuntimeException("Internal service error");
        
        // Call the handleErrorResponse method of the AdminController to simulate error handling.
        ResponseEntity<?> response = new AdminController().handleErrorResponse(exception);

        // Verify that the response status is INTERNAL SERVER ERROR (500).
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        // Verify that the error message in the response is correct for a server-side error.
        assertEquals("Internal Service Error. The server was unable to complete your request. Please try again later.", 
                     ((ResponseModel<String>) response.getBody()).getMessage());
    }

    // Test for bad request exception
    @Test
    public void testHandleErrorResponse_BadRequest() {
        // Simulate a BadRequestException by throwing an IllegalArgumentException.
        // This tests the controller's ability to handle client-side errors.
        Exception exception = new IllegalArgumentException("Bad request");

        // Call the handleErrorResponse method to simulate error handling for bad requests.
        ResponseEntity<?> response = new AdminController().handleErrorResponse(exception);

        // Verify that the response status is FORBIDDEN (403) as defined for bad requests.
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        // Verify that the error message in the response matches the expected "Bad request" message.
        assertEquals("Bad request", ((ResponseModel<String>) response.getBody()).getMessage());
    }
}
