// Grace Radlund
// 12-15-2024
// Tests generated with the help of ChatGPT 4o mini
package com.knockk.api.data.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;
import javax.security.auth.login.CredentialException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.knockk.api.data.repository.ResidentRepository;
import com.knockk.api.data.repository.UserRepository;

public class ResidentDataServiceTests {

    // Mock the UserRepository to simulate interactions with the database.
    @Mock
    private UserRepository userRepository;
    
    // Mock the ResidentRepository to simulate interactions with the database.
    @Mock
    private ResidentRepository residentRepository;
    
    private ResidentDataService residentDataService;

    // Test data to be used in multiple test cases.
    private String validEmail = "testuser@example.com";
    private String validPassword = "password123";
    private UUID validId = UUID.randomUUID(); // A mock UUID to simulate a valid resident ID.


    @BeforeEach
    public void setUp() {
        // Initialize the mocks before each test method
        MockitoAnnotations.openMocks(this);
        
        // Inject mocks into the service.
        residentDataService = new ResidentDataService(userRepository, residentRepository);
    }

    // Test case for successful login with valid email and password.
    @Test
    public void testFindResidentByEmailAndPassword_Success() throws CredentialException {
        // Mock the repository to return a valid UUID when correct credentials are provided.
        when(userRepository.findByEmailAndPassword(validEmail, validPassword)).thenReturn(Optional.of(validId));

        // Call the service method with valid credentials and capture the result.
        UUID result = residentDataService.findResidentByEmailAndPassword(validEmail, validPassword);

        // Assert that the returned UUID is not null, indicating a successful login.
        assertNotNull(result);
        // Assert that the returned UUID matches the expected valid ID.
        assertEquals(validId, result);

        // Verify that the repository method was called exactly once with the correct parameters.
        verify(userRepository, times(1)).findByEmailAndPassword(validEmail, validPassword);
    }

    // Test case for invalid credentials (i.e., wrong email or password).
    @Test
    public void testFindResidentByEmailAndPassword_InvalidCredentials() {
        // Mock the repository to return an empty Optional when invalid credentials are provided.
        when(userRepository.findByEmailAndPassword(validEmail, validPassword)).thenReturn(Optional.empty());

        // Assert that the service method throws a CredentialException when no matching resident is found.
        assertThrows(CredentialException.class, () -> {
            residentDataService.findResidentByEmailAndPassword(validEmail, validPassword);
        });

        // Verify that the repository method was called exactly once with the correct parameters.
        verify(userRepository, times(1)).findByEmailAndPassword(validEmail, validPassword);
    }

    // Test case for an empty email provided in the login attempt.
    @Test
    public void testFindResidentByEmailAndPassword_EmptyEmail() throws CredentialException {
        // Simulate an empty email and mock the repository to return an empty Optional.
        String emptyEmail = "";
        when(userRepository.findByEmailAndPassword(emptyEmail, validPassword)).thenReturn(Optional.empty());

        // Assert that the service method throws a CredentialException when the email is empty.
        assertThrows(CredentialException.class, () -> {
            residentDataService.findResidentByEmailAndPassword(emptyEmail, validPassword);
        });

        // Verify that the repository method was called exactly once with the empty email.
        verify(userRepository, times(1)).findByEmailAndPassword(emptyEmail, validPassword);
    }

    // Test case for an empty password provided in the login attempt.
    @Test
    public void testFindResidentByEmailAndPassword_EmptyPassword() throws CredentialException {
        // Simulate an empty password and mock the repository to return an empty Optional.
        String emptyPassword = "";
        when(userRepository.findByEmailAndPassword(validEmail, emptyPassword)).thenReturn(Optional.empty());

        // Assert that the service method throws a CredentialException when the password is empty.
        assertThrows(CredentialException.class, () -> {
            residentDataService.findResidentByEmailAndPassword(validEmail, emptyPassword);
        });

        // Verify that the repository method was called exactly once with the empty password.
        verify(userRepository, times(1)).findByEmailAndPassword(validEmail, emptyPassword);
    }
}
