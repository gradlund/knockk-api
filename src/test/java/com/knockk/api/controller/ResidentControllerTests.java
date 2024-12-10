package com.knockk.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knockk.api.business.ResidentBusinessService;
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

	//Simulates HTTP requests
    @Autowired
    private MockMvc mockMvc;

    //Creates a mock bean and injects into the controller
    @MockBean
    private ResidentBusinessService service;

    private ObjectMapper objectMapper;

    @Mock
    private Errors errors;

    private UserModel validUserModel;

    @BeforeEach
    public void setUp() {
        // Setup a valid user model
        validUserModel = new UserModel("validUsername", "validPassword");
    }

    //Simulates a successful login
    //Checks the response body for the correct message and id
    @Test
    public void testLoginSuccess() throws Exception {
        // Mock the successful login response (service returns a valid UUID)
        UUID mockId = UUID.randomUUID();
        when(service.login(any(UserModel.class))).thenReturn(mockId);

        mockMvc.perform(post("/residents/login")
                .contentType("application/json")
                .content("{\"email\":\"validUsername\",\"password\":\"validPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login Successful"))
                .andExpect(jsonPath("$.data.Id").value(mockId.toString()));
    }

    //Simulates a bad request (request body is empty)
    //Checks the response body for the correct message and status code
    @Test
    public void testLoginBadRequest() throws Exception {
        // Simulate validation errors
        when(errors.hasErrors()).thenReturn(true);

        mockMvc.perform(post("/residents/login")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Bad request"));
    }

    //Simulates a request with invalid credentials
    //Checks the response body for the correct message and status code
    @Test
    public void testLoginInvalidCredentials() throws Exception {
        // Simulate service throwing an exception for invalid credentials
        when(service.login(any(UserModel.class))).thenThrow(new IllegalArgumentException("Invalid credentials"));

        mockMvc.perform(post("/residents/login")
                .contentType("application/json")
                .content("{\"email\":\"invalid\",\"password\":\"wrongPassword\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Forbidden. Invalid credentials."));
    }

    //Simulates an API call that has an internal server error
    //Checks the response body for the correct message and status code
    @Test
    public void testHandleErrorResponse_InternalServerError() {
        // Simulate a generic exception being thrown
        Exception exception = new RuntimeException("Internal service error");
        ResponseEntity<?> response = new ResidentController().handleErrorResponse(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Service Error. The server was unable to complete your request. Please try again later.",
                     ((ResponseModel<String>) response.getBody()).getMessage());
    }

    //Simulates an API call that has a bad request
    //Checks the response body for the correct message and status code
    @Test
    public void testHandleErrorResponse_BadRequest() {
        // Simulate a BadRequestException
        Exception exception = new IllegalArgumentException("Bad request");
        ResponseEntity<?> response = new ResidentController().handleErrorResponse(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Bad request", ((ResponseModel<String>) response.getBody()).getMessage());
    }

}
