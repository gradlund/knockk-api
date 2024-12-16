// Grace Radlund
// 12-15-2024
// Tests generated with the help of ChatGPT 4o mini
package com.knockk.api.business;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import javax.security.auth.login.CredentialException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.knockk.api.data.repository.UserRepository;
import com.knockk.api.data.service.ResidentDataService;
import com.knockk.api.model.LoginModel;
import com.knockk.api.model.UserModel;

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

    @BeforeEach
    public void setUp() {
    	// Initialize mocks before each test method
        MockitoAnnotations.openMocks(this);
        // Inject mock into the service
        residentBusinessService = new ResidentBusinessService(dataService);
    }

    // Test for successful login
    @Test
    public void testLogin_Success() throws CredentialException {
        //Create a valid UserModel
        UserModel validUser = new UserModel(validEmail, validPassword);
        
        // Mock the ResidentDataService to return the expect resident and verification
        when(dataService.findResidentByEmailAndPassword(validEmail, validPassword)).thenReturn(validUUID);
        when(dataService.checkVerified(validUUID)).thenReturn(true);
        
        // Call the login method
        LoginModel result = residentBusinessService.login(validUser);

        // Verify the returned UUID matches the mocked validUUID
        assertNotNull(result);
        assertEquals(validUUID, result.getId());

        // Verify that the dataService method was called once with the correct credentials
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

        // Verify that the dataService method was called once with the incorrect credentials
        verify(dataService, times(1)).findResidentByEmailAndPassword(invalidEmail, invalidPassword);
    }
}