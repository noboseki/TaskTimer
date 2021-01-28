package com.noboseki.tasktimer.controller.integration;

import com.noboseki.tasktimer.domain.ConfirmationToken;
import com.noboseki.tasktimer.domain.TokenType;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.ExceptionTextConstants;
import com.noboseki.tasktimer.playload.UserServiceChangePasswordRequest;
import com.noboseki.tasktimer.playload.UserServiceCreateRequest;
import com.noboseki.tasktimer.playload.UserServiceUpdateRequest;
import com.noboseki.tasktimer.service.ConfirmationTokenService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerIT extends BaseControllerTest {
    private final String PASSWORD = "password";

    @Autowired
    private ConfirmationTokenService tokenService;

    private User user;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        user = createTestUser(PASSWORD);
    }

    @AfterEach
    void tearDown() {
        userDao.delete(user);
    }

    @Nested
    @DisplayName("Create")
    class UserControllerITCreate {
        private final String URL = "/user/create/";
        private UserServiceCreateRequest request;

        @BeforeEach
        void setUp() {
            request = UserServiceCreateRequest.builder()
                    .email("create@test.com")
                    .password("password")
                    .username("ItUsername").build();
        }

        @Test
        @DisplayName("Correct")
        void correct() throws Exception {
            String JsonRequest = mapper.writeValueAsString(request);

            mockMvc.perform(post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(charEncoding)
                    .content(JsonRequest))
                    .andExpect(status().isOk())
                    .andExpect(content().string("User has been created"));
        }

        @Test
        @DisplayName("Duplicate error")
        void duplicateError() throws Exception {
            request.setEmail("test@test.com");

            String JsonRequest = mapper.writeValueAsString(request);

            mockMvc.perform(post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(charEncoding)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(JsonRequest))
                    .andExpect(status().is(409))
                    .andExpect(jsonPath("message",
                            is(ExceptionTextConstants.duplicate("User", request.getEmail()))));
        }
    }

    @Nested
    @DisplayName("Get")
    class UserControllerITGet {
        private final String url = "/user/get";

        @Test
        @DisplayName("Correct")
        void correct() throws Exception {
            mockMvc.perform(get(url)
                    .with(httpBasic(user.getEmail(), PASSWORD)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("publicId", is(user.getPublicId().intValue())))
                    .andExpect(jsonPath("email", is(user.getEmail())))
                    .andExpect(jsonPath("username", is(user.getUsername())));
        }

        @Test
        @DisplayName("Unauthorized")
        void unauthorized() throws Exception {
            mockMvc.perform(get(url))
                    .andExpect(status().is(401));
        }
    }

    @Nested
    @DisplayName("Change password token request")
    class UserControllerITChangePasswordTokenRequest {
        private final String url = "/user/changePasswordTokenRequest/";

        @Test
        @DisplayName("Correct")
        void correct() throws Exception {
            mockMvc.perform(post(url)
                    .content("test@test.com"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Emile has been sent")).andReturn();

            ConfirmationToken token = tokenService.getByUser_EmailAndType(user.getEmail(), TokenType.PASSWORD)
                    .orElseThrow();
            tokenService.deleteToken(token);
        }

        @Test
        @DisplayName("Invalid email error")
        void invalidEmailError() throws Exception {
            mockMvc.perform(post(url)
                    .content("invalid@test.com"))
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("message", is(ExceptionTextConstants.resourceNotFound("User", "invalid@test.com"))))
                    .andExpect(jsonPath("httpStatus", is("NOT_FOUND")));
        }
    }

    @Nested
    @DisplayName("Change password")
    class UserControllerITChangePassword {
        private final String URL = "/user/changePassword/";
        private ConfirmationToken token;

        @AfterEach
        void tearDown() {
            if (token != null) {
                tokenService.deleteToken(token);
            }
        }

        @Test
        @DisplayName("Correct")
        void correct() throws Exception {
            token = tokenService.createTokenForUser(user, TokenType.PASSWORD);
            UserServiceChangePasswordRequest request = new UserServiceChangePasswordRequest(
                    token.getConfirmationToken(),
                    PASSWORD,
                    "password2021");

            mockMvc.perform(put(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(charEncoding)
                    .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Password has been changed")).andReturn();

            tokenService.deleteToken(token);
        }

        @Test
        @DisplayName("Invalid token")
        void invalidToken() throws Exception {
            UserServiceChangePasswordRequest request = new UserServiceChangePasswordRequest(
                    UUID.randomUUID().toString(),
                    "password",
                    "password2021");

            mockMvc.perform(put(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(charEncoding)
                    .content(mapper.writeValueAsString(request)))
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("message",
                            is(ExceptionTextConstants.resourceNotFound("Token", request.getToken()))))
                    .andExpect(jsonPath("httpStatus", is("NOT_FOUND")));
        }

        @Test
        @DisplayName("Invalid old password")
        void invalidOldPassword() throws Exception {
            token = tokenService.createTokenForUser(user, TokenType.PASSWORD);
            UserServiceChangePasswordRequest request = new UserServiceChangePasswordRequest(
                    token.getConfirmationToken(),
                    "password2021",
                    "password2021");

            mockMvc.perform(put(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(charEncoding)
                    .content(mapper.writeValueAsString(request)))
                    .andExpect(status().is(409))
                    .andExpect(jsonPath("message",
                            is(ExceptionTextConstants.invalid("Old password", request.getOldPassword()))))
                    .andExpect(jsonPath("httpStatus", is("CONFLICT")));

            tokenService.deleteToken(token);
        }
    }

    @Nested
    @DisplayName("Update profile")
    class UserControllerITUpdateProfile {
        private final String URL = "/user/update/";
        private UserServiceUpdateRequest request;

        @BeforeEach
        void setUp() {
            request = new UserServiceUpdateRequest(
                    "testUsername",
                    "userTest@test.com",
                    "Thor");
        }

        @Test
        @DisplayName("Correct")
        void correct() throws Exception {
            mockMvc.perform(put(URL)
                    .with(httpBasic(user.getEmail(), PASSWORD))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(charEncoding)
                    .content(mapper.writeValueAsString(request)))
                    .andExpect(content().string("User profile has been updated"))
                    .andExpect(status().isOk()).andReturn();
        }

        @Test
        @DisplayName("Invalid profile img name")
        void InvalidProfileImg() throws Exception {
            request.setProfileImgName("Test");

            mockMvc.perform(put(URL)
                    .with(httpBasic(user.getEmail(), PASSWORD))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(charEncoding)
                    .content(mapper.writeValueAsString(request)))
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("message",
                            is(ExceptionTextConstants.resourceNotFound("Profile img", request.getProfileImgName()))))
                    .andExpect(jsonPath("httpStatus", is("NOT_FOUND")));
        }

        @Test
        @DisplayName("Unauthorized")
        void unauthorized() throws Exception {
            mockMvc.perform(put(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(charEncoding)
                    .content(mapper.writeValueAsString(request)))
                    .andExpect(status().is(401));
        }
    }
}