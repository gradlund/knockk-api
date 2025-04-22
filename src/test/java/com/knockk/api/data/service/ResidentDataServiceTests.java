// Grace Radlund
// 4-22-2024
// Tests generated with the help of ChatGPT 4o mini and Grok
package com.knockk.api.data.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Date;
import java.util.UUID;
import javax.security.auth.login.CredentialException;
import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.jdbc.core.JdbcTemplate;
import com.knockk.api.data.repository.BuildingRepository;
import com.knockk.api.data.repository.FriendshipRepository;
import com.knockk.api.data.repository.LeaseRepository;
import com.knockk.api.data.repository.ResidentRepository;
import com.knockk.api.data.repository.UnitRepository;
import com.knockk.api.data.repository.UserRepository;
import com.knockk.api.util.Gender;
import com.knockk.api.util.entity.BuildingEntity;
import com.knockk.api.util.entity.FriendshipEntity;
import com.knockk.api.util.entity.ResidentEntity;
import com.knockk.api.util.entity.UnitEntity;
import com.knockk.api.util.entity.UserEntity;
import com.knockk.api.data.mapper.ResidentMapper;

/**
 * Class that tests the resident data service
 */
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

    // Mock the UnitRepository to simulate interactions with the database.
    @Mock
    private UnitRepository unitRepository;

    // Mock the datasource
    @Mock
    private DataSource dataSource;

    // Mock the jdbc template
    @Mock
    private JdbcTemplate jdbcTemplateObject;

    // Service instance with mocks injected by Mockito
    @InjectMocks
    private ResidentDataService residentDataService;

    // Test data to be used in multiple test cases.
    private String validEmail = "testuser@example.com";
    private String validPassword = "password123";
    private UUID validId = UUID.randomUUID(); // A mock UUID to simulate a valid resident ID.
    private ResidentEntity mockResident;
    private UserEntity user;

    // Mock test building information
    private BuildingEntity mockBuilding;
    private ArrayList<Integer> noRoomsRight;
    private ArrayList<Integer> noRoomsLeft;
    private UUID buildingId;
    private String address;
    private String name;

    // Mock test unit information
    private UUID leaseId;
    private UUID unitId;
    private Date startDate;
    private Date endDate;

    // Mock test friendship information
    private UUID invitorId;
    private UUID inviteeId;
    private FriendshipEntity mockFriendship;

    // Mock UUIDs
    private UUID residentId;
    private UUID friendId;

    // Mock unit
    private UnitEntity mockUnit;

    @BeforeEach
    public void setUp() {
        // Initialize the mocks before each test method
        MockitoAnnotations.openMocks(this);

        // Initialize building data, lease and unit data
        noRoomsRight = new ArrayList<>(Arrays.asList(22, 56));
        noRoomsLeft = new ArrayList<>(Arrays.asList(32, 66));
        buildingId = UUID.randomUUID();
        leaseId = UUID.randomUUID();
        unitId = UUID.randomUUID();
        mockUnit = new UnitEntity(UUID.randomUUID(), 3, 209, 4, buildingId);
        // mockBuilding = new BuildingEntity(UUID.randomUUID(), "Encanto", 500, 6, 1,
        // noRoomsRight, noRoomsLeft, UUID.randomUUID());
        mockBuilding = mock(BuildingEntity.class);
        address = "123 Main St";
        name = "Sunset Towers";
        startDate = new Date();
        endDate = new Date();

        // Mock UUIDs
        residentId = UUID.randomUUID();
        friendId = UUID.randomUUID();

        // Mock resident data
        mockResident = new ResidentEntity(residentId, "Grace", "Radlund", Gender.Female, 21, "Sun Prairie", "",
                "", "", "ginsta", "gsnap", "gx", "gface", leaseId, true);

        // Mock friendship data
        mockFriendship = new FriendshipEntity(UUID.randomUUID(), new Date(), invitorId, inviteeId, false);
        invitorId = UUID.randomUUID();
        inviteeId = UUID.randomUUID();

        user = new UserEntity(residentId, validEmail, validPassword);

    }

    // Test successfully creating an account
    @Test
    public void testCreateAccount_Success() throws Exception {
        // Arrange: Set up a new user with no existing email
        user.setEmail("newuser@example.com"); // Set email
        user.setPassword("password123"); // Set password
        UUID newUserId = UUID.randomUUID(); // Expected returned ID

        // Mock repository to indicate email doesn't exist and save returns new ID
        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(userRepository.saveAccount("newuser@example.com", "password123")).thenReturn(newUserId);

        // Act: Call the method to create the account
        UUID result = residentDataService.createAccount(user);

        // Assert: Verify the returned ID matches and repository methods were called
        assertEquals(newUserId, result);
        verify(userRepository, times(1)).findByEmail("newuser@example.com");
        verify(userRepository, times(1)).saveAccount("newuser@example.com", "password123");
        verifyNoMoreInteractions(userRepository);
    }

    // Test that throws an exception if the email already exists
    @Test
    public void testCreateAccount_EmailAlreadyExists_ThrowsException() {
        // Arrange: Set up a user with an existing email
        user.setEmail("existinguser@example.com");
        user.setPassword("password123");
        UserEntity userEntity = new UserEntity(residentId, user.getEmail(), user.getPassword());

        // Mock repository to indicate email already exists
        when(userRepository.findByEmail("existinguser@example.com")).thenReturn(Optional.of(userEntity));

        // Act & Assert: Expect an exception when email is taken
        Exception exception = assertThrows(Exception.class, () -> {
            residentDataService.createAccount(user);
        });

        // Verify the exception message and that saveAccount wasn't called
        assertEquals("User already exists with that email.", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("existinguser@example.com");
        verify(userRepository, never()).saveAccount(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
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

    /**
     * Test to verify if a resident's credentials are correct and whether they are
     * verified.
     * The method checks if the resident exists in the database and throws an
     * exception if not.
     * Then, it returns whether the resident is verified.
     * In this test, we mock the database response to test both success and failure
     * cases.
     */
    @Test
    public void testCheckVerified_ResidentVerified_Success() throws CredentialException {
        // Arrange: Set up a resident ID and a verified resident
        mockResident.setVerified(true); // Resident is verified
        List<ResidentEntity> residents = List.of(mockResident); // List with one resident

        // Mock JdbcTemplate query to return the resident list
        String sql = "SELECT * FROM \"Resident\" WHERE resident_id = '" + residentId + "'";
        when(jdbcTemplateObject.query(eq(sql), any(ResidentMapper.class))).thenReturn(residents);

        // Act: Call the method to check verification status
        boolean result = residentDataService.checkVerified(residentId);

        // Assert: Verify the resident is verified and JdbcTemplate was called
        assertTrue(result);
        verify(jdbcTemplateObject, times(1)).query(eq(sql), any(ResidentMapper.class));
        verifyNoMoreInteractions(jdbcTemplateObject);
    }

    /**
     * Test to verify that the method throws a CredentialException when no resident
     * is found.
     * This tests the case where the database query returns no results, and an
     * exception should
     * be thrown to indicate invalid credentials.
     */
    @Test
    public void testCheckVerified_ResidentNotVerified_Success() throws CredentialException {
        // Arrange: Set up an unverified resident
        mockResident.setVerified(false); // Resident is not verified
        List<ResidentEntity> residents = List.of(mockResident);

        // Mock JdbcTemplate query to return the resident list
        String sql = "SELECT * FROM \"Resident\" WHERE resident_id = '" + residentId + "'";
        when(jdbcTemplateObject.query(eq(sql), any(ResidentMapper.class))).thenReturn(residents);

        // Act: Call the method to check verification status
        boolean result = residentDataService.checkVerified(residentId);

        // Assert: Verify the resident is not verified and JdbcTemplate was called
        assertFalse(result);
        verify(jdbcTemplateObject, times(1)).query(eq(sql), any(ResidentMapper.class));
        verifyNoMoreInteractions(jdbcTemplateObject);
    }

    /**
     * Test to verify that a new friendship is created successfully when no
     * friendship exists.
     * We mock the repository behavior to simulate adding a friendship, and the
     * method should
     * return the created friendship entity.
     */
    @Test
    public void testCreateFriendship_Success() throws Exception {
        // Mock the repository's findByResidentIdAndNeighborId method to return an empty
        // Optional
        when(friendshipRepository.findByResidentIdAndNeighborId(invitorId, inviteeId))
                .thenReturn(Optional.empty()) // First call, no friendship exists
                .thenReturn(Optional.of(mockFriendship)); // Second call, friendship exists after creation

        // Mock the repository's addFriendship method to return a successful result
        // (non-zero rows)
        when(friendshipRepository.addFriendship(invitorId, inviteeId, false))
                .thenReturn(UUID.randomUUID());

        // Chat is stupid and wrong! It had me do this, but it was causing an issue
        // because it rewrote the calls
        // Mock the findByResidentIdAndNeighborId method again to return a friendship
        // entity after creation
        // when(friendshipRepository.findByResidentIdAndNeighborId(invitorId,
        // inviteeId))
        // .thenReturn(Optional.of(mockFriendship));

        // Act: Call the method under test
        FriendshipEntity createdFriendship = residentDataService.createFriendship(invitorId, inviteeId);

        // Assert: Verify that the friendship was created successfully
        assertNotNull(createdFriendship);
        // assertEquals(invitorId, createdFriendship.getInvitorId());
        // assertEquals(inviteeId, createdFriendship.getInviteeId());
        assertFalse(createdFriendship.isAccepted());

        // Verify that the repository methods were called with the correct arguments
        verify(friendshipRepository, times(2)).findByResidentIdAndNeighborId(invitorId, inviteeId);
        verify(friendshipRepository).addFriendship(invitorId, inviteeId, false);
    }

    /**
     * Test to verify that an exception is thrown when the friendship already
     * exists.
     * We mock the repository's findByResidentIdAndNeighborId method to simulate an
     * existing friendship.
     */
    @Test
    public void testCreateFriendship_AlreadyExists_ThrowsException() throws Exception {

        // Mock the repository's findByResidentIdAndNeighborId method to return an
        // existing friendship
        when(friendshipRepository.findByResidentIdAndNeighborId(invitorId, inviteeId))
                .thenReturn(Optional.of(mockFriendship));

        // Assert that the service method throws a Exception
        assertThrows(Exception.class, () -> {
            residentDataService.createFriendship(invitorId, inviteeId);
        });

        // Verify that the repository method was called exactly once with the correct
        // parameters.
        verify(friendshipRepository, times(1)).findByResidentIdAndNeighborId(invitorId, inviteeId);
    }

    /**
     * Test to verify that checkConnected returns false when no friendship exists.
     */
    @Test
    public void testCheckConnected_NoFriendship_ReturnsFalse() {
        // Mock the repository's findByResidentIdAndNeighborId method to return an empty
        // Optional
        when(friendshipRepository.findByResidentIdAndNeighborId(residentId, friendId))
                .thenReturn(Optional.empty());

        // Act: Call the method under test
        boolean isConnected = residentDataService.checkConnected(residentId, friendId);

        // Assert: Verify that the method returns false when no friendship exists
        assertFalse(isConnected);

        // Verify that the repository method was called once with the correct arguments
        verify(friendshipRepository).findByResidentIdAndNeighborId(residentId, friendId);
    }

    /**
     * Test to verify that checkConnected returns true when a friendship exists and
     * is accepted.
     */
    @Test
    public void testCheckConnected_FriendshipAccepted_ReturnsTrue() {
        mockFriendship.setAccepted(true);

        // Mock the repository's findByResidentIdAndNeighborId method to return the
        // accepted friendship
        when(friendshipRepository.findByResidentIdAndNeighborId(residentId, friendId))
                .thenReturn(Optional.of(mockFriendship));

        // Act: Call the method under test
        boolean isConnected = residentDataService.checkConnected(residentId, friendId);

        // Assert: Verify that the method returns true when the friendship is accepted
        assertTrue(isConnected);

        // Verify that the repository method was called once with the correct arguments
        verify(friendshipRepository).findByResidentIdAndNeighborId(residentId, friendId);
    }

    /**
     * Test to verify that checkConnected returns false when a friendship exists but
     * is not accepted.
     */
    @Test
    public void testCheckConnected_FriendshipNotAccepted_ReturnsFalse() {
        // Mock the repository's findByResidentIdAndNeighborId method to return the
        // non-accepted friendship
        when(friendshipRepository.findByResidentIdAndNeighborId(residentId, friendId))
                .thenReturn(Optional.of(mockFriendship));

        // Act: Call the method under test
        boolean isConnected = residentDataService.checkConnected(residentId, friendId);

        // Assert: Verify that the method returns false when the friendship is not
        // accepted
        assertFalse(isConnected);

        // Verify that the repository method was called once with the correct arguments
        verify(friendshipRepository).findByResidentIdAndNeighborId(residentId, friendId);
    }

    /**
     * Tests createResident success case.
     * Simulates a valid ResidentEntity, expecting true when the resident is saved
     * successfully.
     */
    @Test
    public void testCreateResidentSuccess() throws Exception {
        // Arrange: Mock residentRepository.register to return 1 (one row affected).
        when(residentRepository.register(
                eq(residentId),
                eq("Grace"),
                eq("Radlund"),
                eq(21),
                eq("Sun Prairie"),
                eq(""),
                eq(""),
                eq(""),
                eq("ginsta"),
                eq("gsnap"),
                eq("gx"),
                eq("gface"),
                eq(Gender.Female),
                eq(leaseId),
                eq(true))).thenReturn(1);

        // Act: Call createResident with a valid ResidentEntity.
        boolean result = residentDataService.createResident(mockResident);

        // Assert: Verify the method returns true.
        assertTrue(result, "createResident should return true on success");
    }

    /**
     * Tests createResident failure case when no rows are affected.
     * Simulates a repository failure, expecting an Exception to be thrown.
     */
    @Test
    public void testCreateResidentFailure() {
        // Arrange: Mock residentRepository.register to return 0 (no rows affected).
        when(residentRepository.register(
                eq(residentId),
                eq("Grace"),
                eq("Radlund"),
                eq(21),
                eq("Sun Prairie"),
                eq(""),
                eq(""),
                eq(""),
                eq("ginsta"),
                eq("gsnap"),
                eq("gx"),
                eq("gface"),
                eq(Gender.Female),
                eq(leaseId),
                eq(true))).thenReturn(0);

        // Act & Assert: Expect Exception when no rows are affected.
        Exception exception = assertThrows(Exception.class,
                () -> residentDataService.createResident(mockResident),
                "Should throw Exception when no rows are affected");
        assertEquals("Could not save the resident.", exception.getMessage(),
                "Exception message should match");
    }

    /**
     * Test to verify that deleteFriendship returns true when the friendship is
     * deleted successfully.
     */
    @Test
    public void testDeleteFriendship_Success() throws Exception {
        // Mock the repository's deleteFriendship method to return 1 (indicating
        // successful deletion)
        when(friendshipRepository.deleteFriendship(residentId, friendId))
                .thenReturn(1); // Successful deletion (1 row deleted)

        // Act: Call the method under test
        boolean result = residentDataService.deleteFriendship(residentId, friendId);

        // Assert: Verify that the method returns true when the deletion is successful
        assertTrue(result);

        // Verify that the repository method was called once with the correct arguments
        verify(friendshipRepository).deleteFriendship(residentId, friendId);
    }

    /**
     * Test to verify that deleteFriendship throws an exception when the deletion
     * fails.
     */
    @Test
    public void testDeleteFriendship_Failure_ThrowsException() throws Exception {
        // Mock the repository's deleteFriendship method to return 0 (indicating failure
        // to delete)
        when(friendshipRepository.deleteFriendship(residentId, friendId))
                .thenReturn(0); // Failed deletion (0 rows deleted)

        // Assert that the service method throws a Exception
        assertThrows(Exception.class, () -> {
            residentDataService.deleteFriendship(residentId, friendId);
        });

        // Verify that the repository method was called exactly once with the correct
        // parameters.
        verify(friendshipRepository, times(1)).deleteFriendship(residentId, friendId);
    }

    /**
     * Test to verify that findFriendship returns a FriendshipEntity when the
     * friendship exists.
     */
    @Test
    public void testFindFriendship_FriendshipExists_ReturnsFriendship() {
        // Mock the repository's findByResidentIdAndNeighborId method to return the
        // existing friendship
        when(friendshipRepository.findByResidentIdAndNeighborId(residentId, friendId))
                .thenReturn(Optional.of(mockFriendship));

        // Act: Call the method under test
        Optional<FriendshipEntity> result = residentDataService.findFriendship(residentId, friendId);

        // Assert: Verify that the returned Optional contains the mockFriendship
        assertTrue(result.isPresent());
        assertEquals(mockFriendship, result.get());

        // Verify that the repository method was called once with the correct arguments
        verify(friendshipRepository).findByResidentIdAndNeighborId(residentId, friendId);
    }

    /**
     * Test to verify that findFriendship returns an empty Optional when the
     * friendship does not exist.
     */
    @Test
    public void testFindFriendship_FriendshipDoesNotExist_ReturnsEmptyOptional() {
        // Mock the repository's findByResidentIdAndNeighborId method to return an empty
        // Optional (no friendship)
        when(friendshipRepository.findByResidentIdAndNeighborId(residentId, friendId))
                .thenReturn(Optional.empty());

        // Act: Call the method under test
        Optional<FriendshipEntity> result = residentDataService.findFriendship(residentId, friendId);

        // Assert: Verify that the returned Optional is empty
        assertFalse(result.isPresent());

        // Verify that the repository method was called once with the correct arguments
        verify(friendshipRepository).findByResidentIdAndNeighborId(residentId, friendId);
    }

    /**
     * Test to verify that findNeighborsByUnit returns a list of UUIDs when
     * neighbors exist.
     */
    @Test
    public void testFindNeighborsByUnit_NeighborsExist_ReturnsList() {
        // Arrange: Define the floor and room
        int floor = 2;
        int room = 305;

        // Create a list of mock UUIDs representing the neighbors (residents)
        UUID resident1 = UUID.randomUUID();
        UUID resident2 = UUID.randomUUID();
        List<UUID> mockNeighbors = Arrays.asList(resident1, resident2);

        // Mock the repository's findResidentsByUnit method to return the mock list of
        // neighbors
        when(residentRepository.findResidentsByUnit(floor, room)).thenReturn(mockNeighbors);

        // Act: Call the method under test
        List<UUID> result = residentDataService.findNeighborsByUnit(floor, room);

        // Assert: Verify that the returned list matches the expected neighbors list
        assertNotNull(result); // Ensure the result is not null
        assertEquals(2, result.size()); // Verify the size of the list
        assertTrue(result.contains(resident1)); // Verify resident1 is in the list
        assertTrue(result.contains(resident2)); // Verify resident2 is in the list

        // Verify that the repository method was called once with the correct arguments
        verify(residentRepository).findResidentsByUnit(floor, room);
    }

    /**
     * Test to verify that findNeighborsByUnit returns an empty list when no
     * neighbors are found.
     */
    @Test
    public void testFindNeighborsByUnit_NoNeighborsFound_ReturnsEmptyList() {
        // Arrange: Define the floor and room
        int floor = 3;
        int room = 101;

        // Mock the repository's findResidentsByUnit method to return an empty list (no
        // neighbors)
        when(residentRepository.findResidentsByUnit(floor, room)).thenReturn(Arrays.asList());

        // Act: Call the method under test
        List<UUID> result = residentDataService.findNeighborsByUnit(floor, room);

        // Assert: Verify that the returned list is empty
        assertNotNull(result); // Ensure the result is not null
        assertTrue(result.isEmpty()); // Verify the list is empty

        // Verify that the repository method was called once with the correct arguments
        verify(residentRepository).findResidentsByUnit(floor, room);
    }

    /**
     * Test to verify that findUnitByUserId returns the unit ID when both lease and
     * unit are found.
     */
    @Test
    public void testFindUnitByUserId_ValidResident_ReturnsUnitId() throws Exception {
        // Mock the repository methods to return the appropriate values
        when(residentRepository.findLeaseIdByResidentId(residentId)).thenReturn(Optional.of(leaseId));
        when(leaseRepository.findUnitByLeaseId(leaseId)).thenReturn(Optional.of(unitId));

        // Act: Call the method under test
        UUID result = residentDataService.findUnitByUserId(residentId);

        // Assert: Verify that the unitId is returned as expected
        assertEquals(unitId, result);

        // Verify that both repository methods were called with the correct arguments
        verify(residentRepository).findLeaseIdByResidentId(residentId);
        verify(leaseRepository).findUnitByLeaseId(leaseId);
    }

    /**
     * Test to verify that findUnitByUserId throws an exception when no lease ID is
     * found.
     */
    @Test
    public void testFindUnitByUserId_NoLeaseFound_ThrowsException() throws Exception {
        // Mock the repository methods to return empty for leaseId
        when(residentRepository.findLeaseIdByResidentId(residentId)).thenReturn(Optional.empty());

        // Assert that the service method throws a Exception
        assertThrows(Exception.class, () -> {
            residentDataService.findUnitByUserId(residentId);
        });

        // Verify that the repository methods were called with the correct arguments
        verify(residentRepository).findLeaseIdByResidentId(residentId);
    }

    /**
     * Test to verify that findUnitByUserId throws an exception when no unit ID is
     * found for the lease.
     */
    @Test
    public void testFindUnitByUserId_NoUnitFound_ThrowsException() throws Exception {
        // Mock the repository methods to return the leaseId but no unitId
        when(residentRepository.findLeaseIdByResidentId(residentId)).thenReturn(Optional.of(leaseId));
        when(leaseRepository.findUnitByLeaseId(leaseId)).thenReturn(Optional.empty());

        // Assert that the service method throws a Exception
        assertThrows(Exception.class, () -> {
            residentDataService.findUnitByUserId(residentId);
        });

        // Verify that the repository methods were called with the correct arguments
        verify(residentRepository).findLeaseIdByResidentId(residentId);
        verify(leaseRepository).findUnitByLeaseId(leaseId);
    }

    // Test case for successful login with valid email and password.
    @Test
    public void testFindResidentByEmail_Success() throws CredentialException {
        // Mock the repository to return a valid UUID when correct credentials are
        // provided.
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.of(user));

        // Call the service method with valid credentials and capture the result.
        UserEntity result = residentDataService.findResidentByEmailAndPassword(validEmail, validPassword);

        // Assert that the returned UUID is not null, indicating a successful login.
        assertNotNull(result);
        // Assert that the returned UUID matches the expected valid ID.
        assertEquals(user, result);

        // Verify that the repository method was called exactly once with the correct
        // parameters.
        verify(userRepository, times(1)).findByEmail(validEmail);
    }

    // Test case for invalid credentials (i.e., wrong email or password).
    @Test
    public void testFindResidentByEmail_InvalidCredentials() {
        // Mock the repository to return an empty Optional when invalid credentials are
        // provided.
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.empty());

        // Assert that the service method throws a CredentialException when no matching
        // resident is found.
        assertThrows(CredentialException.class, () -> {
            residentDataService.findResidentByEmailAndPassword(validEmail, validPassword);
        });

        // Verify that the repository method was called exactly once with the correct
        // parameters.
        verify(userRepository, times(1)).findByEmail(validEmail);
    }

    // Test case for an empty email provided in the login attempt.
    @Test
    public void testFindResidentByEmail_EmptyEmail() throws CredentialException {
        // Simulate an empty email and mock the repository to return an empty Optional.
        String emptyEmail = "";
        when(userRepository.findByEmail(emptyEmail)).thenReturn(Optional.empty());

        // Assert that the service method throws a CredentialException when the email is
        // empty.
        assertThrows(CredentialException.class, () -> {
            residentDataService.findResidentByEmailAndPassword(emptyEmail, validPassword);
        });

        // Verify that the repository method was called exactly once with the empty
        // email.
        verify(userRepository, times(1)).findByEmail(emptyEmail);
    }

    /**
     * Test to verify that findUnitById returns the correct UnitEntity when the unit
     * exists.
     */
    @Test
    public void testFindUnitById_UnitExists_ReturnsUnitEntity() {

        // Mock the repository method to return the mockUnitEntity
        when(unitRepository.findById(unitId)).thenReturn(Optional.of(mockUnit));

        // Act: Call the method under test
        UnitEntity result = residentDataService.findUnitById(unitId);

        // Assert: Verify that the result is the same as the mockUnitEntity
        assertNotNull(result); // Ensure the result is not null

        // Verify that the findById method was called with the correct unitId
        verify(unitRepository).findById(unitId);
    }

    /**
     * Test to verify that findUnitById throws NoSuchElementException when the unit
     * does not exist.
     */
    @Test
    public void testFindUnitById_UnitNotFound_ThrowsNoSuchElementException() {
        // Mock the repository method to return an empty Optional (simulating that the
        // unit does not exist)
        when(unitRepository.findById(unitId)).thenReturn(Optional.empty());

        // Assert that the service method throws a Exception
        assertThrows(Exception.class, () -> {
            residentDataService.findUnitById(unitId);
        });

        // Verify that the findById method was called with the correct unitId
        verify(unitRepository).findById(unitId);
    }

    /**
     * Tests getBuildingIdByAddressAndName success case.
     * Simulates valid address and name, expecting a UUID when the building is
     * found.
     */
    @Test
    public void testGetBuildingIdByAddressAndNameSuccess() throws Exception {
        // Arrange: Mock buildingRepository to return an Optional containing a UUID.
        when(buildingRepository.findByAddressAndName(address, name))
                .thenReturn(Optional.of(buildingId));

        // Act: Call getBuildingIdByAddressAndName with valid address and name.
        UUID result = residentDataService.getBuildingIdByAddressAndName(address, name);

        // Assert: Verify the returned UUID matches the expected building ID.
        assertNotNull(result, "Building ID should not be null");
        assertEquals(buildingId, result, "Building ID should match");
    }

    /**
     * Tests getBuildingIdByAddressAndName failure case when building is not found.
     * Simulates invalid address and name, expecting an Exception to be thrown.
     */
    @Test
    public void testGetBuildingIdByAddressAndNameNotFound() {
        // Arrange: Mock buildingRepository to return an empty Optional.
        when(buildingRepository.findByAddressAndName(address, name))
                .thenReturn(Optional.empty());

        // Act & Assert: Expect Exception when building is not found.
        Exception exception = assertThrows(Exception.class,
                () -> residentDataService.getBuildingIdByAddressAndName(address, name),
                "Should throw Exception when building is not found");
        assertEquals("Building does not exist with given street and address.", exception.getMessage(),
                "Exception message should match");
    }

    /**
     * Tests getLeaseId success case.
     * Simulates valid inputs, expecting a UUID when the lease is found.
     */
    @Test
    public void testGetLeaseIdSuccess() throws Exception {
        // Arrange: Mock leaseRepository to return an Optional containing a UUID.
        when(leaseRepository.findLeaseId(address, mockBuilding.getName(), mockUnit.getFloor(), mockUnit.getRoom(),
                startDate.toString(), endDate.toString()))
                .thenReturn(Optional.of(leaseId));

        // Act: Call getLeaseId with valid inputs.
        UUID result = residentDataService.getLeaseId(address, mockBuilding.getName(), mockUnit.getFloor(),
                mockUnit.getRoom(), startDate.toString(), endDate.toString());

        // Assert: Verify the returned UUID matches the expected lease ID.
        assertNotNull(result, "Lease ID should not be null");
        assertEquals(leaseId, result, "Lease ID should match");
    }

    /**
     * Tests getLeaseId failure case when lease is not found.
     * Simulates invalid inputs, expecting an Exception to be thrown.
     */
    @Test
    public void testGetLeaseIdNotFound() {
        // Arrange: Mock leaseRepository to return an empty Optional.
        when(leaseRepository.findLeaseId(address, mockBuilding.getName(), mockUnit.getFloor(), mockUnit.getRoom(),
                startDate.toString(), endDate.toString()))
                .thenReturn(Optional.empty());

        // Act & Assert: Expect Exception when lease is not found.
        Exception exception = assertThrows(Exception.class,
                () -> residentDataService.getLeaseId(address, mockBuilding.getName(), mockUnit.getFloor(),
                        mockUnit.getRoom(), startDate.toString(), endDate.toString()),
                "Should throw Exception when lease is not found");
        assertEquals("Lease not found.", exception.getMessage(),
                "Exception message should match");
    }

    /**
     * Tests getResidentName success case with isFullName = true.
     * Simulates a valid residentId, expecting the full name (firstName + lastName).
     */
    @Test
    public void testGetResidentNameFullNameSuccess() {
        // Arrange: Mock jdbcTemplateObject.query to return a list with the
        // ResidentEntity.
        String expectedSql = "SELECT * FROM \"Resident\" WHERE resident_id = '" + residentId + "'";
        when(jdbcTemplateObject.query(eq(expectedSql), any(ResidentMapper.class)))
                .thenReturn(List.of(mockResident));

        // Act: Call getResidentName with isFullName = true.
        String result = residentDataService.getResidentName(residentId, true);

        // Assert: Verify the returned name is the full name.
        assertEquals("Grace Radlund", result, "Should return full name");
    }

    /**
     * Tests getResidentName success case with isFullName = false.
     * Simulates a valid residentId, expecting only the first name.
     */
    @Test
    public void testGetResidentNameFirstNameSuccess() {
        // Arrange: Mock jdbcTemplateObject.query to return a list with the
        // ResidentEntity.
        String expectedSql = "SELECT * FROM \"Resident\" WHERE resident_id = '" + residentId + "'";
        when(jdbcTemplateObject.query(eq(expectedSql), any(ResidentMapper.class)))
                .thenReturn(List.of(mockResident));

        // Act: Call getResidentName with isFullName = false.
        String result = residentDataService.getResidentName(residentId, false);

        // Assert: Verify the returned name is the first name.
        assertEquals("Grace", result, "Should return first name");
    }

    /**
     * Tests getResidentName failure case when resident is not found.
     * Simulates an invalid residentId, expecting a NoSuchElementException from
     * getResidentEntity.
     */
    @Test
    public void testGetResidentNameNotFound() {
        // Arrange: Mock jdbcTemplateObject.query to return an empty list.
        String expectedSql = "SELECT * FROM \"Resident\" WHERE resident_id = '" + residentId + "'";
        when(jdbcTemplateObject.query(eq(expectedSql), any(ResidentMapper.class)))
                .thenReturn(Collections.emptyList());

        // Act & Assert: Expect NoSuchElementException when resident is not found.
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> residentDataService.getResidentName(residentId, true),
                "Should throw NoSuchElementException when resident is not found");
        assertEquals("Problem retrieving resident.", exception.getMessage(),
                "Exception message should match");
    }

    /**
     * Test to verify that updateFriendship updates the friendship when it exists.
     */
    @Test
    public void testUpdateFriendship_FriendshipExists_UpdatesFriendship() throws Exception {
        // Mock the repository to return the mock friendship when queried by the
        // invitorId and inviteeId
        when(friendshipRepository.findByResidentIdAndNeighborId(invitorId, inviteeId))
                .thenReturn(Optional.of(mockFriendship));

        // Mock the repository's save method to return the updated friendship
        when(friendshipRepository.save(mockFriendship)).thenReturn(mockFriendship);

        // Act: Call the method under test with isAccepted set to true
        FriendshipEntity updatedFriendship = residentDataService.updateFriendship(invitorId, inviteeId, true);

        // Assert: Verify that the friendship is updated and returned correctly
        assertNotNull(updatedFriendship); // Ensure the updatedFriendship is not null
        assertTrue(updatedFriendship.isAccepted()); // Verify that the accepted field is updated to true

        // Verify that the findByResidentIdAndNeighborId and save methods were called
        // with the correct parameters
        verify(friendshipRepository).findByResidentIdAndNeighborId(invitorId, inviteeId);
        verify(friendshipRepository).save(mockFriendship);
    }

    /**
     * Test to verify that updateFriendship throws an exception when the friendship
     * does not exist.
     */
    @Test
    public void testUpdateFriendship_FriendshipNotFound_ThrowsException() throws Exception {
        // Mock the repository to return an empty Optional (indicating no existing
        // friendship)
        when(friendshipRepository.findByResidentIdAndNeighborId(invitorId, inviteeId)).thenReturn(Optional.empty());

        // Assert that the service method throws a Exception
        assertThrows(Exception.class, () -> {
            residentDataService.updateFriendship(invitorId, inviteeId, true);
        });

        // Verify that the findByResidentIdAndNeighborId method was called with the
        // correct parameters
        verify(friendshipRepository).findByResidentIdAndNeighborId(invitorId, inviteeId);
    }

}
