package com.noboseki.tasktimer.controller.integration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import com.noboseki.tasktimer.playload.CreateSessionRequest;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithUserDetails;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SessionControllerIT extends ControllerIntegrationTest {

    private final String SESSION_CREATE_URL = "/session/create/";

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        Task task = Task.builder()
                .name(TEST_NAME)
                .user(user).build();

        taskDao.save(task);
    }

    @Nested
    @DisplayName("Create")
    class SessionControllerITCreate {

    }

    @Test
    @DisplayName("Correct")
    @WithUserDetails("user@test.com")
    void createCorrect() throws Exception {
        //Given
        CreateSessionRequest request = new CreateSessionRequest("2016-08-16", "03:18:23");
        String jsonRequest = gson.toJson(request);

        //Then
        mockMvc.perform(post(SESSION_CREATE_URL + TEST_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(jsonRequest))
                .andExpect(status().isOk());
    }
}
