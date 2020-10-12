package com.noboseki.tasktimer.controller.integration;

import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TaskControllerIntegrationTest extends  ControllerIntegrationTest{

    @Test
    @Order(1)
    @DisplayName("Get correct")
    void getCorrect() throws Exception {
        //When
        when(taskService.get(any(UUID.class))).thenReturn(ResponseEntity.ok(taskDto));

        //Then
        mockMvc.perform(get("/task/get/" + uuid)
                .with(httpBasic(userName, userPassword)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.privateID",is(taskDto.getPrivateID().toString())))
                .andExpect(jsonPath("$.name",is("Test")))
                .andExpect(jsonPath("$.complete",is(true)));

        verify(taskService, times(1)).get(any(UUID.class));
    }

    @Test
    @Order(2)
    @DisplayName("Get valid unauthorized")
    void getValidUnauthorized() throws Exception {
        //When
        when(taskService.get(any(UUID.class))).thenReturn(ResponseEntity.ok(taskDto));

        //Then
        getValidUnauthorized("/task/get/" + uuid);
        verify(taskService, times(0)).get(any(UUID.class));
    }

    @Test
    @Order(3)
    @DisplayName("Get valid not found")
    void getValidNotFound() throws Exception {
        //When
        when(taskService.get(any(UUID.class))).thenThrow(ResourceNotFoundException.class);

        //Then
        getValidNotFound("/task/get/" + uuid);
        verify(taskService, times(1)).get(any(UUID.class));
    }

    @Test
    @Order(4)
    @DisplayName("Delete correct")
    void deleteCorrect() throws Exception {
        //When
        when(taskService.delete(any(UUID.class))).thenReturn(response);

        //Then
        deleteCorrect("/task/delete/" + uuid);
        verify(taskService, times(1)).delete(any(UUID.class));
    }

    @Test
    @Order(5)
    @DisplayName("Delete valid not found")
    void deleteValidNotFound() throws Exception {
        //When
        when(taskService.delete(any(UUID.class))).thenThrow(ResourceNotFoundException.class);

        //Then
        deleteValidNotFound("/task/delete/" + uuid);
        verify(taskService, times(1)).delete(any(UUID.class));
    }

    @Test
    @Order(6)
    @DisplayName("Delete valid unauthorized")
    void deleteValidUnauthorized() throws Exception {
        //When
        when(taskService.delete(any(UUID.class))).thenReturn(response);

        //Then
        deleteValidUnauthorized("/task/delete/" + uuid);
        verify(taskService, times(0)).delete(any(UUID.class));
    }
}