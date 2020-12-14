package com.noboseki.tasktimer.controller.integration;

import com.noboseki.tasktimer.playload.UserServiceCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerIT extends ControllerIntegrationTest {

    private final String GET_URL = "/user/get/";
    private final String UPDATE_URL = "/user/update/";
    private final String DELETE_URL = "/user/delete/";
    private final String POST_URL = "/user/create/";

    private final String ADMIN_PASSWORD = "spring";
    private final String USER_PASSWORD = "password";

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
    }

    @Nested
    @DisplayName("Create")
    class UserControllerITCreate {
        private UserServiceCreateRequest request;

        @Test
        @DisplayName("Correct")
        void createCorrect() throws Exception {
            //Given
            request = UserServiceCreateRequest.builder()
                    .email("test@test.com")
                    .userName("test name")
                    .password("TestPassword").build();

            String jsonRequest = gson.toJson(request);

            //Then
            mockMvc.perform(post(POST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonRequest))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("success",is(true)))
                    .andExpect(jsonPath("message",is("User has been created")));
        }

        @Test
        @DisplayName("Valid request")
        void createValidRequest() throws Exception {
            //Given
            request = UserServiceCreateRequest.builder()
                    .email("test@test.com")
                    .password("TestPassword").build();
            String jsonRequest = gson.toJson(request);

            //Then
            mockMvc.perform(post(POST_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonRequest))
                    .andExpect(status().is(417));
        }
    }

    @Nested
    @DisplayName("Get")
    class UserControllerITGet extends ControllerIntegrationTest{

        @Test
        @DisplayName("Correct")
        @WithUserDetails("user@test.com")
        void getCorrectUser() throws Exception {
            mockMvc.perform(get(GET_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.publicId",is(user.getPublicId().intValue())))
                    .andExpect(jsonPath("$.email",is(user.getEmail())))
                    .andExpect(jsonPath("$.username",is(user.getUsername())))
                    .andReturn();
        }

        @Test
        @DisplayName("Unauthorized")
        void getValidUnauthorized() throws Exception {
            useBasicMvcWithHttpBasic(HttpMethod.GET,
                    GET_URL,
                    "", "",
                    401);
        }
    }

    @Nested
    @DisplayName("Get by email")
    class UserControllerITGetByEmail extends ControllerIntegrationTest {

        @Test
        @DisplayName("Correct")
        void getByEmailCorrect() throws Exception {
            mockMvc.perform(get(GET_URL + user.getEmail())
                    .with(httpBasic(admin.getEmail(), ADMIN_PASSWORD)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.publicId",is(user.getPublicId().intValue())))
                    .andExpect(jsonPath("$.email",is(user.getEmail())))
                    .andExpect(jsonPath("$.username",is(user.getUsername())))
                    .andReturn();
        }

        @Test
        @DisplayName("Unauthorized")
        void getByEmailValidUnauthorized() throws Exception {
            useBasicMvcWithHttpBasic(HttpMethod.GET,
                    GET_URL + user.getEmail(),
                    user.getEmail(), USER_PASSWORD,
                    403);
        }

        @Test
        @DisplayName("Forbidden")
        void getByEmailValidForbidden() throws Exception {
            useBasicMvcWithHttpBasic(HttpMethod.GET,
                    GET_URL + admin.getEmail(),
                    admin.getEmail(), admin.getPassword(),
                    401);
        }
    }

    @Nested
    @DisplayName("Delete")
    class UserControllerITDelete extends ControllerIntegrationTest {

        @Test
        @DisplayName("Correct")
        void deleteCorrect() throws Exception {
            useBasicMvcWithHttpBasic(HttpMethod.DELETE,
                    DELETE_URL + user.getEmail(),
                    admin.getEmail(), ADMIN_PASSWORD,
                    200);
        }

        @Test
        @DisplayName("Unauthorized")
        void deleteValidUnauthorized() throws Exception {
            useBasicMvcWithHttpBasic(HttpMethod.DELETE,
                    DELETE_URL + user.getEmail(),
                    user.getEmail(),
                    USER_PASSWORD,
                    403);
        }

        @Test
        @DisplayName("Forbidden")
        void deleteValidForbidden() throws Exception {
            useBasicMvcWithHttpBasic(HttpMethod.DELETE,
                    DELETE_URL + admin.getEmail(),
                    admin.getEmail(),ADMIN_PASSWORD,
                    403);
        }
    }

    @Test
    @DisplayName("Update name correct")
    @WithUserDetails("user@test.com")
    void updateNameCorrect() throws Exception {
        mockMvc.perform(put(UPDATE_URL + "name/test2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success",is(true)))
                .andExpect(jsonPath("message",is("Username has been changed"))).andReturn();
    }

    @Test
    @DisplayName("Update imageUrl correct")
    @WithUserDetails("user@test.com")
    void updateImageUrlCorrect() throws Exception {
        mockMvc.perform(put(UPDATE_URL + "imageUrl/test.url.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success",is(true)))
                .andExpect(jsonPath("message",is("Image has been changed"))).andReturn();
    }


    private MvcResult useBasicMvcWithHttpBasic(HttpMethod httpMethod, String url,
                                               String email, String password,
                                               Integer status) throws Exception {
        switch (httpMethod){
            case GET:
                return mockMvc.perform(get(url)
                        .with(httpBasic(email, password)))
                        .andExpect(status().is(status)).andReturn();
            case PUT:
                return mockMvc.perform(put(url)
                        .with(httpBasic(email, password)))
                        .andExpect(status().is(status)).andReturn();
            case DELETE:
                return mockMvc.perform(delete(url)
                        .with(httpBasic(email, password)))
                        .andExpect(status().is(status)).andReturn();
            default:
                throw new RuntimeException();
        }
    }
}