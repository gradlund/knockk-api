// Grace Radlund
// 12-15-2024
// Tests generated with the help of ChatGPT 4o mini
package com.knockk.api.business;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.knockk.api.data.service.AdminDataService;
import com.knockk.api.model.AdminModel;

import javax.security.auth.login.CredentialException;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class AdminBusinessServiceTests {

	// Mock the AdminDataService
	@Mock
    private AdminDataService dataService; 

    private AdminBusinessService adminBusinessService;

    @BeforeEach
    void setUp() {
    	// Initialize mocks before each test method
        MockitoAnnotations.openMocks(this); 
        // Inject mock into service
        adminBusinessService = new AdminBusinessService(dataService); 
    }

    // Test for successful login
    @Test
    void testLogin_Success() throws Exception {
    	// Prepare test data (adminModel) and define the expected result (UUID).
        AdminModel adminModel = new AdminModel("adminUser", "adminPassword");
        // Mock UUID as the expected result
        UUID expectedUuid = UUID.randomUUID(); 

        // Mock the AdminDataService to return the expected UUID when called with the admin's username and password.
        when(dataService.findAdminByUsernameAndPassword(adminModel.getUsername(), adminModel.getPassword()))
                .thenReturn(expectedUuid);

        // Call the login method on the business service and get the actual result.
        UUID actualUuid = adminBusinessService.login(adminModel);

        // Check that the result is not null and matches the expected UUID.
        assertNotNull(actualUuid);
        assertEquals(expectedUuid, actualUuid);
        
        // Verify that the service method was called with the correct parameters
        verify(dataService, times(1)).findAdminByUsernameAndPassword(adminModel.getUsername(), adminModel.getPassword());
    }

    // Test for invalid credentials (throws CredentialException)
    @Test
    void testLogin_InvalidCredentials() throws CredentialException {
        // Prepare invalid login credentials (wrong username and password).
        AdminModel adminModel = new AdminModel("wrongUser", "wrongPassword");

        // Mock the AdminDataService to throw a CredentialException when called with invalid credentials.
        when(dataService.findAdminByUsernameAndPassword(adminModel.getUsername(), adminModel.getPassword()))
                .thenThrow(new CredentialException("Invalid credentials"));

        // Call the login method and assert that the CredentialException is thrown.
        CredentialException thrown = assertThrows(CredentialException.class, () -> {
            adminBusinessService.login(adminModel);
        });

        // Check that the exception message matches the expected error message.
        assertEquals("Invalid credentials", thrown.getMessage());
        
        // Verify that the service method was called with the correct parameters
        verify(dataService, times(1)).findAdminByUsernameAndPassword(adminModel.getUsername(), adminModel.getPassword());
    }

    // Test for unexpected exceptions 
    @Test
    void testLogin_UnexpectedException() throws CredentialException {
        // Prepare valid login credentials but simulate an unexpected error (RuntimeException).
        AdminModel adminModel = new AdminModel("adminUser", "adminPassword");

        // Mock the AdminDataService to throw a RuntimeException for any reason.
        when(dataService.findAdminByUsernameAndPassword(adminModel.getUsername(), adminModel.getPassword()))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Call the login method and assert that the RuntimeException is thrown.
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            adminBusinessService.login(adminModel);
        });

        // Check that the exception message matches the expected error message.
        assertEquals("Unexpected error", thrown.getMessage());
        
        // Verify that the service method was called with the correct parameters
        verify(dataService, times(1)).findAdminByUsernameAndPassword(adminModel.getUsername(), adminModel.getPassword());
    }

}
