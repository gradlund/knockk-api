// Grace Radlund
// 12-15-2024
// Tests generated with the help of ChatGPT 4o mini
package com.knockk.api.data.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import javax.security.auth.login.CredentialException;
import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.jdbc.core.JdbcTemplate;
import com.knockk.api.data.mapper.ResidentMapper;
import com.knockk.api.data.repository.BuildingRepository;
import com.knockk.api.data.repository.FriendshipRepository;
import com.knockk.api.data.repository.LeaseRepository;
import com.knockk.api.data.repository.ResidentRepository;
import com.knockk.api.data.repository.UnitRepository;
import com.knockk.api.data.repository.UserRepository;
import com.knockk.api.entity.BuildingEntity;
import com.knockk.api.entity.ResidentEntity;

public class ResidentDataServiceTests {

    // Mock the BuildingRepository to simulate interactions with the database.
    @Mock
    private BuildingRepository buildingRepository;

    // Mock the FriendshipRepository to simulate interactions with the database.
    @Mock
    private FriendshipRepository friendshipRepository;

    // Mock the LeaseRepository to simulate interactions with the database.
    @Mock
    private LeaseRepository leaseRepository;

    // Mock the UserRepository to simulate interactions with the database.
    @Mock
    private UserRepository userRepository;

    // Mock the ResidentRepository to simulate interactions with the database.
    @Mock
    private ResidentRepository residentRepository;

    // Mock the data service class to call methods.
    @Mock
    private ResidentDataService residentDataService;

    // Mock the UnitRepository to simulate interactions with the database.
    @Mock
    private UnitRepository unitRepository;

    @Mock
    private DataSource dataSource;

    @Mock
    private JdbcTemplate jdbcTemplateObject;

    // Test data to be used in multiple test cases.
    private String validEmail = "testuser@example.com";
    private String validPassword = "password123";
    private UUID validId = UUID.randomUUID(); // A mock UUID to simulate a valid resident ID.
    private ResidentEntity mockResident;

    // Mock the building entity with top floor 6 and bottom floor 1.
    private ArrayList<Integer> noRoomsRight;
    private ArrayList<Integer> noRoomsLeft;
    // private BuildingEntity mockBuilding;
    private UUID buildingId;

    // Create a mock BuildingEntity
    BuildingEntity mockBuilding = mock(BuildingEntity.class);

    @BeforeEach
    public void setUp() {
        // Initialize the mocks before each test method
        MockitoAnnotations.openMocks(this);

        // Inject mocks into the service.
        residentDataService = new ResidentDataService(buildingRepository, dataSource, friendshipRepository,
                leaseRepository, unitRepository, userRepository, residentRepository);

        noRoomsRight = new ArrayList<>(Arrays.asList(22, 56));
        noRoomsLeft = new ArrayList<>(Arrays.asList(32, 66));

        buildingId = UUID.randomUUID();

        mockResident = new ResidentEntity();

        // mockBuilding = new BuildingEntity(buildingId, "Encanto", 500, 1, 6,
        // noRoomsRight, noRoomsLeft, UUID.randomUUID());
    }

    // Test case for checking if the given floor is between the top and bottom
    // floors of a building.
    // This tests the method's behavior when a valid building and floor are
    // provided,
    // and ensures it returns the correct boolean result based on the floor's
    // validity.
    @Test
    public void testCheckNeighborFloor_ValidFloor() {
        // Mock the methods for getTopFloor and getBottomFloor
        when(mockBuilding.getTopFloor()).thenReturn(6); // Set top floor to 6
        when(mockBuilding.getBottomFloor()).thenReturn(1); // Set bottom floor to 1

        // Mock the building repository to return the mock building when findById is
        // called with the specific buildingId
        when(buildingRepository.findById(buildingId)).thenReturn(Optional.of(mockBuilding));

        // Call the method with a valid floor number (e.g., floor 5) and capture the
        // result.
        boolean result = residentDataService.checkNeighborFloor(buildingId, 5);

        // Assert that the method returns true, as floor 5 is between 1 and 6.
        assertTrue(result);

        // Verify that the repository method was called exactly once with the correct
        // building ID.
        verify(buildingRepository, times(1)).findById(buildingId);
    }

    // Test case for when the floor is outside the valid range (i.e., below the
    // bottom floor or above the top floor).
    // This tests the method's behavior when a floor is not valid for a given
    // building.
    @Test
    public void testCheckNeighborFloor_InvalidFloor() {
        // Mock the methods for getTopFloor and getBottomFloor
        when(mockBuilding.getTopFloor()).thenReturn(6); // Set top floor to 6
        when(mockBuilding.getBottomFloor()).thenReturn(1); // Set bottom floor to 1

        // Create a mock building repository to return the mock building when a specific
        // building ID is requested.
        when(buildingRepository.findById(buildingId)).thenReturn(Optional.of(mockBuilding));

        // Call the method with an invalid floor number (e.g., floor 7) and capture the
        // result.
        boolean result = residentDataService.checkNeighborFloor(buildingId, 7);

        // Assert that the method returns false, as floor 7 is above the top floor.
        assertFalse(result);

        // Call the method with another invalid floor number (e.g., floor 0) and capture
        // the result.
        result = residentDataService.checkNeighborFloor(buildingId, 0);

        // Assert that the method returns false, as floor 0 is below the bottom floor.
        assertFalse(result);

        // Verify that the repository method was called exactly twice because the test
        // is checking for invalid top and bottom floors.
        verify(buildingRepository, times(2)).findById(buildingId);
    }

    // Test case for when the building is not found (i.e., the building ID does not
    // exist).
    // This tests how the method handles the case where the building does not exist
    // in the repository.
    @Test
    public void testCheckNeighborFloor_BuildingNotFound() {
        // Mock the building repository to return an empty Optional, simulating a
        // building not found scenario.
        when(buildingRepository.findById(buildingId)).thenReturn(Optional.empty());

        // Call the method with any floor number (e.g., floor 5) and capture the result.
        // Expect a NoSuchElementException to be thrown, as the building is not found.
        assertThrows(NoSuchElementException.class, () -> {
            residentDataService.checkNeighborFloor(buildingId, 5);
        });

        // Verify that the repository method was called exactly once with the correct
        // building ID.
        verify(buildingRepository, times(1)).findById(buildingId);
    }

    // Test case where the room is valid and has neighbors on both sides.
    // Expects the method to return true since the room has valid neighbors.
    @Test
    public void testCheckNeighborRoom_ValidRoom() {
        // Mock the building entity and repository behavior.
        when(mockBuilding.getNoRoomsRight()).thenReturn(new ArrayList<>(Arrays.asList(22, 56))); // Rooms with no
                                                                                                 // neighbors to the
                                                                                                 // right
        when(mockBuilding.getNoRoomsLeft()).thenReturn(new ArrayList<>(Arrays.asList(10, 15))); // Rooms with no
                                                                                                // neighbors to the left
        when(buildingRepository.findById(buildingId)).thenReturn(Optional.of(mockBuilding));

        // Call the method with a room number that is valid and has neighbors.
        boolean result = residentDataService.checkNeighborRoom(buildingId, 5);

        // Assert that the method returns true, as room 5 has neighbors.
        assertTrue(result);

        // Verify that the repository method was called exactly once with the correct
        // building ID.
        verify(buildingRepository, times(1)).findById(buildingId);
    }

    // Test case where the room has no neighbors to the right.
    // Expects the method to return false since the room exists in noRoomsRight.
    @Test
    public void testCheckNeighborRoom_NoNeighborRight() {
        // Mock the building entity and repository behavior.
        when(mockBuilding.getNoRoomsRight()).thenReturn(new ArrayList<>(Arrays.asList(22, 56))); // Rooms with no
                                                                                                 // neighbors to the
                                                                                                 // right
        when(mockBuilding.getNoRoomsLeft()).thenReturn(new ArrayList<>(Arrays.asList(10, 15))); // Rooms with no
                                                                                                // neighbors to the left
        when(buildingRepository.findById(buildingId)).thenReturn(Optional.of(mockBuilding));

        // Call the method with a room number that has no neighbor to the right.
        boolean result = residentDataService.checkNeighborRoom(buildingId, 22);

        // Assert that the method returns false, as room 22 has no neighbor to the
        // right.
        assertFalse(result);

        // Verify that the repository method was called exactly once with the correct
        // building ID.
        verify(buildingRepository, times(1)).findById(buildingId);
    }

    // Test case where the room has no neighbors to the left.
    // Expects the method to return false since the room exists in noRoomsLeft.
    @Test
    public void testCheckNeighborRoom_NoNeighborLeft() {
        // Mock the building entity and repository behavior.
        when(mockBuilding.getNoRoomsRight()).thenReturn(new ArrayList<>(Arrays.asList(22, 56))); // Rooms with no
                                                                                                 // neighbors to the
                                                                                                 // right
        when(mockBuilding.getNoRoomsLeft()).thenReturn(new ArrayList<>(Arrays.asList(10, 15))); // Rooms with no
                                                                                                // neighbors to the left
        when(buildingRepository.findById(buildingId)).thenReturn(Optional.of(mockBuilding));

        // Call the method with a room number that has no neighbor to the left.
        boolean result = residentDataService.checkNeighborRoom(buildingId, 10);

        // Assert that the method returns false, as room 10 has no neighbor to the left.
        assertFalse(result);

        // Verify that the repository method was called exactly once with the correct
        // building ID.
        verify(buildingRepository, times(1)).findById(buildingId);
    }

    // Test case where no rooms are marked as having no neighbors to the left or
    // right.
    // Expects the method to return true since no rooms have missing neighbors.
    @Test
    public void testCheckNeighborRoom_EmptyNeighborLists() {
        // Mock the building entity and repository behavior with empty lists.
        when(mockBuilding.getNoRoomsRight()).thenReturn(new ArrayList<>());
        when(mockBuilding.getNoRoomsLeft()).thenReturn(new ArrayList<>());
        when(buildingRepository.findById(buildingId)).thenReturn(Optional.of(mockBuilding));

        // Call the method with a room number (valid room) to check.
        boolean result = residentDataService.checkNeighborRoom(buildingId, 5);

        // Assert that the method returns true, as there are no rooms with missing
        // neighbors.
        assertTrue(result);

        // Verify that the repository method was called exactly once with the correct
        // building ID.
        verify(buildingRepository, times(1)).findById(buildingId);
    }

    // Test case where the building does not exist in the repository.
    // Expects a NoSuchElementException to be thrown because the building ID is not
    // found.
    @Test
    public void testCheckNeighborRoom_BuildingNotFound() {
        // Mock the repository to return an empty Optional for the building.
        when(buildingRepository.findById(buildingId)).thenReturn(Optional.empty());

        // Call the method with a valid building ID and room number.
        // Expect it to throw a NoSuchElementException since the building does not
        // exist.
        assertThrows(NoSuchElementException.class, () -> {
            residentDataService.checkNeighborRoom(buildingId, 3);
        });

        // Verify that the repository method was called exactly once with the correct
        // building ID.
        verify(buildingRepository, times(1)).findById(buildingId);
    }

    // Test case for successful login with valid email and password.
    @Test
    public void testFindResidentByEmailAndPassword_Success() throws CredentialException {
        // Mock the repository to return a valid UUID when correct credentials are
        // provided.
        when(userRepository.findByEmailAndPassword(validEmail, validPassword)).thenReturn(Optional.of(validId));

        // Call the service method with valid credentials and capture the result.
        UUID result = residentDataService.findResidentByEmailAndPassword(validEmail, validPassword);

        // Assert that the returned UUID is not null, indicating a successful login.
        assertNotNull(result);
        // Assert that the returned UUID matches the expected valid ID.
        assertEquals(validId, result);

        // Verify that the repository method was called exactly once with the correct
        // parameters.
        verify(userRepository, times(1)).findByEmailAndPassword(validEmail, validPassword);
    }

    // Test case for invalid credentials (i.e., wrong email or password).
    @Test
    public void testFindResidentByEmailAndPassword_InvalidCredentials() {
        // Mock the repository to return an empty Optional when invalid credentials are
        // provided.
        when(userRepository.findByEmailAndPassword(validEmail, validPassword)).thenReturn(Optional.empty());

        // Assert that the service method throws a CredentialException when no matching
        // resident is found.
        assertThrows(CredentialException.class, () -> {
            residentDataService.findResidentByEmailAndPassword(validEmail, validPassword);
        });

        // Verify that the repository method was called exactly once with the correct
        // parameters.
        verify(userRepository, times(1)).findByEmailAndPassword(validEmail, validPassword);
    }

    // Test case for an empty email provided in the login attempt.
    @Test
    public void testFindResidentByEmailAndPassword_EmptyEmail() throws CredentialException {
        // Simulate an empty email and mock the repository to return an empty Optional.
        String emptyEmail = "";
        when(userRepository.findByEmailAndPassword(emptyEmail, validPassword)).thenReturn(Optional.empty());

        // Assert that the service method throws a CredentialException when the email is
        // empty.
        assertThrows(CredentialException.class, () -> {
            residentDataService.findResidentByEmailAndPassword(emptyEmail, validPassword);
        });

        // Verify that the repository method was called exactly once with the empty
        // email.
        verify(userRepository, times(1)).findByEmailAndPassword(emptyEmail, validPassword);
    }

    // Test case for an empty password provided in the login attempt.
    @Test
    public void testFindResidentByEmailAndPassword_EmptyPassword() throws CredentialException {
        // Simulate an empty password and mock the repository to return an empty
        // Optional.
        String emptyPassword = "";
        when(userRepository.findByEmailAndPassword(validEmail, emptyPassword)).thenReturn(Optional.empty());

        // Assert that the service method throws a CredentialException when the password
        // is empty.
        assertThrows(CredentialException.class, () -> {
            residentDataService.findResidentByEmailAndPassword(validEmail, emptyPassword);
        });

        // Verify that the repository method was called exactly once with the empty
        // password.
        verify(userRepository, times(1)).findByEmailAndPassword(validEmail, emptyPassword);
    }
}
