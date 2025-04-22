// Grace Radlund
// 4-22-2024
// Tests generated with the help of ChatGPT 4o mini and Grok
package com.knockk.api.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.Errors;

import com.knockk.api.business.AdminBusinessService;
import com.knockk.api.util.model.AdminModel;
import com.knockk.api.util.model.AdminResidentModel;
import com.knockk.api.util.model.BuildingModel;
import com.knockk.api.util.model.ResponseModel;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Class for testing the admin controller
 */
@WebMvcTest(AdminController.class)
public class AdminControllerTests {

    // MockMvc instance to simulate HTTP requests to the controller.
    @Autowired
    private MockMvc mockMvc;

    // Mock the AdminBusinessService, which is used by the controller.
    @MockBean
    private AdminBusinessService service;

    // Mock the Errors object to simulate validation errors during the login
    // process.
    @MockBean
    private Errors errors;

    // Global variables for reuse across tests
    private UUID adminId;
    private UUID buildingId;
    private BuildingModel building1;
    private BuildingModel building2;
    private List<BuildingModel> buildingModels;
    private Pageable pageable;
    private UUID residentId;
    private AdminResidentModel resident1;
    private AdminResidentModel resident2;
    private List<AdminResidentModel> residentModels;

    @BeforeEach
    public void setUp() {
        // Initialize mocks before each test method.
        MockitoAnnotations.openMocks(this);

        // Initialize the validAdminModel with sample valid credentials before each
        // test.
        adminId = UUID.randomUUID();
        buildingId = UUID.randomUUID();
        building1 = new BuildingModel("Building A", UUID.randomUUID());
        building2 = new BuildingModel("Building B", UUID.randomUUID());
        buildingModels = Arrays.asList(building1, building2);
        pageable = PageRequest.of(0, 10, Sort.by("lastName").ascending());
        residentId = UUID.randomUUID();
        resident1 = new AdminResidentModel(building1.getId(), UUID.randomUUID(), "John", "Doe", "johndoe@gmail.com", 5,
                25, new Date(11 / 11 / 2025), new Date(11 / 11 / 2205));
        resident2 = new AdminResidentModel(building1.getId(), UUID.randomUUID(), "John", "Deer", "johndeer@gmail.com",
                3, 23, new Date(11 / 11 / 2025), new Date(11 / 11 / 2205));
        residentModels = List.of(resident1, resident2);
    }

    // Test case for successful login
    @Test
    public void testLoginSuccess() throws Exception {
        // Simulate a successful login by returning a mock UUID when the service login
        // method is called.
        UUID mockId = UUID.randomUUID();
        when(service.login(any(AdminModel.class))).thenReturn(mockId);

        // Perform a POST request to the login endpoint with valid credentials and check
        // the response.
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
        // Simulate validation errors by making the Errors object return true when
        // checked for errors.
        // This simulates a situation where the input data is invalid or incomplete.
        when(errors.hasErrors()).thenReturn(true);

        // Perform a POST request with invalid/empty JSON content and check the
        // response.
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
        // Simulate the service throwing an exception when invalid credentials are
        // provided.
        // This mocks the behavior of the AdminBusinessService when the credentials are
        // incorrect.
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

        // Call the handleErrorResponse method of the AdminController to simulate error
        // handling.
        ResponseEntity<?> response = new AdminController().handleErrorResponse(exception);

        // Verify that the response status is INTERNAL SERVER ERROR (500).
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        // Verify that the error message in the response is correct for a server-side
        // error.
        assertEquals("Internal Service Error. The server was unable to complete your request. Please try again later.",
                ((ResponseModel<String>) response.getBody()).getMessage());
    }

    // Test for bad request exception
    @Test
    public void testHandleErrorResponse_BadRequest() {
        // Simulate a BadRequestException by throwing an IllegalArgumentException.
        // This tests the controller's ability to handle client-side errors.
        Exception exception = new IllegalArgumentException("Bad request");

        // Call the handleErrorResponse method to simulate error handling for bad
        // requests.
        ResponseEntity<?> response = new AdminController().handleErrorResponse(exception);

        // Verify that the response status is FORBIDDEN (403) as defined for bad
        // requests.
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        // Verify that the error message in the response matches the expected "Bad
        // request" message.
        assertEquals("Bad request", ((ResponseModel<String>) response.getBody()).getMessage());
    }

    // Test successful retrieval of buildings
    @Test
    public void testGetBuildingsSuccess() throws Exception {
        // Arrange
        when(service.getBuildings(eq(adminId))).thenReturn(buildingModels);

        // Act & Assert
        mockMvc.perform(get("/admin/buildings/{adminId}", adminId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data[0].name").value("Building A"))
                .andExpect(jsonPath("$.data[1].name").value("Building B"));
    }

    /**
     * Tests the getBuildings endpoint when service.getBuildings throws an
     * exception.
     * Verifies that the catch block is triggered and handleErrorResponse returns an
     * error response.
     */
    @Test
    public void testGetBuildingsThrowsException() throws Exception {
        // Arrange: Set up the test to simulate an exception.
        // Mock service.getBuildings to throw a RuntimeException when called with
        // adminId.
        when(service.getBuildings(eq(adminId)))
                .thenThrow(new Exception("Not Found. No buildings found."));

        // Act & Assert: Send a GET request and verify the error response.
        mockMvc.perform(get("/admin/buildings/{adminId}", adminId.toString())
                // Set content type to JSON, though not strictly needed for GET.
                .contentType(MediaType.APPLICATION_JSON))
                // Expect HTTP status 404 (Not Found)
                .andExpect(status().isNotFound())
                // Verify the response has an error message.
                // Adjust the message based on what handleErrorResponse returns.
                .andExpect(jsonPath("$.message").value("Not Found"))
                // Verify the status field in ResponseModel, assuming 400.
                .andExpect(jsonPath("$.status").value(404));
    }

    /**
     * Tests the getResidents endpoint for the success case.
     * Simulates a valid buildingId and verified=false, expecting a list of
     * residents.
     */
    @Test
    public void testGetResidentsSuccess() throws Exception {
        // Arrange: Set up the mock service to return residents.
        // Mock service.getResidents to return the residents list when called with
        // buildingId, verified=false, and pageable.
        when(service.getResidents(eq(buildingId), eq(false), eq(pageable)))
                .thenReturn(residentModels);

        // Act & Assert: Send a GET request and verify the success response.
        mockMvc.perform(get("/admin/{buildingId}/residents", buildingId.toString())
                // Set query parameters: verified=false, page=0, size=10, sort=lastName,asc.
                .param("verified", "false")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "lastName,asc")
                // Set content type to JSON, though not strictly needed for GET.
                .contentType(MediaType.APPLICATION_JSON))
                // Expect HTTP status 200 OK.
                .andExpect(status().isOk())
                // Verify ResponseModel fields: message, status, and resident data.
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.status").value(200))
                // Verify the first resident’s fields.
                .andExpect(jsonPath("$.data[0].firstName").value("John"))
                .andExpect(jsonPath("$.data[0].lastName").value("Doe"))
                .andExpect(jsonPath("$.data[0].email").value("johndoe@gmail.com"))
                // Verify the second resident’s fields.
                .andExpect(jsonPath("$.data[1].firstName").value("John"))
                .andExpect(jsonPath("$.data[1].lastName").value("Deer"))
                .andExpect(jsonPath("$.data[1].email").value("johndeer@gmail.com"));
    }

    /**
     * Tests the getResidents endpoint when service.getResidents throws an
     * exception.
     * Verifies that the catch block is triggered and handleErrorResponse returns an
     * error response.
     */
    @Test
    public void testGetResidentsThrowsException() throws Exception {
        // Arrange: Set up the mock service to throw an exception.
        // Mock service.getResidents to throw a Exception when called.
        when(service.getResidents(eq(buildingId), eq(false), eq(pageable)))
                .thenThrow(new Exception("Not found. No residents found."));

        // Act & Assert: Send a GET request and verify the error response.
        mockMvc.perform(get("/admin/{buildingId}/residents", buildingId.toString())
                // Set query parameters: verified=false, page=0, size=10, sort=lastName,asc.
                .param("verified", "false")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "lastName,asc")
                // Set content type to JSON, though not strictly needed for GET.
                .contentType(MediaType.APPLICATION_JSON))
                // Expect HTTP status 400 Bad Request, assuming handleErrorResponse returns
                // this.
                .andExpect(status().isNotFound())
                // Verify ResponseModel fields: message and status.
                // Adjust message based on handleErrorResponse output.
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    /**
     * Tests the getPageInfo endpoint for the success case.
     * Simulates a valid buildingId and areVerified=false, expecting the number of
     * residents.
     */
    @Test
    public void testGetPageInfoSuccess() throws Exception {
        // Arrange: Set up the mock service to return the number of residents.
        // Define the expected number of residents.
        int numOfResidents = 5;
        // Mock service.getNumberOfResidents to return numOfResidents when called with
        // buildingId and areVerified=false.
        when(service.getNumberOfResidents(eq(buildingId), eq(false)))
                .thenReturn(numOfResidents);

        // Act & Assert: Send a GET request and verify the success response.
        mockMvc.perform(get("/admin/{buildingId}/", buildingId.toString())
                // Set query parameter: areVerified=false.
                .param("areVerified", "false")
                // Set content type to JSON, though not strictly needed for GET.
                .contentType(MediaType.APPLICATION_JSON))
                // Expect HTTP status 200 OK.
                .andExpect(status().isOk())
                // Verify ResponseModel fields: message, status, and data.
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").value(numOfResidents));
    }

    /**
     * Tests the getResident endpoint for the success case.
     * Simulates a valid residentId, expecting the resident’s details.
     */
    @Test
    public void testGetResidentSuccess() throws Exception {
        // Arrange: Set up the mock service to return a resident.
        // Mock service.getResident to return the resident when called with residentId.
        when(service.getResident(eq(residentId)))
                .thenReturn(resident1);

        // Act & Assert: Send a GET request and verify the success response.
        mockMvc.perform(get("/admin/{residentId}", residentId.toString())
                // Set content type to JSON, though not strictly needed for GET.
                .contentType(MediaType.APPLICATION_JSON))
                // Expect HTTP status 200 OK.
                .andExpect(status().isOk())
                // Verify ResponseModel fields: message, status, and resident data.
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.status").value(200))
                // Verify resident fields in the data object.
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"))
                .andExpect(jsonPath("$.data.email").value("johndoe@gmail.com"));
    }

    /**
     * Tests the getResident endpoint when service.getResident throws an exception.
     * Verifies that the catch block is triggered and handleErrorResponse returns an
     * error response.
     */
    @Test
    public void testGetResidentThrowsException() throws Exception {
        // Arrange: Set up the mock service to throw an exception.
        // Mock service.getResident to throw a Exception when called.
        when(service.getResident(eq(residentId)))
                .thenThrow(new Exception("Resident not found"));

        // Act & Assert: Send a GET request and verify the error response.
        mockMvc.perform(get("/admin/{residentId}", residentId.toString())
                // Set content type to JSON, though not strictly needed for GET.
                .contentType(MediaType.APPLICATION_JSON))
                // Expect HTTP status 404 Not Found, assuming handleErrorResponse returns this.
                .andExpect(status().isNotFound())
                // Verify ResponseModel fields: message and status.
                // Adjust message based on handleErrorResponse output.
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    /**
     * Tests the deleteResident endpoint for the success case.
     * Simulates a valid residentId, expecting a successful deletion response.
     * Assumes the response body is returned despite HttpStatus.NO_CONTENT (204).
     */
    @Test
    public void testDeleteResidentSuccess() throws Exception {
        // Arrange: Set up the mock service to return true for successful deletion.
        // Mock service.deleteResident to return true when called with residentId.
        when(service.deleteResident(eq(residentId)))
                .thenReturn(true);

        // Act & Assert: Send a DELETE request and verify the success response.
        mockMvc.perform(delete("/admin/residents/{residentId}", residentId.toString())
                // Set content type to JSON, though not strictly needed for DELETE.
                .contentType(MediaType.APPLICATION_JSON))
                // Expect HTTP status 204 No Content.
                .andExpect(status().isNoContent())
                // Verify ResponseModel fields: message, status, and data.
                // Note: If 204 truly omits the body, remove these assertions and check
                // content().string("").
                .andExpect(jsonPath("$.message").value("Account Creation Successful"))
                .andExpect(jsonPath("$.status").value(204))
                .andExpect(jsonPath("$.data").value(true));
    }

    /**
     * Tests the deleteResident endpoint when service.deleteResident throws an
     * exception.
     * Verifies that the catch block is triggered and handleErrorResponse returns an
     * error response.
     */
    @Test
    public void testDeleteResidentThrowsException() throws Exception {
        // Arrange: Set up the mock service to throw an exception.
        // Mock service.deleteResident to throw a Exception when called.
        when(service.deleteResident(eq(residentId)))
                .thenThrow(new Exception("Error deleting resident. Resident id is null.")); // id should be null

        // Act & Assert: Send a DELETE request and verify the error response.
        mockMvc.perform(delete("/admin/residents/{residentId}", residentId)
                // Set content type to JSON, though not strictly needed for DELETE.
                .contentType(MediaType.APPLICATION_JSON))
                // Expect HTTP status 404 Not Found, assuming handleErrorResponse returns this.
                .andExpect(status().isNotFound())
                // Verify ResponseModel fields: message and status.
                // Adjust message based on handleErrorResponse output.
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

}
