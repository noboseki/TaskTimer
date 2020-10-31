package com.noboseki.tasktimer.controller.integration;

import com.noboseki.tasktimer.domain.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TaskControllerIT extends ControllerIntegrationTest {

    private final String POST_URL = "/task/create/";
    private final String GET_URL = "/task/get/";
    private final String GET_ALL_URL = "/task/getAll/";
    private final String PUT_UPDATE_NAME = "/task/updateName/";
    private final String PUT_UPDATE_STATUS = "/task/updateStatus/";
    private final String DELETE_URL = "/task/delete/";

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
    class TaskControllerITCreate extends ControllerIntegrationTest {

        @Test
        @DisplayName("Correct")
        @WithUserDetails("user@test.com")
        void createCorrect() throws Exception {
            //Then
            mvcCreateForm(POST_URL + "New Test",true, "Task has been created");
        }

        @Test
        @DisplayName("Duplicate name")
        @WithUserDetails("user@test.com")
        void createDuplicateName() throws Exception {
            //Then
            mvcCreateForm(POST_URL + TEST_NAME ,false, "Duplicate task name");
        }

        @Test
        @DisplayName("Unauthorized")
        void createUnauthorized() throws Exception {
            //Then
            useBasicMvc(HttpMethod.POST, POST_URL + TEST_NAME, 401);
        }

        private MvcResult mvcCreateForm(String url,boolean success, String message) throws Exception {
            return mockMvc.perform(post(url))
                        .andExpect(status().isOk())
                    .andExpect(jsonPath("success",is(success)))
                    .andExpect(jsonPath("message",is(message))).andReturn();
        }
    }

    @Nested
    @DisplayName("Get")
    class TaskControllerITGet extends ControllerIntegrationTest {

        @Test
        @DisplayName("Correct")
        @WithUserDetails("user@test.com")
        void getCorrect() throws Exception {
            //Then
            mockMvc.perform(get(GET_URL + TEST_NAME))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("name",is(TEST_NAME)))
                    .andExpect(jsonPath("complete",is(false)));
        }

        @Test
        @DisplayName("Resource not found")
        @WithUserDetails("user@test.com")
        void getResourceNotFound() throws Exception {
            //Then
            useBasicMvc(HttpMethod.GET, GET_URL + "", 404);
        }

        @Test
        @DisplayName("Unauthorized")
        void getUnauthorized() throws Exception {
            //Then
            useBasicMvc(HttpMethod.GET, GET_URL + TEST_NAME, 401);
        }
    }

    @Nested
    @DisplayName("GetAll")
    class TaskControllerITGetAll extends ControllerIntegrationTest {

        @Test
        @DisplayName("Correct")
        @WithUserDetails("user@test.com")
        void getAllCorrect() throws Exception {
            //Then
            mockMvc.perform(get(GET_ALL_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.[1].name",is(TEST_NAME)))
                    .andExpect(jsonPath("$.[1].complete",is(false)));
        }

        @Test
        @DisplayName("Unauthorized")
        void getAllUnauthorized() throws Exception {
            //Then
            useBasicMvc(HttpMethod.GET, GET_ALL_URL, 401);
        }
    }

    @Nested
    @DisplayName("Update Name")
    class TaskControllerITUpdateName extends ControllerIntegrationTest {

        @Test
        @DisplayName("Correct")
        @WithUserDetails("user@test.com")
        void UpdateNameCorrect() throws Exception {
            mvcUpdateNameForm(PUT_UPDATE_NAME + TEST_NAME + "/New Name", true, "Task name has been updated");
        }

        @Test
        @DisplayName("Duplicate")
        @WithUserDetails("user@test.com")
        void UpdateNameDuplicate() throws Exception {
            mvcUpdateNameForm(PUT_UPDATE_NAME + TEST_NAME + "/" + TEST_NAME, false, "Duplicate task name");
        }

        @Test
        @DisplayName("Task not found")
        @WithUserDetails("user@test.com")
        void UpdateNameTaskNotFound() throws Exception {
            useBasicMvc(HttpMethod.PUT, PUT_UPDATE_NAME + "not found/New Name", 404);
        }

        @Test
        @DisplayName("Unauthorized")
        void UpdateNameUnauthorized() throws Exception {
            //Then
            useBasicMvc(HttpMethod.PUT, PUT_UPDATE_NAME + TEST_NAME + "/New Name", 401);
        }

        private MvcResult mvcUpdateNameForm(String url, boolean success, String message) throws Exception {
            return mockMvc.perform(put(url))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("success",is(success)))
                    .andExpect(jsonPath("message",is(message))).andReturn();
        }
    }

    @Nested
    @DisplayName("Update Status")
    class TaskControllerITUpdateStatus extends ControllerIntegrationTest {

        @Test
        @DisplayName("Correct")
        @WithUserDetails("user@test.com")
        void UpdateStatusCorrect() throws Exception {
            mockMvc.perform(put(PUT_UPDATE_STATUS + TEST_NAME))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("success",is(true)))
                    .andExpect(jsonPath("message",is("Task status has been updated")));
        }

        @Test
        @DisplayName("Task not found")
        @WithUserDetails("user@test.com")
        void UpdateStatusTaskNotFound() throws Exception {
            useBasicMvc(HttpMethod.PUT, PUT_UPDATE_STATUS + "Not Found", 404);
        }

        @Test
        @DisplayName("Unauthorized")
        void UpdateNameUnauthorized() throws Exception {
            useBasicMvc(HttpMethod.PUT, PUT_UPDATE_STATUS + TEST_NAME, 401);
        }
    }

    @Nested
    @DisplayName("Delete")
    class TaskControllerITDelete extends ControllerIntegrationTest {

        @Test
        @DisplayName("Correct")
        @WithUserDetails("user@test.com")
        void DeleteCorrect() throws Exception {
            mockMvc.perform(delete(DELETE_URL + TEST_NAME))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("success",is(true)))
                    .andExpect(jsonPath("message",is("Task has been deleted")));
        }

        @Test
        @DisplayName("Task not found")
        @WithUserDetails("user@test.com")
        void DeleteTaskNotFound() throws Exception {
            useBasicMvc(HttpMethod.DELETE, DELETE_URL + "Not Found", 404);
        }

        @Test
        @DisplayName("Unauthorized")
        void DeleteUnauthorized() throws Exception {
            useBasicMvc(HttpMethod.DELETE, DELETE_URL + TEST_NAME, 401);
        }
    }
}
