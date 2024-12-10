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

	@Mock
    private AdminDataService dataService; // Mock the AdminDataService

    private AdminBusinessService adminBusinessService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        adminBusinessService = new AdminBusinessService(dataService); // Inject mock into service
    }

    // Test for successful login
    @Test
    void testLogin_Success() throws Exception {
        // Arrange
        AdminModel adminModel = new AdminModel("adminUser", "adminPassword");
        UUID expectedUuid = UUID.randomUUID(); // Mock UUID as the expected result

        // Mock the call to AdminDataService
        when(dataService.findAdminByUsernameAndPassword(adminModel.getUsername(), adminModel.getPassword()))
                .thenReturn(expectedUuid);

        // Act
        UUID actualUuid = adminBusinessService.login(adminModel);

        // Assert
        assertNotNull(actualUuid);
        assertEquals(expectedUuid, actualUuid);
        
        // Verify that the service method was called with the correct parameters
        verify(dataService, times(1)).findAdminByUsernameAndPassword(adminModel.getUsername(), adminModel.getPassword());
    }

    // Test for invalid credentials (throws CredentialException)
    @Test
    void testLogin_InvalidCredentials() throws CredentialException {
        // Arrange
        AdminModel adminModel = new AdminModel("wrongUser", "wrongPassword");

        // Mock the call to AdminDataService to throw CredentialException
        when(dataService.findAdminByUsernameAndPassword(adminModel.getUsername(), adminModel.getPassword()))
                .thenThrow(new CredentialException("Invalid credentials"));

        // Act & Assert
        CredentialException thrown = assertThrows(CredentialException.class, () -> {
            adminBusinessService.login(adminModel);
        });

        assertEquals("Invalid credentials", thrown.getMessage());
        
        // Verify that the service method was called with the correct parameters
        verify(dataService, times(1)).findAdminByUsernameAndPassword(adminModel.getUsername(), adminModel.getPassword());
    }

    // Test for unexpected exceptions (e.g., RuntimeException)
    @Test
    void testLogin_UnexpectedException() throws CredentialException {
        // Arrange
        AdminModel adminModel = new AdminModel("adminUser", "adminPassword");

        // Mock the call to AdminDataService to throw an unexpected exception
        when(dataService.findAdminByUsernameAndPassword(adminModel.getUsername(), adminModel.getPassword()))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            adminBusinessService.login(adminModel);
        });

        assertEquals("Unexpected error", thrown.getMessage());
        
        // Verify that the service method was called with the correct parameters
        verify(dataService, times(1)).findAdminByUsernameAndPassword(adminModel.getUsername(), adminModel.getPassword());
    }

}
