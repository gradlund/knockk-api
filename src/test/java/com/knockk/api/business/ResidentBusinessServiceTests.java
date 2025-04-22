// Grace Radlund
// 4-22-2024
// Tests generated with the help of ChatGPT 4o mini and Grok
package com.knockk.api.business;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;
import java.util.UUID;
import javax.security.auth.login.CredentialException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.knockk.api.data.repository.UserRepository;
import com.knockk.api.data.service.ResidentDataService;
import com.knockk.api.util.Gender;
import com.knockk.api.util.entity.BuildingEntity;
import com.knockk.api.util.entity.FriendshipEntity;
import com.knockk.api.util.entity.ResidentEntity;
import com.knockk.api.util.entity.UnitEntity;
import com.knockk.api.util.entity.UserEntity;
import com.knockk.api.util.model.FriendshipModel;
import com.knockk.api.util.model.LoginModel;
import com.knockk.api.util.model.NeighborRoomModel;
import com.knockk.api.util.model.OptionalResidentModel;
import com.knockk.api.util.model.RegisterModel;
import com.knockk.api.util.model.UnitResidentModel;
import com.knockk.api.util.model.UserModel;

/**
 * Class for testing the admin business service class
 */
public class ResidentBusinessServiceTests {

    // Mocking services, repository, and encoder
    @Mock
    private ResidentDataService dataService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ResidentBusinessService residentBusinessService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    // Create mock data
    private String validEmail;
    private String validPassword;
    private UserEntity userEntity;
    private String invalidEmail = "invaliduser@example.com";
    private String invalidPassword = "wrongpassword";
    private UUID invitorId;
    private UUID inviteeId;
    private UUID residentId;
    private UUID friendId;
    private UUID neighborId = UUID.randomUUID();
    private FriendshipEntity mockFriendshipEntity;
    private FriendshipModel mockFriendshipNotConnected;
    private UUID unitId = UUID.randomUUID();
    private UUID buildingId = UUID.randomUUID();

    private String street;
    private UUID buildingId1;
    private UUID buildingId2;
    private BuildingEntity building1;
    private BuildingEntity building2;

    private int floor = 2;
    private int room = 101;
    private UnitEntity mockUnitEntity = new UnitEntity(UUID.randomUUID(), floor, room, 500, buildingId);

    private UserModel validCredentials;
    private String encodedPassword;

    private RegisterModel validResident;

    @BeforeEach
    public void setUp() {
        // Initialize mocks before each test method
        MockitoAnnotations.openMocks(this);
        // Inject mock into the service
        residentBusinessService = new ResidentBusinessService(dataService,
                passwordEncoder);

        userEntity = new UserEntity(residentId, validEmail, validPassword);

        // Intilize data before each test that might change in the test
        invitorId = UUID.randomUUID();
        inviteeId = UUID.randomUUID();
        residentId = UUID.randomUUID();
        friendId = UUID.randomUUID();
        mockFriendshipNotConnected = new FriendshipModel(invitorId, inviteeId, false);
        mockFriendshipEntity = new FriendshipEntity(UUID.randomUUID(), null, invitorId, inviteeId, false);

        validEmail = "testuser@example.com";
        validPassword = "password123";
        validCredentials = new UserModel(validEmail, validPassword);
        encodedPassword = "$2a$10$encodedPassword";

        street = "123 Main St";
        buildingId1 = UUID.randomUUID();
        buildingId2 = UUID.randomUUID();
        building1 = new BuildingEntity(buildingId1, "Encanto", "3300 W Camelback Road", 400, 6, 1,
                new ArrayList<>(List.of(10, 30)), new ArrayList<>(List.of(20)), UUID.randomUUID());
        building2 = new BuildingEntity(buildingId2, "Papago", "3300 W Camelback Road", 600, 7, 1,
                new ArrayList<>(List.of(10, 30, 50)), new ArrayList<>(List.of(20, 70)), UUID.randomUUID());

        validResident = new RegisterModel(
                residentId.toString(),
                "Grace",
                "Radlund",
                "Female",
                UUID.randomUUID().toString(),
                21,
                "Sun Prairie",
                "Bio",
                "profile.jpg",
                "background.jpg",
                "ginsta",
                "gsnap",
                "gx",
                "gface");

    }

    /**
     * Tests getBuildings success case with multiple buildings.
     * Simulates a valid street, expecting a list of building names.
     */
    @Test
    public void testGetBuildingsSuccess() throws Exception {
        // Arrange: Mock dataService to return a list of BuildingEntity.
        when(dataService.findBuilding(eq(street)))
                .thenReturn(Arrays.asList(building1, building2));

        // Act: Call getBuildings with a valid street.
        List<String> result = residentBusinessService.getBuildings(street);

        // Assert: Verify the returned list contains the correct building names.
        assertNotNull(result, "Result list should not be null");
        assertEquals(2, result.size(), "Should return two building names");
        assertTrue(result.contains("Encanto"), "Should contain Encanto");
        assertTrue(result.contains("Papago"), "Should contain Papago");
    }

    /**
     * Tests getBuildings success case with no buildings.
     * Simulates a valid street with no buildings, expecting an empty list.
     */
    @Test
    public void testGetBuildingsEmptyList() throws Exception {
        // Arrange: Mock dataService to return an empty list.
        when(dataService.findBuilding(eq(street)))
                .thenReturn(Collections.emptyList());

        // Act: Call getBuildings with a valid street.
        List<String> result = residentBusinessService.getBuildings(street);

        // Assert: Verify the returned list is empty.
        assertNotNull(result, "Result list should not be null");
        assertTrue(result.isEmpty(), "Result list should be empty");
    }

    /**
     * Tests getBuildings failure case when no buildings are found.
     * Simulates an invalid street, expecting an Exception from dataService.
     */
    @Test
    public void testGetBuildingsNotFound() throws Exception {
        // Arrange: Mock dataService to throw an Exception.
        String invalidStreet = "999 Unknown St";
        when(dataService.findBuilding(eq(invalidStreet)))
                .thenThrow(new Exception("Invalid address (case sensitive)."));

        // Act & Assert: Expect Exception when no buildings are found.
        Exception exception = assertThrows(Exception.class,
                () -> residentBusinessService.getBuildings(invalidStreet),
                "Should throw Exception when no buildings are found");
        assertEquals("Invalid address (case sensitive).", exception.getMessage(),
                "Exception message should match");
    }

    /**
     * Tests createAccount success case with valid credentials.
     * Simulates a unique email, expecting a UUID from dataService.
     */
    @Test
    public void testCreateAccountSuccess() throws Exception {
        // Arrange: Mock passwordEncoder and dataService.
        when(passwordEncoder.encode(eq(validPassword)))
                .thenReturn(encodedPassword);
        when(dataService.createAccount(any(UserEntity.class)))
                .thenReturn(residentId);

        // Act: Call createAccount with valid credentials.
        UUID result = residentBusinessService.createAccount(validCredentials);

        // Assert: Verify the returned UUID matches the expected residentId.
        assertNotNull(result, "Resident ID should not be null");
        assertEquals(residentId, result, "Resident ID should match");
    }

    /**
     * Tests createAccount failure case with duplicate email.
     * Simulates an existing email, expecting an Exception from dataService.
     */
    @Test
    public void testCreateAccountDuplicateEmail() throws Exception {
        // Arrange: Mock passwordEncoder and dataService to throw an Exception.
        when(passwordEncoder.encode(eq(validPassword)))
                .thenReturn(encodedPassword);
        when(dataService.createAccount(any(UserEntity.class)))
                .thenThrow(new Exception("Email already exists: " + validEmail));

        // Act & Assert: Expect Exception when email is already in use.
        Exception exception = assertThrows(Exception.class,
                () -> residentBusinessService.createAccount(validCredentials),
                "Should throw Exception for duplicate email");
        assertEquals("Email already exists: " + validEmail, exception.getMessage(),
                "Exception message should match");
    }

    // Successful deletion of a friendship
    // This test case checks the scenario when a friendship is successfully deleted
    // from the data service
    @Test
    void testDeleteFriendship_Success() throws Exception {

        // Mock the data service's deleteFriendship method to return true, indicating
        // success
        when(dataService.deleteFriendship(residentId, friendId)).thenReturn(true);

        // Act
        boolean result = residentBusinessService.deleteFriendship(residentId, friendId);

        // Assert
        assertTrue(result); // Verify that the result is true, indicating success
    }

    // Test case 2: Deletion fails, data service returns false
    // This case simulates a failure to delete the friendship, perhaps due to a
    // non-existing friendship
    @Test
    void testDeleteFriendship_Failure() throws Exception {
        // Mock the data service's deleteFriendship method to return false, indicating
        // failure
        when(dataService.deleteFriendship(residentId, friendId)).thenReturn(false);

        // Act
        boolean result = residentBusinessService.deleteFriendship(residentId, friendId);

        // Assert
        assertFalse(result); // Verify that the result is false, indicating failure to delete
    }

    // Friendship does not exist (NoSuchElementException)
    // This case checks if the method correctly throws a NoSuchElementException when
    // the friendship doesn't exist.
    @Test
    void testGetFriendship_FriendshipNotFound() {
        // Mock the data service to return an empty Optional, indicating no friendship
        // found
        when(dataService.findFriendship(residentId, friendId)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            residentBusinessService.getFriendship(residentId, friendId);
        });

        // Assert: Verify that the exception message is as expected
        assertEquals("Not Found. Friendship does not exist.", exception.getMessage());
    }

    // Successfully updating a friendship status to not accepted
    // This case tests a scenario where the friendship status is successfully
    // updated to not accepted.
    @Test
    void testUpdateFriendship_Success_NotAccepted() throws Exception {
        // Mock the data service to return a FriendshipEntity with the updated
        // acceptance status
        when(dataService.updateFriendship(invitorId, inviteeId, false)).thenReturn(mockFriendshipEntity);

        // Act
        FriendshipModel result = residentBusinessService.updateFriendship(invitorId, inviteeId, false);

        // Assert
        assertNotNull(result); // Verify that the result is not null
        assertEquals(invitorId, result.getInvitorId()); // Check that the Invitor ID matches
        assertEquals(inviteeId, result.getInviteeId()); // Check that the Invitee ID matches
        assertFalse(result.isAccepted()); // Verify that the friendship is not accepted
    }

    /**
     * Tests login success case.
     * Simulates valid credentials, expecting a LoginModel with resident ID and
     * verification status.
     * 
     * @throws CredentialException
     */
    @Test
    public void testLoginSuccess() throws CredentialException {
        // Arrange: Mock dataService to return a UserEntity and verification status.
        when(dataService.findResidentByEmailAndPassword(validEmail, validPassword))
                .thenReturn(new UserEntity(residentId, validEmail, encodedPassword));
        when(passwordEncoder.matches(validPassword, encodedPassword))
                .thenReturn(true);
        when(dataService.checkVerified(residentId))
                .thenReturn(true);

        // Act: Call the login method with valid credentials.
        LoginModel result = residentBusinessService.login(new UserModel(validEmail, validPassword));

        // Assert: Verify the LoginModel contains the correct resident ID and verified
        // status.
        assertNotNull(result, "LoginModel should not be null");
        assertEquals(residentId, result.getId(), "Resident ID should match");
        assertTrue(result.isVerified(), "Verified status should be true");
    }

    // Test an unsuccessful login scenario
    @Test
    public void testLogin_InvalidCredentials() throws CredentialException {
        // Prepare invalid user
        UserModel invalidUser = new UserModel(invalidEmail, invalidPassword);

        // Mock the ResidentDataService to throw a CredentialException.
        when(dataService.findResidentByEmailAndPassword(invalidEmail, invalidPassword))
                .thenThrow(new CredentialException("Invalid credentials"));

        // Check that a CredentialException is thrown
        assertThrows(CredentialException.class, () -> {
            residentBusinessService.login(invalidUser);
        });

        // Verify that the dataService method was called once with the incorrect
        // credentials
        verify(dataService, times(1)).findResidentByEmailAndPassword(invalidEmail, invalidPassword);
    }

    // Successfully retrieving connected neighbors with profile photo
    // This case tests the scenario where the resident has neighbors that are
    // connected
    // and also have a profile photo.
    @Test
    void testGetNeighborResidents_Success_ConnectedWithProfilePhoto() {
        // Mock the data service to return a list of neighbor IDs
        List<UUID> neighborIds = List.of(neighborId);
        when(dataService.findNeighborsByUnit(floor, room)).thenReturn(neighborIds);

        // Mock the checkConnected method to return true, indicating the resident is
        // connected to the neighbor
        when(dataService.checkConnected(residentId, neighborId)).thenReturn(true);

        // Mock the getResidentName and getProfilePhoto methods for the connected
        // neighbor
        String neighborName = "John Doe";
        String profilePhoto = "no.jpg";
        when(dataService.getResidentName(neighborId, true)).thenReturn(neighborName);
        when(dataService.getProfilePhoto(neighborId)).thenReturn(profilePhoto);

        // Act
        ArrayList<UnitResidentModel> result = residentBusinessService.getNeighborResidents(residentId, floor, room);

        // Assert
        assertEquals(1, result.size()); // Verify that there is 1 resident in the result
        UnitResidentModel neighbor = result.get(0);
        assertEquals(neighborName, neighbor.getName()); // Check that the neighbor's name matches
        assertEquals(profilePhoto, neighbor.getProfilePhoto()); // Check that the profile photo matches
        assertTrue(neighbor.isConnected()); // Verify that the neighbor is connected
    }

    // No neighbors for the specified unit (empty list)
    // This case tests the scenario where there are no neighbors found for the given
    // floor and room.
    @Test
    void testGetNeighborResidents_NoNeighbors() {
        // Mock the data service to return an empty list of neighbors
        when(dataService.findNeighborsByUnit(floor, room)).thenReturn(new ArrayList<>());

        // Act
        ArrayList<UnitResidentModel> result = residentBusinessService.getNeighborResidents(residentId, floor, room);

        // Assert
        assertTrue(result.isEmpty()); // Verify that the result is an empty list since there are no neighbors
    }

    // Successfully retrieving all neighbors (up, right, below, left)
    // This case tests when all neighboring units are available in all directions.
    @Test
    void testGetNeighborUnits_AllNeighborsAvailable() throws Exception {
        // Mock the data service to retrieve unit ID and unit details
        when(dataService.findUnitByUserId(residentId)).thenReturn(unitId);
        when(dataService.findUnitById(unitId)).thenReturn(mockUnitEntity);

        // Mock the data service to simulate neighbors available in all directions
        when(dataService.checkNeighborFloor(buildingId, floor + 1)).thenReturn(true); // Neighbor above
        when(dataService.checkNeighborRoom(buildingId, room + 1)).thenReturn(true); // Neighbor right
        when(dataService.checkNeighborFloor(buildingId, floor - 1)).thenReturn(true); // Neighbor below
        when(dataService.checkNeighborRoom(buildingId, room - 1)).thenReturn(true); // Neighbor left

        // Act
        List<NeighborRoomModel> result = residentBusinessService.getNeighborUnits(residentId);

        // Assert
        assertEquals(4, result.size()); // All four neighbors should be found
        assertTrue(result.stream().anyMatch(n -> "top".equals(n.getDirection()))); // Top neighbor
        assertTrue(result.stream().anyMatch(n -> "right".equals(n.getDirection()))); // Right neighbor
        assertTrue(result.stream().anyMatch(n -> "below".equals(n.getDirection()))); // Below neighbor
        assertTrue(result.stream().anyMatch(n -> "left".equals(n.getDirection()))); // Left neighbor
    }

    // No neighbors available in any direction
    // This case tests when no neighbors are available in any direction.
    @Test
    void testGetNeighborUnits_NoNeighborsAvailable() throws Exception {
        // Mock the data service to retrieve unit ID and unit details
        when(dataService.findUnitByUserId(residentId)).thenReturn(unitId);
        when(dataService.findUnitById(unitId)).thenReturn(mockUnitEntity);

        // Mock the data service to simulate no neighbors in any direction
        when(dataService.checkNeighborFloor(buildingId, floor + 1)).thenReturn(false); // No neighbor above
        when(dataService.checkNeighborRoom(buildingId, room + 1)).thenReturn(false); // No neighbor right
        when(dataService.checkNeighborFloor(buildingId, floor - 1)).thenReturn(false); // No neighbor below
        when(dataService.checkNeighborRoom(buildingId, room - 1)).thenReturn(false); // No neighbor left

        // Act
        List<NeighborRoomModel> result = residentBusinessService.getNeighborUnits(residentId);

        // Assert
        assertTrue(result.isEmpty()); // No neighbors should be found
    }

    // Successfully updating a resident with all fields provided
    @Test
    void testUpdateResident_AllFieldsProvided() throws Exception {
        // Arrange
        OptionalResidentModel residentInfo = new OptionalResidentModel(
                25, "New York", "Biography", "profile.jpg", "background.jpg", "instagramHandle", "snapchatHandle",
                "Xhandle", "facebookHandle");

        ResidentEntity existingResident = new ResidentEntity(residentId, "Grace", "Radlund", Gender.Female, 22,
                "Chicago", "Old Bio", "oldProfile.jpg", "oldBackground.jpg", "oldInstagram", "oldSnapchat", "oldX",
                "oldFacebook", UUID.randomUUID(), true);
        when(dataService.findResidentById(residentId)).thenReturn(existingResident);
        // when(dataService.updateResident(existingResident)).thenReturn(existingResident);

        // Act
        boolean result = residentBusinessService.updateResident(residentId, residentInfo);

        // Assert
        assertTrue(result); // Assert that the update was successful
        assertEquals(25, existingResident.getAge()); // Assert that the age was updated
        assertEquals("New York", existingResident.getHometown()); // Assert that the hometown was updated
        assertEquals("Biography", existingResident.getBiography()); // Assert that the biography was updated
        assertEquals("profile.jpg", existingResident.getProfilePhoto()); // Assert that the profile photo was updated
        assertEquals("background.jpg", existingResident.getBackgroundPhoto()); // Assert that the background photo was
                                                                               // updated
        assertEquals("instagramHandle", existingResident.getInstagram()); // Assert that the instagram handle was
                                                                          // updated
        assertEquals("snapchatHandle", existingResident.getSnapchat()); // Assert that the snapchat handle was updated
        assertEquals("Xhandle", existingResident.getX()); // Assert that the X handle was updated
        assertEquals("facebookHandle", existingResident.getFacebook()); // Assert that the facebook handle was updated
    }

    // Successfully updating a resident with only some fields provided
    @Test
    void testUpdateResident_SomeFieldsProvided() throws Exception {
        // Arrange
        UUID residentId = UUID.randomUUID();
        OptionalResidentModel residentInfo = new OptionalResidentModel(
                0, "Los Angeles", null, null, "newBackground.jpg", null, "newSnapchat", null, null);

        ResidentEntity existingResident = new ResidentEntity(residentId, "Grace", "Radlund", Gender.Female, 22,
                "Chicago", "Old Bio", "oldProfile.jpg", "oldBackground.jpg", "oldInstagram", "oldSnapchat", "oldX",
                "oldFacebook", UUID.randomUUID(), true);
        when(dataService.findResidentById(residentId)).thenReturn(existingResident);
        // when(dataService.updateResident(existingResident)).thenReturn(existingResident);

        // Act
        boolean result = residentBusinessService.updateResident(residentId, residentInfo);

        // Assert
        assertTrue(result); // Assert that the update was successful
        assertEquals(22, existingResident.getAge()); // Age remains unchanged
        assertEquals("Los Angeles", existingResident.getHometown()); // Assert that the hometown was updated
        assertEquals("Old Bio", existingResident.getBiography()); // Biography remains unchanged
        assertEquals("oldProfile.jpg", existingResident.getProfilePhoto()); // Profile photo remains unchanged
        assertEquals("newBackground.jpg", existingResident.getBackgroundPhoto()); // Background photo updated
        assertEquals("oldInstagram", existingResident.getInstagram()); // Instagram remains unchanged
        assertEquals("newSnapchat", existingResident.getSnapchat()); // Snapchat handle updated
        assertEquals("oldX", existingResident.getX()); // X handle remains unchanged
        assertEquals("oldFacebook", existingResident.getFacebook()); // Facebook remains unchanged
    }

    // Updating with an empty residentInfo (no changes)
    @Test
    void testUpdateResident_EmptyResidentInfo() throws Exception {
        // Arrange
        UUID residentId = UUID.randomUUID();
        OptionalResidentModel residentInfo = new OptionalResidentModel(0, null, null, null, null, null, null, null,
                null);

        ResidentEntity existingResident = new ResidentEntity(residentId, "Grace", "Radlund", Gender.Female, 22,
                "Chicago", "Old Bio", "oldProfile.jpg", "oldBackground.jpg", "oldInstagram", "oldSnapchat", "oldX",
                "oldFacebook", UUID.randomUUID(), true);
        when(dataService.findResidentById(residentId)).thenReturn(existingResident);
        // when(dataService.updateResident(existingResident)).thenReturn(existingResident);

        // Act
        boolean result = residentBusinessService.updateResident(residentId, residentInfo);

        // Assert
        assertTrue(result); // Assert that the update was successful (no changes but the resident was still
                            // updated)
        assertEquals(22, existingResident.getAge()); // Age remains unchanged
        assertEquals("Chicago", existingResident.getHometown()); // Hometown remains unchanged
        assertEquals("Old Bio", existingResident.getBiography()); // Biography remains unchanged
        assertEquals("oldProfile.jpg", existingResident.getProfilePhoto()); // Profile photo remains unchanged
        assertEquals("oldBackground.jpg", existingResident.getBackgroundPhoto()); // Background photo remains unchanged
        assertEquals("oldInstagram", existingResident.getInstagram()); // Instagram remains unchanged
        assertEquals("oldSnapchat", existingResident.getSnapchat()); // Snapchat remains unchanged
        assertEquals("oldX", existingResident.getX()); // X handle remains unchanged
        assertEquals("oldFacebook", existingResident.getFacebook()); // Facebook remains unchanged
    }

    /**
     * Tests register success case with valid RegisterModel.
     * Simulates a valid resident, expecting true from dataService.
     */
    @Test
    public void testRegisterSuccess() throws Exception {
        // Arrange: Mock dataService to return true.
        when(dataService.createResident(any(ResidentEntity.class)))
                .thenReturn(true);

        // Act: Call register with valid RegisterModel.
        boolean result = residentBusinessService.register(validResident);

        // Assert: Verify the method returns true.
        assertTrue(result, "Should return true for successful registration");
    }

    /**
     * Tests register failure case with duplicate resident ID or lease ID.
     * Simulates an existing resident, expecting an Exception from dataService.
     */
    @Test
    public void testRegisterDuplicateResident() throws Exception {
        // Arrange: Mock dataService to throw an Exception.
        when(dataService.createResident(any(ResidentEntity.class)))
                .thenThrow(new Exception("Could not save the resident."));

        // Act & Assert: Expect Exception for duplicate resident.
        Exception exception = assertThrows(Exception.class,
                () -> residentBusinessService.register(validResident),
                "Should throw Exception for duplicate resident");
        assertEquals("Could not save the resident.", exception.getMessage(),
                "Exception message should match");
    }

}