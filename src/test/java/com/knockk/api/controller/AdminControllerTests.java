package com.knockk.api.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Errors;
import org.springframework.web.context.WebApplicationContext;

import com.knockk.api.business.AdminBusinessService;
import com.knockk.api.model.AdminModel;
import com.knockk.api.model.ResponseModel;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(AdminController.class)
public class AdminControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminBusinessService service;

    @Mock
    private Errors errors;

    private AdminModel validAdminModel;

    @BeforeEach
    public void setUp() {
        validAdminModel = new AdminModel("validUsername", "validPassword");
    }

    @Test
    public void testLoginSuccess() throws Exception {
        UUID mockId = UUID.randomUUID();
        when(service.login(any(AdminModel.class))).thenReturn(mockId);

        mockMvc.perform(post("/admin/login")
                .contentType("application/json")
                .content("{\"username\":\"validUsername\",\"password\":\"validPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login Successful"))
                .andExpect(jsonPath("$.data.Id").value(mockId.toString()));
    }

    @Test
    public void testLoginBadRequest() throws Exception {
        // Simulate validation errors
        when(errors.hasErrors()).thenReturn(true);

        mockMvc.perform(post("/admin/login")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Bad request"));
    }

    @Test
    public void testLoginInvalidCredentials() throws Exception {
        // Simulate service throwing an exception for invalid credentials
        when(service.login(any(AdminModel.class))).thenThrow(new IllegalArgumentException("Invalid credentials"));

        mockMvc.perform(post("/admin/login")
                .contentType("application/json")
                .content("{\"username\":\"invalid\",\"password\":\"wrongPassword\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Forbidden. Invalid credentials."));
    }

    @Test
    public void testHandleErrorResponse_InternalServerError() {
        // Simulate a generic exception being thrown
        Exception exception = new RuntimeException("Internal service error");
        ResponseEntity<?> response = new AdminController().handleErrorResponse(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Service Error. The server was unable to complete your request. Please try again later.", 
                     ((ResponseModel<String>) response.getBody()).getMessage());
    }

    @Test
    public void testHandleErrorResponse_BadRequest() {
        // Simulate a BadRequestException
        Exception exception = new IllegalArgumentException("Bad request");
        ResponseEntity<?> response = new AdminController().handleErrorResponse(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Bad request", ((ResponseModel<String>) response.getBody()).getMessage());
    }
}
