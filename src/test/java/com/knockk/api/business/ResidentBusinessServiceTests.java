package com.knockk.api.business;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import javax.security.auth.login.CredentialException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.knockk.api.data.service.ResidentDataService;
import com.knockk.api.model.UserModel;

public class ResidentBusinessServiceTests {

    @Mock
    private ResidentDataService dataService;  // Mocking the dependency

    @InjectMocks
    private ResidentBusinessService residentBusinessService;  // The service we are testing

    private String validEmail = "testuser@example.com";
    private String validPassword = "password123";
    private UUID validUUID = UUID.randomUUID();  // Mocked user ID for valid credentials

    private String invalidEmail = "invaliduser@example.com";
    private String invalidPassword = "wrongpassword";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks
    }

    //Simulates a successful login scenario where the credentials are correct
    @Test
    public void testLogin_Success() throws CredentialException {
        // Arrange: Create a valid UserModel and mock the data service's response
        UserModel validUser = new UserModel(validEmail, validPassword);
        when(dataService.findResidentByEmailAndPassword(validEmail, validPassword)).thenReturn(validUUID);

        // Act: Call the login method
        UUID result = residentBusinessService.login(validUser);

        // Assert: Verify the returned UUID matches the mocked validUUID
        assertNotNull(result);
        assertEquals(validUUID, result);

        // Verify that the dataService method was called once with the correct parameters
        verify(dataService, times(1)).findResidentByEmailAndPassword(validEmail, validPassword);
    }
    
    //Simulates an unsuccessful login scenario where the credentials are incorrect
    @Test
    public void testLogin_InvalidCredentials() throws CredentialException {
        // Arrange: Create an invalid UserModel and mock the data service to throw an exception
        UserModel invalidUser = new UserModel(invalidEmail, invalidPassword);
        when(dataService.findResidentByEmailAndPassword(invalidEmail, invalidPassword))
            .thenThrow(new CredentialException("Invalid credentials"));

        // Act & Assert: Verify that a CredentialException is thrown
        assertThrows(CredentialException.class, () -> {
            residentBusinessService.login(invalidUser);
        });

        // Verify that the dataService method was called once with the incorrect credentials
        verify(dataService, times(1)).findResidentByEmailAndPassword(invalidEmail, invalidPassword);
    }
}