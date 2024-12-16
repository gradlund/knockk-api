// Grace Radlund
// 12-15-2024
// Tests generated with the help of ChatGPT 4o mini
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

    // Mock the AdminRepository, which is the dependency of AdminDataService.
    @Mock
    private AdminRepository adminRepository;

    private AdminDataService adminDataService;

    @BeforeEach
    void setUp() {
        // Initialize mocks before each test method.
        MockitoAnnotations.openMocks(this);
        
        // Inject mock into service.
        adminDataService = new AdminDataService(adminRepository);
    }

    // Test case for a successful login with valid credentials.
    @Test
    void testFindAdminByUsernameAndPassword_Success() throws CredentialException {
        
        // Sample valid credentials for the test.
        String username = "adminUser";
        String password = "adminPass";
        
        // Mock the expected result when the repository is called with valid credentials.
        UUID expectedUuid = UUID.randomUUID(); // Generate a mock UUID for the successful login.
        
        // Mock the repository method to return the expected UUID when valid credentials are passed.
        when(adminRepository.findByUsernameAndPassword(username, password))
                .thenReturn(Optional.of(expectedUuid));
        
        // Call the service method with valid credentials and capture the returned UUID.
        UUID actualUuid = adminDataService.findAdminByUsernameAndPassword(username, password);
        
        // Assert that the returned UUID is not null (i.e., login was successful).
        assertNotNull(actualUuid);
        
        // Assert that the returned UUID matches the expected UUID.
        assertEquals(expectedUuid, actualUuid);
        
        // Verify that the repository method was called exactly once with the correct parameters.
        verify(adminRepository, times(1)).findByUsernameAndPassword(username, password);
    }

    // Test case for invalid credentials (should throw CredentialException).
    @Test
    void testFindAdminByUsernameAndPassword_InvalidCredentials() {
        
        // Sample invalid credentials for the test.
        String username = "wrongUser";
        String password = "wrongPassword";
        
        // Mock the repository method to return an empty Optional (no admin found with these credentials).
        when(adminRepository.findByUsernameAndPassword(username, password))
                .thenReturn(Optional.empty());
        
        // Assert that the service method throws a CredentialException with the expected message.
        CredentialException thrown = assertThrows(CredentialException.class, () -> {
            adminDataService.findAdminByUsernameAndPassword(username, password);
        });
        
        // Assert that the exception message matches the expected "Invalid credentials." message.
        assertEquals("Invalid credentials.", thrown.getMessage());
        
        // Verify that the repository method was called exactly once with the correct parameters.
        verify(adminRepository, times(1)).findByUsernameAndPassword(username, password);
    }

    // Test case for unexpected repository errors (e.g., database failure).
    @Test
    void testFindAdminByUsernameAndPassword_RepositoryError() {
        
        // Sample valid credentials for the test.
        String username = "adminUser";
        String password = "adminPass";
        
        // Mock the repository method to throw a RuntimeException (simulating a database error).
        when(adminRepository.findByUsernameAndPassword(username, password))
                .thenThrow(new RuntimeException("Database error"));
        
        // Assert that the service method throws the expected RuntimeException.
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            adminDataService.findAdminByUsernameAndPassword(username, password);
        });
        
        // Assert that the exception message matches the expected "Database error" message.
        assertEquals("Database error", thrown.getMessage());
        
        // Verify that the repository method was called exactly once with the correct parameters.
        verify(adminRepository, times(1)).findByUsernameAndPassword(username, password);
    }
}