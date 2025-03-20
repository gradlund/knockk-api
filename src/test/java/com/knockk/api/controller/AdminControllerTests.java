// Grace Radlund
// 12-15-2024
// Tests generated with the help of ChatGPT 4o mini
package com.knockk.api.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.Errors;

import com.knockk.api.business.AdminBusinessService;
import com.knockk.api.model.AdminModel;
import com.knockk.api.model.AdminResidentModel;
import com.knockk.api.model.BuildingModel;
import com.knockk.api.model.ResponseModel;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@WebMvcTest(AdminController.class)
public class AdminControllerTests {

    // Inject the MockMvc instance to simulate HTTP requests to the controller.
    @Autowired
    private MockMvc mockMvc;

    // // Mock the AdminBusinessService, which is used by the controller.
    @MockBean
    private AdminBusinessService service;

    // TODOTODO or
    // Mock the AdminBusinessService dependency
    // @Mock
    // private AdminBusinessService service;

    // Inject the mock service into the controller
    @InjectMocks
    private AdminController adminController;

    // Mock the Errors object to simulate validation errors during the login
    // process.
    @Mock
    private Errors errors;

    // Global variables for reuse across tests
    private UUID adminId;
    private BuildingModel building1;
    private BuildingModel building2;
    private List<BuildingModel> buildingModels;
    private Pageable pageable;
    private AdminResidentModel resident1;
    private AdminResidentModel resident2;
    private List<AdminResidentModel> residentEntities;
    private List<AdminResidentModel> residentModels;
    private AdminModel validAdminModel;

    @BeforeEach
    public void setUp() {
        // Initialize mocks before each test method.
        MockitoAnnotations.openMocks(this);

        // Initialize the validAdminModel with sample valid credentials before each
        // test.
        adminId = UUID.randomUUID();
        building1 = new BuildingModel("Building A", UUID.randomUUID());
        building2 = new BuildingModel("Building B", UUID.randomUUID());
        buildingModels = Arrays.asList(building1, building2);
        pageable = PageRequest.of(0, 10, Sort.by("lastName").ascending());
        resident1 = new AdminResidentModel(building1.getId(), UUID.randomUUID(), "John", "Doe", "johndoe@gmail.com", 5,
                25, new Date(11 / 11 / 2025), new Date(11 / 11 / 2205));
        resident2 = new AdminResidentModel(building1.getId(), UUID.randomUUID(), "John", "Deer", "johndeer@gmail.com",
                3, 23, new Date(11 / 11 / 2025), new Date(11 / 11 / 2205));
        residentEntities = List.of(resident1, resident2);
        validAdminModel = new AdminModel("validUsername", "validPassword");
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
    void testGetBuildings_Success() throws Exception {
        // Arrange: Mock the service to return the global buildingModels
        when(service.getBuildings(adminId)).thenReturn(buildingModels);

        // Act: Invoke the controller method with the adminId as a string
        ResponseEntity<?> responseEntity = adminController.getBuildings(adminId.toString());

        // Assert: Verify the response
        assertNotNull(responseEntity, "Success");
        // assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Success");

        // Cast the body to ResponseModel and verify its contents
        ResponseModel<List<BuildingModel>> response = (ResponseModel<List<BuildingModel>>) responseEntity.getBody();
        assertNotNull(response, "Response body should not be null");
        assertEquals("Success", response.getMessage(), "Message should be 'Success'");
        assertEquals(200, response.getStatus(), "Status code should be 200");
        assertEquals(buildingModels, response.getData(), "Building list should match the mocked data");

        // Verify service interaction
        verify(service, times(1)).getBuildings(adminId);
        verifyNoMoreInteractions(service);
    }

    // Test error handling when service throws an exception
    @Test
    void testGetBuildings_ServiceThrowsException() throws Exception {
        // Arrange: Mock the service to throw an exception
        Exception expectedException = new Exception("Not Found. No buildings found.");
        when(service.getBuildings(adminId)).thenThrow(expectedException);

        // Act: Invoke the controller method with the adminId as a string
        ResponseEntity<?> responseEntity = adminController.getBuildings(adminId.toString());

        // Assert: Verify the error response (assuming handleErrorResponse behavior)
        assertNotNull(responseEntity, "ResponseEntity should not be null");
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode(),
                "Not Found. No buildings found.");

        // Cast the body to ResponseModel and verify its contents (assumed behavior)
        ResponseModel<?> response = (ResponseModel<?>) responseEntity.getBody();
        assertNotNull(response, "Response body should not be null");
        assertEquals("Not Found", response.getMessage(),
                "Message should match the exception");
        assertEquals(404, response.getStatus(), "Status code should be 404");

        // Verify service interaction
        verify(service, times(1)).getBuildings(adminId);
        verifyNoMoreInteractions(service);
    }

    // Test successful retrieval of residents
    @Test
    void testGetResidents_Success() throws Exception {
        // Arrange: Mock the service to return the global residentModels
        when(service.getResidents(building1.getId(), false, pageable)).thenReturn(residentModels);

        // Act: Invoke the controller method with the buildingId as a string
        ResponseEntity<?> responseEntity = adminController.getResidents(building1.getId().toString(), false, pageable);

        // Assert: Verify the response
        assertNotNull(responseEntity, "ResponseEntity should not be null");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Status should be OK (200)");

        // Cast the body to ResponseModel and verify its contents
        ResponseModel<List<AdminResidentModel>> response = (ResponseModel<List<AdminResidentModel>>) responseEntity
                .getBody();
        assertNotNull(response, "Response body should not be null");
        assertEquals("Success", response.getMessage(), "Message should be 'Success'");
        assertEquals(200, response.getStatus(), "Status code should be 200");
        assertEquals(residentModels, response.getData(), "Resident list should match the mocked data");

        // Verify service interaction
        verify(service, times(1)).getResidents(building1.getId(), false, pageable);
        verifyNoMoreInteractions(service);
    }

    // Test error handling when service throws an exception
    @Test
    void testGetResidents_ServiceThrowsException() throws Exception {
        // Arrange: Mock the service to throw an exception
        Exception expectedException = new Exception("Not found. No residents found.");
        when(service.getResidents(building1.getId(), true, pageable)).thenThrow(expectedException);

        // Act: Invoke the controller method
        ResponseEntity<?> responseEntity = adminController.getResidents(building1.getId().toString(), true, pageable);

        // Assert: Verify the error response
        assertNotNull(responseEntity, "ResponseEntity should not be null");
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode(),
                "Status should be NOT_FOUND (404)");

        // Cast the body to ResponseModel and verify its contents
        ResponseModel<?> response = (ResponseModel<?>) responseEntity.getBody();
        assertNotNull(response, "Response body should not be null");
        assertEquals("Not Found", response.getMessage(),
                "Message should match the exception");
        assertEquals(404, response.getStatus(), "Status code should be 404");

        // Verify service interaction
        verify(service, times(1)).getResidents(building1.getId(), true, pageable);
        verifyNoMoreInteractions(service);
    }

    // Test with invalid buildingId format
    @Test
    void testGetResidents_InvalidBuildingIdFormat() {
        // Arrange: Pass an invalid UUID string
        String invalidId = "not-a-uuid";

        // Act: Invoke the controller method with an invalid ID
        ResponseEntity<?> responseEntity = adminController.getResidents(invalidId, false, pageable);

        // Assert: Verify the error response (UUID parsing fails)
        assertNotNull(responseEntity, "ResponseEntity should not be null");
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode(),
                "Status should be FORBIDDEN (400)");

        ResponseModel<?> response = (ResponseModel<?>) responseEntity.getBody();
        assertNotNull(response, "Response body should not be null");
        assertTrue(response.getMessage().contains("UUID"), "Message should indicate UUID parsing failure");
        assertEquals(400, response.getStatus(), "Status code should be 400");

        // Verify no service interaction (exception thrown before service call)
        verifyNoInteractions(service);
    }

}
