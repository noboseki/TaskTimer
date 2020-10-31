package com.noboseki.tasktimer.controller.integration;

import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.playload.CreateSessionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SessionControllerIT extends ControllerIntegrationTest {

    private final String SESSION_CREATE_URL = "/session/create/";
    private final String SESSION_GET_BY_TASK = "/session/getByTask/";
    private final String SESSION_GET_BY_DATE = "/session/getByDate/";

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
    class SessionControllerITCreate extends ControllerIntegrationTest {

        private CreateSessionRequest request;
        private String jsonRequest;

        @BeforeEach
        void setUp() {
            super.setUp();
            request = new CreateSessionRequest("2016-08-16", "03:18:23");
        }

        @Test
        @DisplayName("Correct")
        @WithUserDetails("user@test.com")
        void createCorrect() throws Exception {
            //Given
            jsonRequest = gson.toJson(request);

            //Then
            mockMvc.perform(post(SESSION_CREATE_URL + TEST_NAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonRequest))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Valid data format")
        @WithUserDetails("user@test.com")
        void createValidDataFormat() throws Exception {
            //Given
            request.setDate("2016--08--16");
            String jsonRequest = gson.toJson(request);

            //Then
            mockMvc.perform(post(SESSION_CREATE_URL + TEST_NAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonRequest))
                    .andExpect(status().is(400));
        }

        @Test
        @DisplayName("Valid time format")
        @WithUserDetails("user@test.com")
        void createValidTimeFormat() throws Exception {
            //Given
            request.setTime("03:18");
            String jsonRequest = gson.toJson(request);

            //Then
            mockMvc.perform(post(SESSION_CREATE_URL + TEST_NAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonRequest))
                    .andExpect(status().is(400));
        }

        @Test
        @DisplayName("Resource not found")
        @WithUserDetails("user@test.com")
        void createResourceNotFound() throws Exception {
            //Then
            useBasicMvc(HttpMethod.POST, SESSION_CREATE_URL + "Task Not Found", 400);
        }

        @Test
        @DisplayName("Unauthorized")
        void createUnauthorized() throws Exception {
            //Then
            useBasicMvc(HttpMethod.POST, SESSION_CREATE_URL + TEST_NAME, 401);
        }
    }

    @Nested
    @DisplayName("Get by task")
    class SessionControllerITGetByTask extends ControllerIntegrationTest {

        @Test
        @DisplayName("Correct")
        @WithUserDetails("user@test.com")
        void getByTaskCorrect() throws Exception {
            //Then
            useBasicMvc(HttpMethod.GET, SESSION_GET_BY_TASK + TEST_NAME, 200);
        }

        @Test
        @DisplayName("Valid task name")
        @WithUserDetails("user@test.com")
        void getByTaskValidTaskName() throws Exception {
            //Then
            useBasicMvc(HttpMethod.GET, SESSION_GET_BY_TASK + "Valid task name", 404);
        }

        @Test
        @DisplayName("Unauthorized")
        void getByTaskUnauthorized() throws Exception {
            //Then
            useBasicMvc(HttpMethod.GET, SESSION_GET_BY_TASK + TEST_NAME, 401);
        }
    }

    @Nested
    @DisplayName("Get by date")
    class SessionControllerITGetByDate extends  ControllerIntegrationTest {
        protected final String DATE = "2016-08-16";

        @Test
        @DisplayName("Correct")
        @WithUserDetails("user@test.com")
        void getByDateCorrect() throws Exception {
            //Then
            useBasicMvc(HttpMethod.GET, SESSION_GET_BY_DATE + DATE, 200);
        }

        @Test
        @DisplayName("Valid date format")
        @WithUserDetails("user@test.com")
        void getByDateValidTaskName() throws Exception {
            //Then
            useBasicMvc(HttpMethod.GET, SESSION_GET_BY_DATE + "Valid date format", 400);
        }

        @Test
        @DisplayName("Unauthorized")
        void getByDateUnauthorized() throws Exception {
            //Then
            useBasicMvc(HttpMethod.GET, SESSION_GET_BY_DATE + DATE, 401);
        }
    }
}
