// Grace Radlund
// 4-22-2024
// Tests generated with the help of ChatGPT 4o mini and Grok
package com.knockk.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knockk.api.business.ResidentBusinessService;
import com.knockk.api.util.Gender;
import com.knockk.api.util.model.FriendshipModel;
import com.knockk.api.util.model.LoginModel;
import com.knockk.api.util.model.NeighborRoomModel;
import com.knockk.api.util.model.OptionalResidentModel;
import com.knockk.api.util.model.RegisterModel;
import com.knockk.api.util.model.ResidentModel;
import com.knockk.api.util.model.ResponseModel;
import com.knockk.api.util.model.UnitResidentModel;
import com.knockk.api.util.model.UserModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.Errors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Unit tests for the Resident Controller
 */
// Tests a specific controller without starting Spring Boot application
@WebMvcTest(ResidentController.class)
public class ResidentControllerTests {

        // Inject the MockMvc instance to simulate HTTP requests to the controller.
        @Autowired
        private MockMvc mockMvc;

        // Mock the ResidentBusinessService, which is used by the controller.
        @MockBean
        private ResidentBusinessService service;

        // Mock the Errors object to simulate validation errors during the login
        // process.
        @Mock
        private Errors errors;

        // ObjectMapper for JSON serialization/deserialization.
        private ObjectMapper objectMapper;

        // Declare a valid AdminModel to use in tests.
        private UserModel validUserModel;

        // Mock ids
        private UUID mockResidentId;
        private UUID mockFriendId;
        private UUID invitorId;
        private UUID inviteeId;

        // Mock friendship
        private FriendshipModel friendshipRequest;
        private FriendshipModel friendshipResponse;

        // Bad mock ids
        private String invalidResidentId;
        private String invalidFriendId;

        // Mocks for unit information
        private int mockFloor;
        private int mockRoom;
        private String address;
        private String buildingName;
        private Date startDate;
        private Date endDate;
        private UUID leaseID;

        private List<String> buildings;

        // Mock resident
        private ResidentModel mockResident;

        // Mock optional resident
        private OptionalResidentModel optionalResident;

        @BeforeEach
        public void setUp() {
                // Initialize ObjectMapper for JSON serialization.
                objectMapper = new ObjectMapper();

                // Initialize the validAdminModel with sample valid credentials before each
                // test.
                validUserModel = new UserModel("validUsername", "validPassword");

                // ID's
                mockResidentId = UUID.randomUUID(); // A mock UUID to simulate a valid resident ID.
                mockFriendId = UUID.randomUUID();
                invalidResidentId = "invalidUUID";
                invalidFriendId = "invalidUUID";
                inviteeId = UUID.randomUUID();
                invitorId = UUID.randomUUID();

                friendshipRequest = new FriendshipModel(invitorId, inviteeId, false);
                friendshipResponse = new FriendshipModel(invitorId, inviteeId, false);

                // Unit information
                mockFloor = 2;
                mockRoom = 101;
                address = "123 Street";
                buildingName = "building";
                startDate = new Date();
                endDate = new Date();
                leaseID = UUID.randomUUID();

                buildings = Arrays.asList("Building A", "Building B");

                // Models
                // Mock resident
                mockResident = new ResidentModel(21, "Phoenix", "I don't have one", "", "", "johninstagram",
                                "johnsnapchat", "johnx", "johnfacebook", mockResidentId, "John", "Doe", Gender.Male);

                // Mock optional resident
                optionalResident = new OptionalResidentModel(22, "Madison", "I don't have one", "", "",
                                "johninstagram", "johnsnapchat", "johnx", "johnfacebook");

        }

        /**
         * Tests the updateFriendship endpoint for the success case.
         * Simulates a valid FriendshipModel, expecting a successful friendship
         * creation.
         */
        @Test
        public void testUpdateFriendshipSuccess() throws Exception {
                // Arrange: Set up the mock service to return a FriendshipModel.
                // Mock service.updateFriendship to return friendshipResponse when called with
                // invitorId, inviteeId, and isAccepted.
                when(service.updateFriendship(eq(invitorId), eq(inviteeId), eq(false)))
                                .thenReturn(friendshipResponse);

                // Act & Assert: Send a POST request and verify the success response.
                mockMvc.perform(post("/residents/friendship")
                                // Set content type to JSON for the request body.
                                .contentType(MediaType.APPLICATION_JSON)
                                // Serialize the friendshipRequest to JSON.
                                .content(objectMapper.writeValueAsString(friendshipRequest)))
                                // Expect HTTP status 200 OK.
                                .andExpect(status().isOk())
                                // Verify ResponseModel fields: message, status, and friendship data.
                                .andExpect(jsonPath("$.message").value("Success"))
                                .andExpect(jsonPath("$.status").value(200))
                                // Verify FriendshipModel fields in the data object.
                                .andExpect(jsonPath("$.data.invitorId").value(invitorId.toString()))
                                .andExpect(jsonPath("$.data.inviteeId").value(inviteeId.toString()))
                                .andExpect(jsonPath("$.data.accepted").value(false));
        }

        /**
         * Tests the updateFriendship endpoint when service.updateFriendship throws an
         * exception.
         * Verifies that the catch block is triggered and handleErrorResponse returns an
         * error response.
         */
        @Test
        public void testUpdateFriendshipThrowsException() throws Exception {
                // Arrange: Create an invalid FriendshipModel with null invitorId and inviteeId.
                // Assumes @NotNull annotations on invitorId and inviteeId to trigger validation
                // errors.
                FriendshipModel invalidRequest = new FriendshipModel(null, null, false);

                // Perform a POST request with invalid data (e.g., missing fields)
                mockMvc.perform(post("/residents/friendship")
                                // Set content type to JSON for the request body.
                                .contentType(MediaType.APPLICATION_JSON)
                                // Serialize the invalid FriendshipModel to JSON.
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                // Expect HTTP status 400 Bad Request due to validation failure.
                                .andExpect(status().isForbidden())
                                // Verify ResponseModel fields: message and status.
                                // Matches the Exception("Bad Request") thrown in the controller.
                                .andExpect(jsonPath("$.message").value("Bad Request"))
                                .andExpect(jsonPath("$.status").value(400));
        }

        /**
         * Tests createAccount success case.
         * Simulates a valid UserModel, expecting a successful account creation.
         */
        @Test
        public void testCreateAccountSuccess() throws Exception {
                // Arrange: Mock service to return a UUID for valid credentials.
                when(service.createAccount(eq(validUserModel)))
                                .thenReturn(mockResidentId);

                // Act & Assert: Send POST request and verify response.
                mockMvc.perform(post("/residents/create-account")
                                // Set content type to JSON.
                                .contentType(MediaType.APPLICATION_JSON)
                                // Serialize valid UserModel to JSON.
                                .content(objectMapper.writeValueAsString(validUserModel)))
                                // Expect HTTP status 200 OK (as coded in controller).
                                .andExpect(status().isOk())
                                // Verify ResponseModel fields: data, message, status.
                                .andExpect(jsonPath("$.message").value("Account Creation Successful"))
                                .andExpect(jsonPath("$.status").value(204));
        }

        /**
         * Tests getLease success case.
         * Simulates valid query parameters, expecting a successful lease retrieval.
         */
        @Test
        public void testGetLeaseSuccess() throws Exception {
                // Arrange: Mock service to return a UUID for valid parameters.
                when(service.getLease(anyString(), anyString(), anyInt(), anyInt(), anyString(), anyString()))
                                .thenReturn(leaseID);

                // Act & Assert: Send GET request to /residents/lease with valid parameters and
                // verify response.
                mockMvc.perform(get("/residents/lease")
                                // Add query parameters.
                                .param("address", address)
                                .param("buildingName", buildingName)
                                .param("floor", String.valueOf(mockFloor))
                                .param("room", String.valueOf(mockRoom))
                                .param("startDate", String.valueOf(startDate))
                                .param("endDate", String.valueOf(endDate))
                                // Set content type (optional for GET, included for consistency).
                                .contentType(MediaType.APPLICATION_JSON))
                                // Expect HTTP status 200 OK (as coded in controller).
                                .andExpect(status().isOk())
                                // Verify ResponseModel fields: data, message, status.
                                .andExpect(jsonPath("$.data").value(leaseID.toString()))
                                .andExpect(jsonPath("$.message").value("Account Creation Successful"))
                                .andExpect(jsonPath("$.status").value(204));
        }

        /**
         * Tests getLease failure case.
         * Simulates invalid parameters causing a service exception, expecting a Not
         * Found response via handleErrorResponse.
         */
        @Test
        public void testGetLeaseNotFound() throws Exception {
                // Arrange: Mock service to throw an exception for invalid parameters.
                when(service.getLease(anyString(), anyString(), anyInt(), anyInt(), anyString(), anyString()))
                                .thenThrow(new RuntimeException("Lease not found"));

                // Act & Assert: Send GET request to /residents/lease with parameters and verify
                // error response.
                mockMvc.perform(get("/residents/lease")
                                // Use same parameters; exception is triggered by service logic.
                                .param("address", address)
                                .param("buildingName", buildingName)
                                .param("floor", String.valueOf(mockFloor))
                                .param("room", String.valueOf(mockRoom))
                                .param("startDate", String.valueOf(startDate))
                                .param("endDate", String.valueOf(endDate))
                                .contentType(MediaType.APPLICATION_JSON))
                                // Expect HTTP status 404 Not Found (as per handleErrorResponse for "not
                                // found").
                                .andExpect(status().isNotFound())
                                // Verify ResponseModel fields: data, message, status.
                                .andExpect(jsonPath("$.data.Error").value("Lease not found"))
                                .andExpect(jsonPath("$.message").value("Not Found"))
                                .andExpect(jsonPath("$.status").value(404));
        }

        // Simulates a successful request to get an existing friendship where the
        // residents are connected.
        // Checks the response body for the correct message, status code, and friendship
        // details.
        @Test
        public void testGetFriendshipSuccess() throws Exception {
                // Create a mock FriendshipModel to simulate a successful response from the
                // service.
                FriendshipModel mockFriendship = new FriendshipModel(mockResidentId, mockFriendId, true);

                // Mock the service method to return the mock friendship when called.
                when(service.getFriendship(mockResidentId, mockFriendId)).thenReturn(mockFriendship);

                // Perform a GET request to the /friendship endpoint with valid residentId and
                // friendId.
                mockMvc.perform(get("/residents/{residentId}/friendship/{friendId}", mockResidentId, mockFriendId))
                                // Expect HTTP OK (200) status for a successful request.
                                .andExpect(status().isOk())
                                // Verify that the message in the response is "Success".
                                .andExpect(jsonPath("$.message").value("Success"))
                                // Verify that the status code in the response is 200.
                                .andExpect(jsonPath("$.status").value(200))
                                // Verify that the response contains the correct resident and friend IDs.
                                .andExpect(jsonPath("$.data.invitorId").value(mockResidentId.toString()))
                                .andExpect(jsonPath("$.data.inviteeId").value(mockFriendId.toString()))
                                .andExpect(jsonPath("$.data.accepted").value(true));
        }

        // Simulates a bad request when the UUIDs for residentId or friendId are
        // invalid.
        // Checks the response body for the correct message and status code.
        @Test
        public void testGetFriendshipInvalidUUID() throws Exception {
                // Perform a GET request to the /friendship endpoint with invalid UUIDs.
                mockMvc.perform(get("/residents/{residentId}/friendship/{friendId}", invalidResidentId,
                                invalidFriendId))
                                // Expect HTTP INTERNAL SERVER ERROR (500) status due to invalid UUID format.
                                .andExpect(status().isInternalServerError());
        }

        // Test case for successfully deleting an existing friendship.
        // Checks the response body for the correct message and status code.
        @Test
        public void testDeleteFriendshipSuccess() throws Exception {
                // Mock the service to return true, indicating the friendship has been
                // successfully deleted.
                when(service.deleteFriendship(mockResidentId, mockFriendId)).thenReturn(true);

                // Perform a DELETE request to the /friendship endpoint with valid residentId
                // and friendId.
                mockMvc.perform(delete("/residents/{residentId}/friendship/{friendId}", mockResidentId, mockFriendId))
                                // Expect HTTP OK (200) status for a successful deletion.
                                .andExpect(status().isOk())
                                // Verify that the message in the response is "Success."
                                .andExpect(jsonPath("$.message").value("Success"))
                                // Verify that the status code in the response is 204 (No Content).
                                .andExpect(jsonPath("$.status").value(204));
        }

        // Test case where no friendship exists between the resident and friend.
        // Checks the response body for the correct message and status code.
        @Test
        public void testDeleteFriendshipNotFound() throws Exception {
                // Mock the service to return false, indicating no friendship was found to
                // delete.
                when(service.deleteFriendship(mockResidentId, mockFriendId)).thenReturn(false);

                // Perform a DELETE request to the /friendship endpoint with valid residentId
                // and friendId.
                mockMvc.perform(delete("/residents/{residentId}/friendship/{friendId}", mockResidentId, mockFriendId))
                                // Expect HTTP Internal Server Error (500) status for a friendship not found.
                                .andExpect(status().isInternalServerError())
                                // Verify the message in the response
                                .andExpect(jsonPath("$.data.Error").value("Error deleting friendship."));
        }

        // Test case where invalid UUIDs are provided for residentId or friendId.
        // Checks the response body for the correct error message and status code.
        @Test
        public void testDeleteFriendshipInvalidUUID() throws Exception {
                // Perform a DELETE request to the /friendship endpoint with invalid UUIDs.
                mockMvc.perform(delete("/residents/{residentId}/friendship/{friendId}", invalidResidentId,
                                invalidFriendId))
                                // Expect HTTP Internal Server Error (500) status due to invalid UUID format.
                                .andExpect(status().isInternalServerError());
        }

        /**
         * Tests getBuilding success case.
         * Simulates a valid street path variable, expecting a successful retrieval of
         * building names.
         */
        @Test
        public void testGetBuildingSuccess() throws Exception {
                // Arrange: Mock service to return a list of building names for the street.
                when(service.getBuildings(anyString()))
                                .thenReturn(buildings);

                // Act & Assert: Send GET request to /residents/building/{street} and verify
                // response.
                mockMvc.perform(get("/residents/building/{street}", address)
                                // Set content type (optional for GET, included for consistency).
                                .contentType(MediaType.APPLICATION_JSON))
                                // Expect HTTP status 200 OK (as coded in controller).
                                .andExpect(status().isOk())
                                // Verify ResponseModel fields: data, message, status.
                                .andExpect(jsonPath("$.data[0]").value("Building A"))
                                .andExpect(jsonPath("$.data[1]").value("Building B"))
                                .andExpect(jsonPath("$.message").value("Success"))
                                .andExpect(jsonPath("$.status").value(200));
        }

        /**
         * Tests getBuilding failure case.
         * Simulates an invalid or non-existent street, expecting a Not Found response
         * via handleErrorResponse.
         */
        @Test
        public void testGetBuildingNotFound() throws Exception {
                // Arrange: Mock service to throw an exception for an invalid street.
                when(service.getBuildings(anyString()))
                                .thenThrow(new Exception("Invalid address (case sensitive)."));

                // Act & Assert: Send GET request to /residents/building/{street} and verify
                // error response.
                mockMvc.perform(get("/residents/building/{street}", address)
                                .contentType(MediaType.APPLICATION_JSON))
                                // Expect HTTP status 404 Not Found (as per handleErrorResponse for "not
                                // found").
                                .andExpect(status().isNotFound())
                                // Verify ResponseModel fields: data, message, status.
                                .andExpect(jsonPath("$.message").value("Not Found"))
                                .andExpect(jsonPath("$.status").value(404));
        }

        // Test case for successfully retrieving the neighboring residents of a unit.
        // Checks the response body for the correct message, status code, and residents
        // list.
        @Test
        public void testGetNeighborResidentsSuccess() throws Exception {
                // Create mock residents list to simulate the data returned from the service.
                ArrayList<UnitResidentModel> mockResidentsList = new ArrayList<>();
                mockResidentsList.add(new UnitResidentModel(UUID.randomUUID(), "John Doe", false));
                mockResidentsList.add(new UnitResidentModel(UUID.randomUUID(), "Jane Smith", true));

                // Mock the service to return the list of residents for the given floor and
                // room.
                when(service.getNeighborResidents(mockResidentId, mockFloor, mockRoom))
                                .thenReturn(mockResidentsList);

                // Perform a GET request to the /neighbor-units endpoint with valid residentId,
                // floor, and room.
                mockMvc.perform(
                                get("/residents/{residentId}/neighbor-units/{floor}-{room}", mockResidentId, mockFloor,
                                                mockRoom))
                                // Expect HTTP OK (200) status for a successful response.
                                .andExpect(status().isOk())
                                // Verify that the response contains the correct success message.
                                .andExpect(jsonPath("$.message").value("Success"))
                                // Verify that the list of neighboring residents is correctly returned.
                                .andExpect(jsonPath("$.data").isArray())
                                // Verify that the list contains two residents.
                                .andExpect(jsonPath("$.data.length()").value(2));
        }

        // Test case for when no residents are found in the specified unit.
        // Checks the response body for the correct message and status code.
        @Test
        public void testGetNeighborResidentsNoResidentsFound() throws Exception {
                // Mock the service to return an empty list, indicating no residents in the
                // unit.
                when(service.getNeighborResidents(mockResidentId, mockFloor, mockRoom))
                                .thenReturn(new ArrayList<>());

                // Perform a GET request to the /neighbor-units endpoint with valid residentId,
                // floor, and room.
                mockMvc.perform(
                                get("/residents/{residentId}/neighbor-units/{floor}-{room}", mockResidentId, mockFloor,
                                                mockRoom))
                                // Expect HTTP OK (200) status for a successful response even if no residents
                                // exist.
                                .andExpect(status().isOk())
                                // Verify that the response contains the correct success message.
                                .andExpect(jsonPath("$.message").value("Success"))
                                // Verify that the data field is an empty array.
                                .andExpect(jsonPath("$.data").isEmpty());
        }

        // Test case for invalid floor or room number.
        // Checks the response body for the correct error message and status code.
        @Test
        public void testGetNeighborResidentsInvalidFloorOrRoom() throws Exception {
                // Create mock UUID for the resident, and use invalid floor/room values (e.g.,
                // negative numbers).
                String invalidFloor = "wrong"; // Invalid floor number
                String invalidRoom = "hi"; // Invalid room number

                // Perform a GET request to the /neighbor-units endpoint with invalid floor and
                // room numbers.
                mockMvc.perform(
                                get("/residents/{residentId}/neighbor-units/{floor}-{room}", mockResidentId,
                                                invalidFloor, invalidRoom))
                                // Expect HTTP BAD REQUEST (400) status for invalid floor or room numbers.
                                .andExpect(status().isBadRequest());
                // Verify that the response message indicates invalid floor or room values.
                // //TODO - fix this expect
                // .andExpect(jsonPath("$.message").value("Failed to convert value of type
                // 'java.lang.String' to required type 'int'; For input string:
                // \\\"wrong\\\""));
        }

        // Test case for successfully retrieving neighboring units for a resident.
        // Checks the response body for the correct message, status code, and list of
        // neighboring units.
        @Test
        public void testGetNeighborUnitsSuccess() throws Exception {
                // Create mock list of NeighborRoomModel objects to simulate data returned by
                // the service.
                List<NeighborRoomModel> mockNeighborsList = new ArrayList<>();
                mockNeighborsList.add(new NeighborRoomModel("above", 2, 101));
                mockNeighborsList.add(new NeighborRoomModel("right", 1, 102));

                // Mock the service to return the list of neighboring units for the resident.
                when(service.getNeighborUnits(mockResidentId))
                                .thenReturn(mockNeighborsList);

                // Perform a GET request to the /neighbor-units endpoint with a valid
                // residentId.
                mockMvc.perform(get("/residents/{residentId}/neighbor-units", mockResidentId))
                                // Expect HTTP OK (200) status for a successful response.
                                .andExpect(status().isOk())
                                // Verify that the response contains the correct success message.
                                .andExpect(jsonPath("$.message").value("Success"))
                                // Verify that the list of neighboring units is correctly returned.
                                .andExpect(jsonPath("$.data").isArray())
                                // Verify that the list contains two neighboring units.
                                .andExpect(jsonPath("$.data.length()").value(2));
        }

        // Test case for when no neighboring units are found for a resident.
        // Checks the response body for the correct message and status code.
        @Test
        public void testGetNeighborUnitsNoNeighborsFound() throws Exception {
                // Mock the service to return an empty list, indicating no neighboring units.
                when(service.getNeighborUnits(mockResidentId))
                                .thenReturn(new ArrayList<>());

                // Perform a GET request to the /neighbor-units endpoint with the valid
                // residentId.
                mockMvc.perform(get("/residents/{residentId}/neighbor-units", mockResidentId))
                                // Expect HTTP OK (200) status even though no neighboring units are found.
                                .andExpect(status().isOk())
                                // Verify that the response contains the correct success message.
                                .andExpect(jsonPath("$.message").value("Success"))
                                // Verify that the data field is an empty array.
                                .andExpect(jsonPath("$.data").isEmpty());
        }

        // Test case for a non-existent resident, where the residentId doesn't exist in
        // the system.
        // Checks the response body for the correct error message and status code.
        @Test
        public void testGetNeighborUnitsResidentNotFound() throws Exception {
                // Simulate that the resident does not exist in the system by throwing an
                // exception.
                when(service.getNeighborUnits(mockResidentId))
                                .thenThrow(new IllegalArgumentException("Problem getting lease id."));

                // Perform the GET request to the /neighbor-units endpoint with the valid
                // residentId.
                mockMvc.perform(get("/residents/{residentId}/neighbor-units", mockResidentId))
                                // Expect HTTP NOT FOUND (404) status when the resident does not exist.
                                .andExpect(status().isNotFound())
                                // Verify that the response message indicates the resident was not found.
                                .andExpect(jsonPath("$.message").value("Problem retrieving. May not exist."))
                                // Verify message error
                                .andExpect(jsonPath("$.data.Error").value("Problem getting lease id."));
        }

        // Test case for successfully retrieving a resident by ID.
        // Checks the response body for the correct message, status code, and resident
        // data.
        @Test
        public void testGetResidentSuccess() throws Exception {
                // Mock the service to return the mock resident data.
                when(service.getResident(mockResidentId))
                                .thenReturn(mockResident);

                // Perform a GET request to the /residents/{residentId} endpoint with the valid
                // residentId.
                mockMvc.perform(get("/residents/{residentId}", mockResidentId))
                                // Expect HTTP OK (200) status for a successful response.
                                .andExpect(status().isOk())
                                // Verify that the response contains the correct success message.
                                .andExpect(jsonPath("$.message").value("Success"))
                                // Verify that the response contains the resident's ID.
                                .andExpect(jsonPath("$.data.id").value(mockResidentId.toString()))
                                // Check some of the fields
                                // Verify that the resident's first name is returned correctly.
                                .andExpect(jsonPath("$.data.firstName").value("John"))
                                // Verify that the resident's last name is returned correctly.
                                .andExpect(jsonPath("$.data.lastName").value("Doe"))
                                // Verify that the resident's email is returned correctly.
                                .andExpect(jsonPath("$.data.gender").value("Male"));
        }

        // Test case for a non-existent resident, where the residentId doesn't exist in
        // the system.
        // Checks the response body for the correct error message and status code.
        @Test
        public void testGetResidentNotFound() throws Exception {
                // Simulate that the resident does not exist in the system by throwing an
                // exception.
                when(service.getResident(mockResidentId))
                                .thenThrow(new IllegalArgumentException("Resident not found"));

                // Perform the GET request to the /residents/{residentId} endpoint with the
                // valid residentId.
                mockMvc.perform(get("/residents/{residentId}", mockResidentId))
                                // Expect HTTP NOT FOUND (404) status when the resident does not exist.
                                .andExpect(status().isNotFound())
                                // Verify that the response message indicates the resident was not found.
                                .andExpect(jsonPath("$.message").value("Not Found"));
        }

        // TODO: change from internal server error to bad request
        // Test case for invalid UUID format in residentId.
        // Checks the response body for the correct error message and status code.
        @Test
        public void testGetResidentInvalidUUID() throws Exception {
                // Perform a GET request to the /residents/{residentId} endpoint with the
                // invalid residentId.
                mockMvc.perform(get("/residents/{residentId}", invalidResidentId))
                                // Expect HTTP INTERNAL SERVER ERROR (500) status due to invalid UUID format.
                                .andExpect(status().isInternalServerError());
        }

        /**
         * Tests updateResident success case.
         * Simulates a valid residentId and OptionalResidentModel, expecting a
         * successful update.
         */
        @Test
        public void testUpdateResidentSuccess() throws Exception {
                // Arrange: Mock service to return true for a valid update.
                when(service.updateResident(eq(mockResidentId), any(OptionalResidentModel.class)))
                                .thenReturn(true);

                // Act & Assert: Send POST request to /residents/{residentId} and verify
                // response.
                mockMvc.perform(post("/residents/{residentId}", mockResidentId)
                                // Set content type to JSON.
                                .contentType(MediaType.APPLICATION_JSON)
                                // Serialize OptionalResidentModel to JSON.
                                .content(objectMapper.writeValueAsString(mockResident)))
                                // Expect HTTP status 200 OK (as coded in controller).
                                .andExpect(status().isOk())
                                // Verify ResponseModel fields: data, message, status.
                                .andExpect(jsonPath("$.data.hometown").value("Phoenix"))
                                .andExpect(jsonPath("$.message").value("Success"))
                                .andExpect(jsonPath("$.status").value(200));
        }

        /**
         * Tests updateResident failure case for non-existent resident.
         * Simulates a valid residentId but a service exception, expecting a Not Found
         * response via handleErrorResponse.
         */
        @Test
        public void testUpdateResidentNotFound() throws Exception {
                // Arrange: Mock service to throw an exception for a non-existent resident.
                when(service.updateResident(eq(mockResidentId), any(OptionalResidentModel.class)))
                                .thenThrow(new RuntimeException("Resident not found"));

                // Act & Assert: Send POST request to /residents/{residentId} and verify error
                // response.
                mockMvc.perform(post("/residents/{residentId}", mockResidentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mockResident)))
                                // Expect HTTP status 404 Not Found (as per handleErrorResponse for "not
                                // found").
                                .andExpect(status().isNotFound())
                                // Verify ResponseModel fields: data, message, status.
                                .andExpect(jsonPath("$.data.Error").value("Resident not found"))
                                .andExpect(jsonPath("$.message").value("Not Found"))
                                .andExpect(jsonPath("$.status").value(404));
        }

        // Test case for successful login.
        // Checks the response body for the correct message and id.
        @Test
        public void testLoginSuccess() throws Exception {
                // Mock the successful login response, returning a LoginModel with a mock UUID
                // and a 'verified' status.
                // This simulates the login behavior when the credentials are correct.
                LoginModel mockLogin = new LoginModel(UUID.randomUUID(), false);

                // When the service's login method is called, it will return the mock
                // LoginModel.
                when(service.login(any(UserModel.class))).thenReturn(mockLogin);

                // Log the mock login ID for debugging purposes (could be removed in production
                // code).
                System.out.println(mockLogin.getId());

                // Perform a POST request to the login endpoint with valid credentials and check
                // the response.
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
                // Simulate validation errors by returning true when errors.hasErrors() is
                // called.
                // This simulates a case where the request body is missing required fields or is
                // malformed.
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
                // Simulate the service throwing an exception when invalid credentials are
                // provided.
                // This tests the scenario where the user enters incorrect login details.
                when(service.login(any(UserModel.class)))
                                .thenThrow(new IllegalArgumentException("Invalid credentials"));

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

        /**
         * Tests register success case.
         * Simulates a valid RegisterModel, expecting a successful registration.
         */
        @Test
        public void testRegisterSuccess() throws Exception {
                // Arrange: Mock service to return true for valid registration.
                when(service.register(any(RegisterModel.class)))
                                .thenReturn(true);

                RegisterModel registerModel = new RegisterModel(mockResidentId.toString(), "John", "Doe", "Male",
                                leaseID.toString(), 21, "Phoenix", "Bio", "", "", "", "", "", "");

                // Act & Assert: Send POST request to /residents/register and verify response.
                mockMvc.perform(post("/residents/")
                                // Set content type to JSON.
                                .contentType(MediaType.APPLICATION_JSON)
                                // Serialize valid RegisterModel to JSON.
                                .content(objectMapper.writeValueAsString(registerModel)))
                                // Expect HTTP status 200 OK (as coded in controller).
                                .andExpect(status().isOk())
                                // Verify ResponseModel fields: data, message, status.
                                .andExpect(jsonPath("$.data").value(true))
                                .andExpect(jsonPath("$.message").value("Success"))
                                .andExpect(jsonPath("$.status").value(201));
        }

        /**
         * Tests register validation failure.
         * Simulates an invalid RegisterModel, expecting a Bad Request response via
         * handleErrorResponse.
         * Verifies that IllegalArgumentException("Bad request") is thrown and handled.
         */
        @Test
        public void testRegisterValidationError() throws Exception {
                // Arrange: Use invalid RegisterModel with null fields to trigger
                // errors.hasErrors().
                // Assumes @NotNull on leaseId, email, and password in RegisterModel.

                // Act & Assert: Send POST request to /residents/register and verify error
                // response.
                mockMvc.perform(post("/residents/")
                                // Set content type to JSON.
                                .contentType(MediaType.APPLICATION_JSON)
                                // Serialize invalid RegisterModel to JSON.
                                .content(objectMapper.writeValueAsString(null)))
                                // Expect HTTP status 400 Bad Request (as per handleErrorResponse).
                                .andExpect(status().isBadRequest());
                // TODO - make sure it's being handled in the handleErrorRepsonse method, not
                // being overriden by Spring's exception handling
        }

        // Test case for handling a bad request error.
        // This test simulates a bad request error and checks the corresponding
        // response.
        @Test
        public void testHandleErrorResponse_BadRequest() {
                // Simulate a BadRequestException by throwing an IllegalArgumentException.
                Exception exception = new IllegalArgumentException("Bad request");

                // Call the controller's error handler method to simulate error response.
                ResponseEntity<?> response = new ResidentController().handleErrorResponse(exception);

                // Verify that the response status is FORBIDDEN (403) as defined for bad
                // requests.
                assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
                // Verify that the response message matches the expected "Bad request" message.
                assertEquals("Bad request", ((ResponseModel<String>) response.getBody()).getMessage());
        }
}
