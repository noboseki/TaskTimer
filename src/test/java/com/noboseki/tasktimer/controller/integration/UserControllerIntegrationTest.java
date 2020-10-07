package com.noboseki.tasktimer.controller.integration;

import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerIntegrationTest extends ControllerIntegrationTest {

    @Test
    @Order(1)
    @DisplayName("Get correct")
    void getCorrect() throws Exception {
        //When
        String uuid = UUID.randomUUID().toString();
        when(userService.get(any(UUID.class))).thenReturn(ResponseEntity.ok(userDto));

        //Then
        mockMvc.perform(get("/user/get/" + uuid)
                    .with(httpBasic(username, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.privateID",is(userDto.getPrivateID().toString())))
                .andExpect(jsonPath("$.publicId",is(1)))
                .andExpect(jsonPath("$.email",is("test@test.com")))
                .andExpect(jsonPath("$.imageUrl",is("test")))
                .andExpect(jsonPath("$.password",is(password)))
                .andExpect(jsonPath("$.emailVerified",is(true)));

        verify(userService, times(1)).get(any(UUID.class));
    }

    @Test
    @Order(2)
    @DisplayName("Get valid unauthorized")
    void getValidUnauthorized() throws Exception {
        //When
        String uuid = UUID.randomUUID().toString();
        when(userService.get(any(UUID.class))).thenReturn(ResponseEntity.ok(userDto));

        //Then
        mockMvc.perform(get("/user/get/" + uuid))
                .andExpect(status().is(401));

        verify(userService, times(0)).get(any(UUID.class));
    }

    @Test
    @Order(3)
    @DisplayName("Get valid not found")
    void getValidNotFound() throws Exception {
        //When
        String uuid = UUID.randomUUID().toString();
        when(userService.get(any(UUID.class))).thenThrow(ResourceNotFoundException.class);

        //Then
        mockMvc.perform(get("/user/get/" + uuid)
                    .with(httpBasic(username, password)))
                .andExpect(status().is(404)).andReturn();

        verify(userService, times(1)).get(any(UUID.class));
    }
}