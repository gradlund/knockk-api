// Grace Radlund
// 12-15-2024
// Tests generated with the help of ChatGPT 4o mini
package com.knockk.api.data.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.knockk.api.data.Gender;
import com.knockk.api.data.repository.AdminRepository;
import com.knockk.api.data.repository.BuildingRepository;
import com.knockk.api.data.repository.ResidentRepository;
import com.knockk.api.data.repository.UserRepository;
import com.knockk.api.entity.AdminEntity;
import com.knockk.api.entity.AdminResidentEntity;
import com.knockk.api.entity.BuildingEntity;

import javax.security.auth.login.CredentialException;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

class AdminDataServiceTests {

    // Mock the AdminRepository, which is the dependency of AdminDataService.
    @Mock
    private AdminRepository adminRepository;

    @Mock
    private BuildingRepository buildingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ResidentRepository residentRepository;

    // The service to test with mocks injected
    @InjectMocks
    private AdminDataService adminDataService;

    private UUID adminId;
    private UUID buildingId;
    private BuildingEntity building;
    private BuildingEntity building2;
    private List<BuildingEntity> buildingEntities;
    private UUID residentId;
    private AdminResidentEntity residentEntity;
    private AdminResidentEntity residentEntity2;
    private List<AdminResidentEntity> residentEntities;
    private Pageable pageable;
    private boolean verified;

    @BeforeEach
    void setUp() {
        // Initialize mocks before each test method.
        MockitoAnnotations.openMocks(this);
        // No need to manually create adminDataService here; @InjectMocks handles it

        // Inject mock into service.
        // adminDataService = new AdminDataService(adminRepository, buildingRepository,
        // userRepository, residentRepository);

        adminId = UUID.randomUUID();
        residentId = UUID.randomUUID();
        building = new BuildingEntity(buildingId, "Encanto", "3300 W Camelback Road", 400, 6, 1,
                new ArrayList<>(List.of(10, 30)), new ArrayList<>(List.of(20)), adminId);
        building2 = new BuildingEntity(UUID.randomUUID(), "Papago", "3300 W Camelback Road", 600, 7, 1,
                new ArrayList<>(List.of(10, 30, 50)), new ArrayList<>(List.of(20, 70)), adminId);
        buildingEntities = List.of(building);
        buildingId = UUID.randomUUID();
        residentEntity = new AdminResidentEntity(buildingId, residentId, "John", "Doe", Gender.Male,
                "johndoe@gmail.com", 3, 23,
                new Date(11 / 11 / 2025), new Date(11 / 11 / 2026), false);
        residentEntity2 = new AdminResidentEntity(buildingId, UUID.randomUUID(), "John", "Deer", Gender.Male,
                "johndeer@gmail.com",
                4, 33, new Date(11 / 11 / 2025), new Date(11 / 11 / 2026), false);
        residentEntities = List.of(residentEntity, residentEntity2);
        verified = true;
        pageable = PageRequest.of(0, 10, Sort.by("floor").ascending());
    }

    @Test
    public void testActivateResident_Success() throws Exception {
        // Arrange
        when(residentRepository.activate(residentId)).thenReturn(1);

        // Act
        Boolean result = adminDataService.activateResident(residentId);

        // Assert
        assertTrue(result);
        verify(residentRepository, times(1)).activate(residentId);
    }

    @Test
    public void testActivateResident_NoRowsUpdated_ThrowsException() {
        // Arrange
        UUID residentId = UUID.randomUUID();
        when(residentRepository.activate(residentId)).thenReturn(0);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            adminDataService.activateResident(residentId);
        });

        assertEquals("Error updating resident.", exception.getMessage());
        verify(residentRepository, times(1)).activate(residentId);
    }

    @Test
    public void testDeleteResident_Success() throws Exception {
        // Arrange
        UUID residentId = UUID.randomUUID();
        when(residentRepository.findResidentById(residentId)).thenReturn(Optional.empty());
        doNothing().when(residentRepository).deleteById(residentId);
        doNothing().when(userRepository).deleteById(residentId);

        // Act
        Boolean result = adminDataService.deleteResident(residentId);

        // Assert
        assertTrue(result);
        verify(residentRepository, times(1)).deleteById(residentId);
        verify(userRepository, times(1)).deleteById(residentId);
        verify(residentRepository, times(1)).findResidentById(residentId);
    }

    @Test
    public void testDeleteResident_ResidentStillExists_ThrowsException() throws Exception {
        // Arrange
        when(residentRepository.findResidentById(residentId)).thenReturn(Optional.of(residentEntity));
        doNothing().when(residentRepository).deleteById(residentId);
        doNothing().when(userRepository).deleteById(residentId);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            adminDataService.deleteResident(residentId);
        });

        assertEquals("Did not delete.", exception.getMessage());
        verify(residentRepository, times(1)).deleteById(residentId);
        verify(userRepository, times(1)).deleteById(residentId);
        verify(residentRepository, times(1)).findResidentById(residentId);
    }

    @Test
    public void testDeleteResident_ResidentRepositoryThrowsException() {
        // Arrange
        doThrow(new RuntimeException("Database error")).when(residentRepository).deleteById(residentId);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            adminDataService.deleteResident(residentId);
        });

        verify(residentRepository, times(1)).deleteById(residentId);
        verify(userRepository, never()).deleteById(residentId); // Should not reach this call
        verify(residentRepository, never()).findResidentById(residentId); // Should not reach this call
    }

    @Test
    public void testDeleteResident_UserRepositoryThrowsException() throws Exception {
        // Arrange
        UUID residentId = UUID.randomUUID();
        doNothing().when(residentRepository).deleteById(residentId);
        doThrow(new RuntimeException("Database error")).when(userRepository).deleteById(residentId);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            adminDataService.deleteResident(residentId);
        });

        verify(residentRepository, times(1)).deleteById(residentId);
        verify(userRepository, times(1)).deleteById(residentId);
        verify(residentRepository, never()).findResidentById(residentId); // Should not reach this call
    }

    @Test
    public void testDeleteResident_RaceConditionSimulation() throws Exception {
        // Simulate race condition: deletions occur, but findResidentById returns a
        // resident
        doNothing().when(residentRepository).deleteById(residentId);
        doNothing().when(userRepository).deleteById(residentId);
        when(residentRepository.findResidentById(residentId)).thenReturn(Optional.of(residentEntity)); // Resident
                                                                                                       // reappears

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            adminDataService.deleteResident(residentId);
        });

        assertEquals("Did not delete.", exception.getMessage());
        verify(residentRepository, times(1)).deleteById(residentId);
        verify(userRepository, times(1)).deleteById(residentId);
        verify(residentRepository, times(1)).findResidentById(residentId);
    }

    // Test case for a successful login with valid credentials.
    @Test
    void testFindAdminByUsernameAndPassword_Success() throws CredentialException {

        // Sample valid credentials for the test.
        String username = "adminUser";
        String password = "adminPass";

        // Mock the expected result when the repository is called with valid
        // credentials.
        UUID expectedUuid = UUID.randomUUID(); // Generate a mock UUID for the successful login.

        // Mock the repository method to return the expected UUID when valid credentials
        // are passed.
        when(adminRepository.findByUsernameAndPassword(username, password))
                .thenReturn(Optional.of(expectedUuid));

        // Call the service method with valid credentials and capture the returned admin entity.
        AdminEntity adminEntity = adminDataService.findAdminByUsername(username);

        // Assert that the returned UUID is not null (i.e., login was successful).
        assertNotNull(adminEntity);

        // Verify that the repository method was called exactly once with the correct
        // parameters.
        verify(adminRepository, times(1)).findByUsername(username);
    }

    // Test case for invalid credentials (should throw CredentialException).
    @Test
    void testFindAdminByUsernameAndPassword_InvalidCredentials() {

        // Sample invalid credentials for the test.
        String username = "wrongUser";
        String password = "wrongPassword";

        // Mock the repository method to return an empty Optional (no admin found with
        // these credentials).
        when(adminRepository.findByUsernameAndPassword(username, password))
                .thenReturn(Optional.empty());

        // Assert that the service method throws a CredentialException with the expected
        // message.
        CredentialException thrown = assertThrows(CredentialException.class, () -> {
            adminDataService.findAdminByUsername(username);
        });

        // Assert that the exception message matches the expected "Invalid credentials."
        // message.
        assertEquals("Invalid credentials.", thrown.getMessage());

        // Verify that the repository method was called exactly once with the correct
        // parameters.
        verify(adminRepository, times(1)).findByUsernameAndPassword(username, password);
    }

    // Test case for unexpected repository errors (e.g., database failure).
    @Test
    void testFindAdminByUsernameAndPassword_RepositoryError() {

        // Sample valid credentials for the test.
        String username = "adminUser";
        String password = "adminPass";

        // Mock the repository method to throw a RuntimeException (simulating a database
        // error).
        when(adminRepository.findByUsernameAndPassword(username, password))
                .thenThrow(new RuntimeException("Database error"));

        // Assert that the service method throws the expected RuntimeException.
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            adminDataService.findAdminByUsername(username);
        });

        // Assert that the exception message matches the expected "Database error"
        // message.
        assertEquals("Database error", thrown.getMessage());

        // Verify that the repository method was called exactly once with the correct
        // parameters.
        verify(adminRepository, times(1)).findByUsernameAndPassword(username, password);
    }

    @Test
    public void testFindBuildingsByAdminId_Success_SingleBuilding() throws Exception {
        // Arrange
        when(buildingRepository.findAllByAdminId(adminId)).thenReturn(buildingEntities);

        // Act
        List<BuildingEntity> result = adminDataService.findBuildingsByAdminId(adminId);

        // Assert
        assertEquals(1, result.size());
        assertEquals(building, result.get(0));
        verify(buildingRepository, times(1)).findAllByAdminId(adminId);
    }

    @Test
    public void testFindBuildingsByAdminId_Success_MultipleBuildings() throws Exception {
        // Arrange
        List<BuildingEntity> buildings = List.of(building, building2);
        when(buildingRepository.findAllByAdminId(adminId)).thenReturn(buildings);

        // Act
        List<BuildingEntity> result = adminDataService.findBuildingsByAdminId(adminId);

        // Assert
        assertEquals(2, result.size());
        assertEquals(building, result.get(0));
        assertEquals(building2, result.get(1));
        verify(buildingRepository, times(1)).findAllByAdminId(adminId);
    }

    @Test
    public void testFindBuildingsByAdminId_NoBuildings_ThrowsException() {
        // Arrange;
        when(buildingRepository.findAllByAdminId(adminId)).thenReturn(Collections.emptyList());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            adminDataService.findBuildingsByAdminId(adminId);
        });

        assertEquals("Not Found. No buildings found.", exception.getMessage());
        verify(buildingRepository, times(1)).findAllByAdminId(adminId);
    }

    @Test
    public void testFindBuildingsByAdminId_NullAdminId_ThrowsException() {
        // Arrange
        when(buildingRepository.findAllByAdminId(null)).thenReturn(Collections.emptyList());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            adminDataService.findBuildingsByAdminId(adminId);
        });

        assertEquals("Not Found. No buildings found.", exception.getMessage());
        verify(buildingRepository, times(0)).findAllByAdminId(null);
    }

    @Test
    public void testFindBuildingsByAdminId_RepositoryThrowsException() {
        // Arrange
        when(buildingRepository.findAllByAdminId(adminId)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            adminDataService.findBuildingsByAdminId(adminId);
        });

        verify(buildingRepository, times(1)).findAllByAdminId(adminId);
    }

    @Test
    public void testFindResident_Success() throws Exception {
        // Arrange: Set up a valid resident ID and mock resident entity
        when(residentRepository.findResidentById(residentId)).thenReturn(Optional.of(residentEntity));

        // Act: Call the method with the valid resident ID
        AdminResidentEntity result = adminDataService.findResident(residentId);

        // Assert: Verify the returned resident matches the mock and repository was
        // called
        assertEquals(residentEntity, result);
        verify(residentRepository, times(1)).findResidentById(residentId);
    }

    @Test
    public void testFindResident_NotFound_ThrowsException() {
        // Arrange: Set up a resident ID that doesn't exist
        when(residentRepository.findResidentById(residentId)).thenReturn(Optional.empty());

        // Act & Assert: Expect an exception when no resident is found
        Exception exception = assertThrows(Exception.class, () -> {
            adminDataService.findResident(residentId);
        });

        // Verify the exception message and repository call
        assertEquals("Not found. No resident found.", exception.getMessage());
        verify(residentRepository, times(1)).findResidentById(residentId);
    }

    @Test
    public void testFindResident_NullResidentId_ThrowsException() {
        // Arrange: Test with a null resident ID
        UUID residentId = null;
        when(residentRepository.findResidentById(null)).thenReturn(Optional.empty());

        // Act & Assert: Expect an exception for null input (assuming repository returns
        // empty)
        Exception exception = assertThrows(Exception.class, () -> {
            adminDataService.findResident(residentId);
        });

        // Verify the exception message and repository call
        assertEquals("Not found. No resident found.", exception.getMessage());
        verify(residentRepository, times(1)).findResidentById(null);
    }

    @Test
    public void testFindResident_RepositoryThrowsException() {
        // Arrange: Simulate a repository failure
        when(residentRepository.findResidentById(residentId)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert: Expect the runtime exception to propagate
        assertThrows(RuntimeException.class, () -> {
            adminDataService.findResident(residentId);
        });

        // Verify the repository was called once
        verify(residentRepository, times(1)).findResidentById(residentId);
    }

    @Test
    public void testFindResidents_DefaultSort_Success() throws Exception {
        // Arrange: Set up a building ID, verified status, and default sort (empty sort
        // string)
        boolean verified = true;
        Pageable pageable = PageRequest.of(0, 10); // No sort specified, defaults to lastName ASC
        when(residentRepository.findAllByBuildingIdAndVerificationSortByLastName(buildingId, verified, 10, 0L))
                .thenReturn(residentEntities);

        // Act: Call the method with no sort specified
        List<AdminResidentEntity> result = adminDataService.findResidents(buildingId, verified, pageable);

        // Assert: Verify the result and repository call
        assertEquals(2, result.size());
        assertEquals(residentEntity, result.get(0));
        verify(residentRepository, times(1)).findAllByBuildingIdAndVerificationSortByLastName(buildingId, verified, 10,
                0L);
        verifyNoMoreInteractions(residentRepository);
    }

    @Test
    public void testFindResidents_SortByLastNameAsc_Success() throws Exception {
        // Arrange: Set up with explicit lastName ASC sort
        Pageable pageable = PageRequest.of(0, 5, Sort.by("lastName").ascending());
        when(residentRepository.findAllByBuildingIdAndVerificationSortByLastName(buildingId, verified, 5, 0L))
                .thenReturn(residentEntities);

        // Act: Call the method with lastName ASC sort
        List<AdminResidentEntity> result = adminDataService.findResidents(buildingId, verified, pageable);

        // Assert: Verify the result and correct repository method was called
        assertEquals(2, result.size());
        assertEquals(residentEntity, result.get(0));
        verify(residentRepository, times(1)).findAllByBuildingIdAndVerificationSortByLastName(buildingId, verified, 5,
                0L);
        verifyNoMoreInteractions(residentRepository);
    }

    @Test
    public void testFindResidents_SortByLastNameDesc_Success() throws Exception {
        // Arrange: Set up with lastName DESC sort
        boolean verified = true;
        Pageable pageable = PageRequest.of(1, 10, Sort.by("lastName").descending());
        when(residentRepository.findAllByBuildingIdAndVerificationSortByLastNameDesc(buildingId, verified, 10, 10L))
                .thenReturn(residentEntities);

        // Act: Call the method with lastName DESC sort
        List<AdminResidentEntity> result = adminDataService.findResidents(buildingId, verified, pageable);

        // Assert: Verify the result and correct repository method was called
        assertEquals(2, result.size());
        assertEquals(residentEntity, result.get(0));
        verify(residentRepository, times(1)).findAllByBuildingIdAndVerificationSortByLastNameDesc(buildingId, verified,
                10, 10L);
        verifyNoMoreInteractions(residentRepository);
    }

    @Test
    public void testFindResidents_SortByFloorAsc_Success() throws Exception {
        // Arrange: Set up with floor ASC sort
        Pageable pageable = PageRequest.of(0, 20, Sort.by("floor").ascending());
        when(residentRepository.findAllByBuildingIdAndVerificationSortByFloor(buildingId, verified, 20, 0L))
                .thenReturn(residentEntities);

        // Act: Call the method with floor ASC sort
        List<AdminResidentEntity> result = adminDataService.findResidents(buildingId, verified, pageable);

        // Assert: Verify the result and correct repository method was called
        assertEquals(2, result.size());
        assertEquals(residentEntity, result.get(0));
        verify(residentRepository, times(1)).findAllByBuildingIdAndVerificationSortByFloor(buildingId, verified, 20,
                0L);
        verifyNoMoreInteractions(residentRepository);
    }

    @Test
    public void testFindResidents_SortByFloorDesc_Success() throws Exception {
        // Arrange: Set up with floor DESC sort
        boolean verified = true;
        Pageable pageable = PageRequest.of(0, 15, Sort.by("floor").descending());
        when(residentRepository.findAllByBuildingIdAndVerificationSortByFloorDesc(buildingId, verified, 15, 0L))
                .thenReturn(residentEntities);

        // Act: Call the method with floor DESC sort
        List<AdminResidentEntity> result = adminDataService.findResidents(buildingId, verified, pageable);

        // Assert: Verify the result and correct repository method was called
        assertEquals(2, result.size());
        assertEquals(residentEntity, result.get(0));
        verify(residentRepository, times(1)).findAllByBuildingIdAndVerificationSortByFloorDesc(buildingId, verified, 15,
                0L);
        verifyNoMoreInteractions(residentRepository);
    }

    @Test
    public void testFindResidents_InvalidSort_DefaultsToBasicQuery_Success() throws Exception {
        // Arrange: Set up with an invalid sort (e.g., "invalidField")
        Pageable pageable = PageRequest.of(0, 10, Sort.by("invalidField").ascending());

        when(residentRepository.findAllByBuildingIdAndVerification(buildingId, verified, 10, 0L))
                .thenReturn(residentEntities);

        // Act: Call the method with an invalid sort
        List<AdminResidentEntity> result = adminDataService.findResidents(buildingId, verified, pageable);

        // Assert: Verify it falls back to default query and returns results
        assertEquals(2, result.size());
        assertEquals(residentEntity, result.get(0));
        verify(residentRepository, times(1)).findAllByBuildingIdAndVerification(buildingId, verified, 10, 0L);
        verifyNoMoreInteractions(residentRepository);
    }

    @Test
    public void testFindResidents_NoResidents_ThrowsException() {
        // Arrange: Set up with no residents returned
        boolean verified = true;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("lastName").ascending());
        when(residentRepository.findAllByBuildingIdAndVerificationSortByLastName(buildingId, verified, 10, 0L))
                .thenReturn(Collections.emptyList());

        // Act & Assert: Expect an exception when no residents are found
        Exception exception = assertThrows(Exception.class, () -> {
            adminDataService.findResidents(buildingId, verified, pageable);
        });

        // Verify the exception message and repository call
        assertEquals("Not found. No residents found.", exception.getMessage());
        verify(residentRepository, times(1)).findAllByBuildingIdAndVerificationSortByLastName(buildingId, verified, 10,
                0L);
        verifyNoMoreInteractions(residentRepository);
    }

    @Test
    public void testFindResidents_RepositoryThrowsException() {
        // Arrange: Simulate a repository failure
        when(residentRepository.findAllByBuildingIdAndVerificationSortByFloor(buildingId, verified, 10, 0L))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert: Expect the runtime exception to propagate
        assertThrows(RuntimeException.class, () -> {
            adminDataService.findResidents(buildingId, verified, pageable);
        });

        // Verify the repository was called once
        verify(residentRepository, times(1)).findAllByBuildingIdAndVerificationSortByFloor(buildingId, verified, 10,
                0L);
        verifyNoMoreInteractions(residentRepository);
    }

}