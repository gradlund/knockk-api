// Grace Radlund
// 12-15-2024
// Tests generated with the help of ChatGPT 4o mini
package com.knockk.api.business;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;
import java.util.UUID;
import javax.security.auth.login.CredentialException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.knockk.api.data.repository.UserRepository;
import com.knockk.api.data.service.ResidentDataService;
import com.knockk.api.entity.FriendshipEntity;
import com.knockk.api.entity.UnitEntity;
import com.knockk.api.model.FriendshipModel;
import com.knockk.api.model.LoginModel;
import com.knockk.api.model.NeighborRoomModel;
import com.knockk.api.model.UnitResidentModel;
import com.knockk.api.model.UserModel;
import com.knockk.api.model.OptionalResidentModel;
import com.knockk.api.entity.ResidentEntity;
import com.knockk.api.data.Gender;

public class ResidentBusinessServiceTests {

    // Mocking the ResidentDataService
    @Mock
    private ResidentDataService dataService;

    // Mock the UserRepository
    @Mock
    private UserRepository userRepository;

    private ResidentBusinessService residentBusinessService;

    // Create mock data
    private String validEmail = "testuser@example.com";
    private String validPassword = "password123";
    private UUID validUUID = UUID.randomUUID();
    private String invalidEmail = "invaliduser@example.com";
    private String invalidPassword = "wrongpassword";
    private UUID invitorId;
    private UUID inviteeId;
    private UUID residentId;
    private UUID friendId;
    UUID neighborId = UUID.randomUUID();
    private FriendshipModel mockFriendshipNotConnected;
    private FriendshipEntity mockFriendshipEntity;
    private UUID unitId = UUID.randomUUID();
    private UUID buildingId = UUID.randomUUID();
    private int floor = 2;
    private int room = 101;
    UnitEntity mockUnitEntity = new UnitEntity(UUID.randomUUID(), floor, room, 500, buildingId);

    @BeforeEach
    public void setUp() {
        // Initialize mocks before each test method
        MockitoAnnotations.openMocks(this);
        // Inject mock into the service
        residentBusinessService = new ResidentBusinessService(dataService);

        // Intilize data before each test that might change in the test
        invitorId = UUID.randomUUID();
        inviteeId = UUID.randomUUID();
        residentId = UUID.randomUUID();
        friendId = UUID.randomUUID();
        mockFriendshipNotConnected = new FriendshipModel(invitorId, inviteeId, false);
        mockFriendshipEntity = new FriendshipEntity(UUID.randomUUID(), null, invitorId, inviteeId, false);
    }

    // Valid case where the friendship is created successfully
    // In this case, the service will call the data service, which will return a
    // valid FriendshipEntity
    @Test
    void testCreateFriendship_Success() throws Exception {
        // Mock the data service method
        when(dataService.createFriendship(invitorId, inviteeId)).thenReturn(mockFriendshipEntity);

        // Act
        FriendshipModel result = residentBusinessService.createFriendship(invitorId, inviteeId);

        // Assert
        assertNotNull(result); // Check that the result is not null
        assertEquals(invitorId, result.getInvitorId()); // Check that the Invitor ID matches
        assertEquals(inviteeId, result.getInviteeId()); // Check that the Invitee ID matches
        assertFalse(result.isAccepted()); // Check that the friendship is not accepted by default
    }

    // Case where DataService throws an exception, testing error handling
    // This simulates a failure when trying to create the friendship
    @Test
    void testCreateFriendship_DataServiceFailure() throws Exception {
        // Mock the data service to throw an exception
        when(dataService.createFriendship(invitorId, inviteeId)).thenThrow(new RuntimeException("Data service error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> residentBusinessService.createFriendship(invitorId, inviteeId));
        // Expecting a RuntimeException to be thrown due to the mocked failure
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

    // Successfully retrieving a friendship that exists
    // This case tests the scenario when the friendship exists and is returned as a
    // model.
    @Test
    void testGetFriendship_Success() {
        when(dataService.findFriendship(residentId, friendId)).thenReturn(Optional.of(mockFriendshipEntity));

        // Act
        FriendshipModel result = residentBusinessService.getFriendship(residentId, friendId);

        // Assert
        assertNotNull(result); // Verify that the result is not null
        assertEquals(residentId, result.getInvitorId()); // Check the invitorId in the model
        assertEquals(friendId, result.getInviteeId()); // Check the inviteeId in the model
        assertTrue(result.isAccepted()); // Check that the friendship is accepted
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

    // Successfully updating a friendship status
    // This case tests a scenario where the friendship status is successfully
    // updated to accepted.
    @Test
    void testUpdateFriendship_Success_Accepted() throws Exception {
        // Mock the data service to return a FriendshipEntity with the updated
        // acceptance status
        when(dataService.updateFriendship(invitorId, inviteeId, true)).thenReturn(mockFriendshipEntity);

        // Act
        FriendshipModel result = residentBusinessService.updateFriendship(invitorId, inviteeId, true);

        // Assert
        assertNotNull(result); // Verify that the result is not null
        assertEquals(invitorId, result.getInvitorId()); // Check that the Invitor ID matches
        assertEquals(inviteeId, result.getInviteeId()); // Check that the Invitee ID matches
        assertTrue(result.isAccepted()); // Verify that the friendship is accepted
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

    @Test
    public void testLogin_Success() throws CredentialException {
        // Create a valid UserModel
        UserModel validUser = new UserModel(validEmail, validPassword);

        // Mock the ResidentDataService to return the expect resident and verification
        when(dataService.findResidentByEmailAndPassword(validEmail, validPassword)).thenReturn(validUUID);
        when(dataService.checkVerified(validUUID)).thenReturn(true);

        // Call the login method
        LoginModel result = residentBusinessService.login(validUser);

        // Verify the returned UUID matches the mocked validUUID
        assertNotNull(result);
        assertEquals(validUUID, result.getId());

        // Verify that the dataService method was called once with the correct
        // credentials
        verify(dataService, times(1)).findResidentByEmailAndPassword(validEmail, validPassword);
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
        when(dataService.updateResident(existingResident)).thenReturn(existingResident);

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
        when(dataService.updateResident(existingResident)).thenReturn(existingResident);

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
        when(dataService.updateResident(existingResident)).thenReturn(existingResident);

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
}