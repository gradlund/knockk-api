package com.knockk.api.data.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.knockk.api.data.repository.AdminRepository;

import javax.security.auth.login.CredentialException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class AdminDataServiceTests {

	 @Mock
	    private AdminRepository adminRepository; // Mock the AdminRepository

	    private AdminDataService adminDataService;

	    @BeforeEach
	    void setUp() {
	        MockitoAnnotations.openMocks(this); // Initialize mocks
	        adminDataService = new AdminDataService(adminRepository); // Inject mock into service
	    }

	    // Test for successful login with correct credentials
	    @Test
	    void testFindAdminByUsernameAndPassword_Success() throws CredentialException {
	        // Arrange
	        String username = "adminUser";
	        String password = "adminPass";
	        UUID expectedUuid = UUID.randomUUID(); // Mock UUID as the expected result

	        // Mock the call to AdminRepository
	        when(adminRepository.findByUsernameAndPassword(username, password))
	                .thenReturn(Optional.of(expectedUuid));

	        // Act
	        UUID actualUuid = adminDataService.findAdminByUsernameAndPassword(username, password);

	        // Assert
	        assertNotNull(actualUuid);  // Ensure we got a UUID back
	        assertEquals(expectedUuid, actualUuid);  // Ensure the returned UUID matches

	        // Verify that the repository method was called with the correct parameters
	        verify(adminRepository, times(1)).findByUsernameAndPassword(username, password);
	    }

	    // Test for invalid credentials (throws CredentialException)
	    @Test
	    void testFindAdminByUsernameAndPassword_InvalidCredentials() {
	        // Arrange
	        String username = "wrongUser";
	        String password = "wrongPassword";

	        // Mock the call to AdminRepository to return an empty Optional (no matching record)
	        when(adminRepository.findByUsernameAndPassword(username, password))
	                .thenReturn(Optional.empty());

	        // Act & Assert
	        CredentialException thrown = assertThrows(CredentialException.class, () -> {
	            adminDataService.findAdminByUsernameAndPassword(username, password);
	        });

	        assertEquals("Invalid credentials.", thrown.getMessage());  // Ensure the exception message is correct

	        // Verify that the repository method was called with the correct parameters
	        verify(adminRepository, times(1)).findByUsernameAndPassword(username, password);
	    }

	    // Test for unexpected repository errors (e.g., database failure)
	    @Test
	    void testFindAdminByUsernameAndPassword_RepositoryError() {
	        // Arrange
	        String username = "adminUser";
	        String password = "adminPass";

	        // Mock the call to AdminRepository to throw an exception
	        when(adminRepository.findByUsernameAndPassword(username, password))
	                .thenThrow(new RuntimeException("Database error"));

	        // Act & Assert
	        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
	            adminDataService.findAdminByUsernameAndPassword(username, password);
	        });

	        assertEquals("Database error", thrown.getMessage());  // Ensure the exception message is correct

	        // Verify that the repository method was called with the correct parameters
	        verify(adminRepository, times(1)).findByUsernameAndPassword(username, password);
	    }

}
