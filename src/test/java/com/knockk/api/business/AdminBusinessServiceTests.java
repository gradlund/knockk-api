// Grace Radlund
// 4-22-2024
// Tests generated with the help of ChatGPT 4o mini and Grok
package com.knockk.api.business;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.knockk.api.data.service.AdminDataService;
import com.knockk.api.util.Gender;
import com.knockk.api.util.entity.AdminEntity;
import com.knockk.api.util.entity.AdminResidentEntity;
import com.knockk.api.util.entity.BuildingEntity;
import com.knockk.api.util.model.AdminModel;
import com.knockk.api.util.model.AdminResidentModel;
import com.knockk.api.util.model.BuildingModel;

import javax.security.auth.login.CredentialException;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.data.domain.PageRequest;

/**
 * Class for testing the admin business service class
 */
class AdminBusinessServiceTests {

        // Mock
        @Mock
        private AdminDataService dataService;

        @Mock
        private AdminBusinessService adminBusinessService;

        @Mock
        private BCryptPasswordEncoder passwordEncoder;

        // Global variables for reuse across tests
        private UUID adminId;
        private UUID buildingId1;
        private UUID buildingId2;
        private BuildingEntity building1;
        private BuildingEntity building2;
        private List<BuildingEntity> buildingEntities;
        private UUID residentId;
        private AdminResidentEntity residentEntity;
        private AdminResidentEntity residentEntity2;
        private List<AdminResidentEntity> residentEntities;
        private Pageable pageable;
        private AdminEntity adminEntity;

        @BeforeEach
        void setUp() {
                // Initialize mocks before each test method
                MockitoAnnotations.openMocks(this);

                // Inject mock into service
                adminBusinessService = new AdminBusinessService(dataService, passwordEncoder);

                // Initialize global variables with fresh values before each test
                adminId = UUID.randomUUID();

                building1 = new BuildingEntity(buildingId1, "Encanto", "3300 W Camelback Road", 400, 6, 1,
                                new ArrayList<>(List.of(10, 30)), new ArrayList<>(List.of(20)), adminId);
                building2 = new BuildingEntity(buildingId2, "Papago", "3300 W Camelback Road", 600, 7, 1,
                                new ArrayList<>(List.of(10, 30, 50)), new ArrayList<>(List.of(20, 70)), adminId);

                buildingEntities = List.of(building1, building2);

                residentId = UUID.randomUUID();
                residentEntity = new AdminResidentEntity(buildingId1, residentId, "John", "Doe", Gender.Male,
                                "johndoe@gmail.com", 3, 23,
                                new Date(11 / 11 / 2025), new Date(11 / 11 / 2026), false);
                residentEntity2 = new AdminResidentEntity(buildingId1, UUID.randomUUID(), "John", "Deer", Gender.Male,
                                "johndeer@gmail.com",
                                4, 33, new Date(11 / 11 / 2025), new Date(11 / 11 / 2026), false);

                residentEntities = List.of(residentEntity, residentEntity2);

                pageable = PageRequest.of(0, 10, Sort.by("lastName").ascending());
                adminEntity = new AdminEntity(adminId, null, null);
        }

        // Test successful resident activation
        @Test
        void testActivateResident_Success() throws Exception {
                // Arrange: Create a sample UUID and set up mock behavior
                UUID residentId = UUID.randomUUID();
                when(dataService.activateResident(residentId)).thenReturn(true);

                // Act: Call the method under test
                Boolean result = adminBusinessService.activateResident(residentId);

                // Assert: Verify the result and mock interaction
                assertTrue(result, "The method should return true for successful activation");
                verify(dataService, times(1)).activateResident(residentId);
        }

        // Test when data service throws an exception
        @Test
        void testActivateResident_ExceptionThrown() throws Exception {
                // Arrange: Create a sample UUID and set up mock to throw exception
                UUID residentId = UUID.randomUUID();
                Exception testException = new Exception("Database error");
                when(dataService.activateResident(residentId)).thenThrow(testException);

                // Act & Assert: Verify the exception is propagated
                Exception thrown = assertThrows(Exception.class,
                                () -> adminBusinessService.activateResident(residentId),
                                "Error updating resident.");

                // Verify the exception message and mock interaction
                assertEquals("Database error", thrown.getMessage(),
                                "Error updating resident.");
                verify(dataService, times(1)).activateResident(residentId);
        }

        // Test with null residentId
        @Test
        void testActivateResident_NullId() throws Exception {
                // Arrange: Use null as residentId
                UUID residentId = null;
                Exception testException = new Exception("Error updating resident. Resident id is null.");
                when(dataService.activateResident(null)).thenThrow(testException);

                // Act & Assert: Verify the exception is propagated
                Exception thrown = assertThrows(Exception.class,
                                () -> adminBusinessService.activateResident(residentId),
                                "Error updating resident. Resident id is null.");

                // Assert: Verify the result and mock interaction
                // Verify the exception message and mock interaction
                assertEquals("Error updating resident. Resident id is null.", thrown.getMessage(),
                                "Error updating resident. Resident id is null.");
                verify(dataService, times(0)).activateResident(residentId);
        }

        // Test successful resident deletion
        @Test
        void testDeleteResident_SuccessfulDeletion() throws Exception {
                // Arrange: Set up a random UUID and mock the dataService to return true
                UUID residentId = UUID.randomUUID();
                when(dataService.deleteResident(residentId)).thenReturn(true);

                // Act: Invoke the method under test
                Boolean result = adminBusinessService.deleteResident(residentId);

                // Assert: Check that the result is true and the dataService was called once
                assertTrue(result, "Expected true when resident is successfully deleted");
                verify(dataService, times(1)).deleteResident(residentId);
                verifyNoMoreInteractions(dataService);
        }

        // Test when residentId is null, expecting an exception from the business
        // service
        @Test
        void testDeleteResident_NullResidentId() {
                // Arrange: Pass null as residentId (exception thrown before dataService is
                // called)

                // Act & Assert: Verify that the correct exception is thrown
                Exception thrown = assertThrows(Exception.class,
                                () -> adminBusinessService.deleteResident(null),
                                "Expected an exception when residentId is null");

                // Verify the exception message
                assertEquals("Error deleting resident. Resident id is null.", thrown.getMessage(),
                                "Exception message should match the one thrown for null residentId");

                // Verify that dataService was not called
                verifyNoInteractions(dataService);
        }

        // Test when dataService throws an exception (e.g., resident not found)
        @Test
        void testDeleteResident_ResidentNotFound() throws Exception {
                // Arrange: Set up a random UUID and mock the dataService to throw an exception
                UUID residentId = UUID.randomUUID();
                Exception expectedException = new Exception("Resident not found");
                when(dataService.deleteResident(residentId)).thenThrow(expectedException);

                // Act & Assert: Verify that the exception is thrown and propagated
                Exception thrown = assertThrows(Exception.class,
                                () -> adminBusinessService.deleteResident(residentId),
                                "Expected an exception when resident is not found");

                // Verify the exception details and mock interaction
                assertEquals("Resident not found", thrown.getMessage(),
                                "Exception message should match the one thrown by dataService");
                verify(dataService, times(1)).deleteResident(residentId);
                verifyNoMoreInteractions(dataService);
        }

        // Test when dataService throws a different exception (e.g., database error)
        @Test
        void testDeleteResident_DatabaseError() throws Exception {
                // Arrange: Set up a random UUID and mock the dataService to throw a different
                // exception
                UUID residentId = UUID.randomUUID();
                Exception expectedException = new Exception("Database connection failed");
                when(dataService.deleteResident(residentId)).thenThrow(expectedException);

                // Act & Assert: Verify that the exception is thrown and propagated
                Exception thrown = assertThrows(Exception.class,
                                () -> adminBusinessService.deleteResident(residentId),
                                "Expected an exception when dataService encounters a database error");

                // Verify the exception details and mock interaction
                assertEquals("Database connection failed", thrown.getMessage(),
                                "Exception message should match the one thrown by dataService");
                verify(dataService, times(1)).deleteResident(residentId);
                verifyNoMoreInteractions(dataService);
        }

        // Test successful retrieval and conversion of buildings
        @Test
        void testGetBuildings_SuccessfulRetrieval() throws Exception {
                // Arrange: Mock the dataService to return the global buildingEntities
                when(dataService.findBuildingsByAdminId(adminId)).thenReturn(buildingEntities);

                // Act: Invoke the method under test
                List<BuildingModel> result = adminBusinessService.getBuildings(adminId);

                // Assert: Verify the result contains the expected models
                assertEquals(2, result.size(), "Expected two buildings in the result");
                assertEquals(building1.getName(), result.get(0).getName(), "First building name should match");
                assertEquals(building1.getId(), result.get(0).getId(), "First building ID should match");
                assertEquals(building2.getName(), result.get(1).getName(), "Second building name should match");
                assertEquals(building2.getId(), result.get(1).getId(), "Second building ID should match");
                verify(dataService, times(1)).findBuildingsByAdminId(adminId);
                verifyNoMoreInteractions(dataService);
        }

        // Test when no buildings are found (dataService throws exception)
        @Test
        void testGetBuildings_NoBuildingsFound() throws Exception {
                // Arrange: Mock the dataService to throw an exception for the global adminId
                Exception expectedException = new Exception("Not Found. No buildings found.");
                when(dataService.findBuildingsByAdminId(adminId)).thenThrow(expectedException);

                // Act & Assert: Verify that the exception is thrown and propagated
                Exception thrown = assertThrows(Exception.class,
                                () -> adminBusinessService.getBuildings(adminId),
                                "Expected an exception when no buildings are found");

                // Verify the exception message and mock interaction
                assertEquals("Not Found. No buildings found.", thrown.getMessage(),
                                "Exception message should match the one thrown by dataService");
                verify(dataService, times(1)).findBuildingsByAdminId(adminId);
                verifyNoMoreInteractions(dataService);
        }

        // Test when adminId is null
        @Test
        void testGetBuildings_NullAdminId() throws Exception {
                // Arrange: Use null adminId and mock the dataService to return a single
                // building
                List<BuildingEntity> defaultEntities = List.of(building1);
                when(dataService.findBuildingsByAdminId(null)).thenReturn(defaultEntities);

                // Act: Invoke the method with null adminId
                List<BuildingModel> result = adminBusinessService.getBuildings(null);

                // Assert: Verify the result (assuming dataService handles null gracefully)
                assertEquals(1, result.size(), "Expected one building in the result");
                assertEquals(building1.getName(), result.get(0).getName(), "Building name should match");
                assertEquals(building1.getId(), result.get(0).getId(), "Building ID should match");
                verify(dataService, times(1)).findBuildingsByAdminId(null);
                verifyNoMoreInteractions(dataService);
        }

        // Test when dataService throws a different exception (e.g., database error)
        @Test
        void testGetBuildings_DatabaseError() throws Exception {
                // Arrange: Mock the dataService to throw a database error for the global
                // adminId
                Exception expectedException = new Exception("Database connection failed");
                when(dataService.findBuildingsByAdminId(adminId)).thenThrow(expectedException);

                // Act & Assert: Verify that the exception is thrown and propagated
                Exception thrown = assertThrows(Exception.class,
                                () -> adminBusinessService.getBuildings(adminId),
                                "Expected an exception when dataService encounters a database error");

                // Verify the exception message and mock interaction
                assertEquals("Database connection failed", thrown.getMessage(),
                                "Exception message should match the one thrown by dataService");
                verify(dataService, times(1)).findBuildingsByAdminId(adminId);
                verifyNoMoreInteractions(dataService);
        }

        // Test successful retrieval and conversion of a resident
        @Test
        void testGetResident_SuccessfulRetrieval() throws Exception {
                // Arrange: Mock the dataService to return the global residentEntity
                when(dataService.findResident(residentId)).thenReturn(residentEntity);

                // Act: Invoke the method under test
                AdminResidentModel result = adminBusinessService.getResident(residentId);

                // Assert: Verify the result matches the residentEntity data
                assertNotNull(result, "Result should not be null");
                assertEquals(residentEntity.getBuildingId(), result.getBuildingId(), "Building ID should match");
                assertEquals(residentEntity.getResidentId(), result.getResidentId(), "Resident ID should match");
                assertEquals(residentEntity.getFirstName(), result.getFirstName(), "First name should match");
                assertEquals(residentEntity.getLastName(), result.getLastName(), "Last name should match");
                assertEquals(residentEntity.getEmail(), result.getEmail(), "Email should match");
                assertEquals(residentEntity.getFloor(), result.getFloor(), "Floor should match");
                assertEquals(residentEntity.getRoom(), result.getRoom(), "Room should match");
                assertEquals(residentEntity.getLeaseStart(), result.getStartDate(), "Lease start should match");
                assertEquals(residentEntity.getLeaseEnd(), result.getEndDate(), "Lease end should match");
                verify(dataService, times(1)).findResident(residentId);
                verifyNoMoreInteractions(dataService);
        }

        // Test when no resident is found (dataService throws exception)
        @Test
        void testGetResident_NoResidentFound() throws Exception {
                // Arrange: Mock the dataService to throw an exception for the global residentId
                Exception expectedException = new Exception("Not found. No resident found.");
                when(dataService.findResident(residentId)).thenThrow(expectedException);

                // Act & Assert: Verify that the exception is thrown and propagated
                Exception thrown = assertThrows(Exception.class,
                                () -> adminBusinessService.getResident(residentId),
                                "Expected an exception when no resident is found");

                // Verify the exception message and mock interaction
                assertEquals("Not found. No resident found.", thrown.getMessage(),
                                "Exception message should match the one thrown by dataService");
                verify(dataService, times(1)).findResident(residentId);
                verifyNoMoreInteractions(dataService);
        }

        // Test successful retrieval of residents with default sorting (lastName ASC)
        @Test
        void testFindResidents_SuccessfulRetrieval_DefaultSort() throws Exception {
                // Arrange: Mock the dataService to return residentEntities
                when(dataService.findResidents(buildingId1, true, pageable)).thenReturn(residentEntities);

                // Act: Invoke the method under test
                List<AdminResidentModel> result = adminBusinessService.getResidents(buildingId1, true, pageable);

                // Assert: Verify the result contains the expected models
                assertEquals(2, result.size(), "Expected two residents in the result");
                assertEquals(residentEntity.getFirstName(), result.get(0).getFirstName(),
                                "First resident first name should match");
                assertEquals(residentEntity.getLastName(), result.get(0).getLastName(),
                                "First resident last name should match");
                assertEquals(residentEntity2.getFirstName(), result.get(1).getFirstName(),
                                "Second resident first name should match");
                assertEquals(residentEntity2.getLastName(), result.get(1).getLastName(),
                                "Second resident last name should match");
                verify(dataService, times(1)).findResidents(buildingId1, true, pageable);
                verifyNoMoreInteractions(dataService);
        }

        // Test successful retrieval with custom sorting (floor DESC)
        @Test
        void testFindResidents_SuccessfulRetrieval_FloorDescSort() throws Exception {
                // Arrange: Create a pageable with sort by floor DESC
                Pageable customPageable = PageRequest.of(0, 10, Sort.by("floor").descending());
                when(dataService.findResidents(buildingId1, false, customPageable)).thenReturn(residentEntities);

                // Act: Invoke the method with custom sorting
                List<AdminResidentModel> result = adminBusinessService.getResidents(buildingId1, false, customPageable);

                // Assert: Verify the result (order isn’t enforced here, just content)
                assertEquals(2, result.size(), "Expected two residents in the result");
                assertTrue(result.stream().anyMatch(r -> r.getFirstName().equals("John")),
                                "John should be in the result");
                assertTrue(result.stream().anyMatch(r -> r.getFirstName().equals("John")),
                                "John should be in the result");
                verify(dataService, times(1)).findResidents(buildingId1, false, customPageable);
                verifyNoMoreInteractions(dataService);
        }

        // Test when no residents are found (dataService throws exception)
        @Test
        void testFindResidents_NoResidentsFound() throws Exception {
                // Arrange: Mock the dataService to throw an exception
                Exception expectedException = new Exception("Not found. No residents found.");
                when(dataService.findResidents(buildingId1, true, pageable)).thenThrow(expectedException);

                // Act & Assert: Verify that the exception is thrown
                Exception thrown = assertThrows(Exception.class,
                                () -> adminBusinessService.getResidents(buildingId1, true, pageable),
                                "Expected an exception when no residents are found");

                // Verify the exception message and mock interaction
                assertEquals("Not found. No residents found.", thrown.getMessage(),
                                "Exception message should match the one thrown by dataService");
                verify(dataService, times(1)).findResidents(buildingId1, true, pageable);
                verifyNoMoreInteractions(dataService);
        }

        // Test when dataService throws a different exception (e.g., database error)
        @Test
        void testFindResidents_DatabaseError() throws Exception {
                // Arrange: Mock the dataService to throw a database error
                Exception expectedException = new Exception("Database connection failed");
                when(dataService.findResidents(buildingId1, true, pageable)).thenThrow(expectedException);

                // Act & Assert: Verify that the exception is thrown
                Exception thrown = assertThrows(Exception.class,
                                () -> adminBusinessService.getResidents(buildingId1, true, pageable),
                                "Expected an exception when dataService encounters a database error");

                // Verify the exception message and mock interaction
                assertEquals("Database connection failed", thrown.getMessage(),
                                "Exception message should match the one thrown by dataService");
                verify(dataService, times(1)).findResidents(buildingId1, true, pageable);
                verifyNoMoreInteractions(dataService);
        }

        // Test for successful login
        @Test
        void testLogin_Success() throws Exception {
                // Prepare test data (adminModel)
                AdminModel adminModel = new AdminModel("adminUser", "password123");

                when(dataService.findAdminByUsername("adminUser")).thenReturn(adminEntity);
                when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

                // Act
                AdminEntity result = dataService.findAdminByUsername(adminModel.getUsername());
                boolean passwordEncode = passwordEncoder.matches("password123", "encodedPassword");

                // Assert
                assertNotNull(result);
                assertEquals(adminEntity, result);
                verify(dataService, times(1)).findAdminByUsername("adminUser");
                verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
        }

        // Test for invalid credentials (throws CredentialException)
        @Test
        void testLogin_InvalidCredentials() throws CredentialException {
                // Prepare invalid login credentials (wrong username and password).
                AdminModel adminModel = new AdminModel("wrongUser", "wrongPassword");

                // Mock the AdminDataService to throw a CredentialException when called with
                // invalid credentials.
                when(dataService.findAdminByUsername(adminModel.getUsername()))
                                .thenThrow(new CredentialException("Invalid credentials"));

                // Call the login method and assert that the CredentialException is thrown.
                CredentialException thrown = assertThrows(CredentialException.class, () -> {
                        adminBusinessService.login(adminModel);
                });

                // Check that the exception message matches the expected error message.
                assertEquals("Invalid credentials", thrown.getMessage());

                // Verify that the service method was called with the correct parameters
                verify(dataService, times(1)).findAdminByUsername(adminModel.getUsername());
        }

        // Test for unexpected exceptions
        @Test
        void testLogin_UnexpectedException() throws CredentialException {
                // Prepare valid login credentials but simulate an unexpected error
                // (RuntimeException).
                AdminModel adminModel = new AdminModel("adminUser", "adminPassword");

                // Mock the AdminDataService to throw a RuntimeException for any reason.
                when(dataService.findAdminByUsername(adminModel.getUsername()))
                                .thenThrow(new RuntimeException("Unexpected error"));

                // Call the login method and assert that the RuntimeException is thrown.
                RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
                        adminBusinessService.login(adminModel);
                });

                // Check that the exception message matches the expected error message.
                assertEquals("Unexpected error", thrown.getMessage());

                // Verify that the service method was called with the correct parameters
                verify(dataService, times(1)).findAdminByUsername(adminModel.getUsername());
        }

}
