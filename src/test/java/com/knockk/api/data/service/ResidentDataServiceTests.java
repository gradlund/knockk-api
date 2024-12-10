package com.knockk.api.data.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;
import javax.security.auth.login.CredentialException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.knockk.api.data.repository.UserRepository;

public class ResidentDataServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ResidentDataService residentDataService;

    private String validEmail = "testuser@example.com";
    private String validPassword = "password123";
    private UUID validId = UUID.randomUUID();

    @BeforeEach
    public void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);
    }

    //Simulates finding a resident when the credentials are valid
    @Test
    public void testFindResidentByEmailAndPassword_Success() throws CredentialException {
        // Arrange: Simulate a successful user repository response
        when(userRepository.findByEmailAndPassword(validEmail, validPassword)).thenReturn(Optional.of(validId));

        // Act: Call the service method
        UUID result = residentDataService.findResidentByEmailAndPassword(validEmail, validPassword);

        // Assert: Verify the result is as expected
        assertNotNull(result);
        assertEquals(validId, result);

        // Verify that the repository method was called with the correct arguments
        verify(userRepository, times(1)).findByEmailAndPassword(validEmail, validPassword);
    }

    //Simulates finding a resident where the credentials are invalid
    @Test
    public void testFindResidentByEmailAndPassword_InvalidCredentials() {
        // Arrange: Simulate no user found in the repository
        when(userRepository.findByEmailAndPassword(validEmail, validPassword)).thenReturn(Optional.empty());

        // Act & Assert: Verify that a CredentialException is thrown
        assertThrows(CredentialException.class, () -> {
            residentDataService.findResidentByEmailAndPassword(validEmail, validPassword);
        });

        // Verify that the repository method was called once
        verify(userRepository, times(1)).findByEmailAndPassword(validEmail, validPassword);
    }

    //Tests the behavior when the email is empty
    @Test
    public void testFindResidentByEmailAndPassword_EmptyEmail() throws CredentialException {
        // Arrange: Simulate a response when email is empty
        String emptyEmail = "";
        when(userRepository.findByEmailAndPassword(emptyEmail, validPassword)).thenReturn(Optional.empty());

        // Act & Assert: Expect CredentialException for empty email
        assertThrows(CredentialException.class, () -> {
            residentDataService.findResidentByEmailAndPassword(emptyEmail, validPassword);
        });

        // Verify the repository interaction
        verify(userRepository, times(1)).findByEmailAndPassword(emptyEmail, validPassword);
    }

    //Tests the behavior when the password is empty
    @Test
    public void testFindResidentByEmailAndPassword_EmptyPassword() throws CredentialException {
        // Arrange: Simulate a response when password is empty
        String emptyPassword = "";
        when(userRepository.findByEmailAndPassword(validEmail, emptyPassword)).thenReturn(Optional.empty());

        // Act & Assert: Expect CredentialException for empty password
        assertThrows(CredentialException.class, () -> {
            residentDataService.findResidentByEmailAndPassword(validEmail, emptyPassword);
        });

        // Verify the repository interaction
        verify(userRepository, times(1)).findByEmailAndPassword(validEmail, emptyPassword);
    }
}
