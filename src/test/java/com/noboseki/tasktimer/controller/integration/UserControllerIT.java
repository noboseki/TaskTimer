package com.noboseki.tasktimer.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noboseki.tasktimer.domain.*;
import com.noboseki.tasktimer.playload.UserServiceChangePasswordRequest;
import com.noboseki.tasktimer.playload.UserServiceCreateRequest;
import com.noboseki.tasktimer.playload.UserServiceUpdateRequest;
import com.noboseki.tasktimer.repository.AuthorityDao;
import com.noboseki.tasktimer.repository.ProfileImgDao;
import com.noboseki.tasktimer.repository.UserDao;
import com.noboseki.tasktimer.service.ConfirmationTokenService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class UserControllerIT {
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private ConfirmationTokenService tokenService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private AuthorityDao authorityDao;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ProfileImgDao profileImgDao;

    private User user;
    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();
        user = createTestUser();
    }

    @AfterEach
    void tearDown() {
        userDao.delete(user);
    }

    @Nested
    @DisplayName("Create")
    class UserControllerITCreate {
        private UserServiceCreateRequest request;

        @BeforeEach
        void setUp() {
            request = UserServiceCreateRequest.builder()
                    .email("create@test.com")
                    .password("password")
                    .username("ItUsername")
                    .build();
        }

        @Test
        @DisplayName("Correct")
        void correct() throws Exception {
            String JsonRequest = mapper.writeValueAsString(request);

            mockMvc.perform(post("/user/create/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(JsonRequest))
                    .andExpect(status().isOk())
                    .andExpect(content().string("User has been created"));
        }

        @Test
        @DisplayName("Duplicate error")
        void duplicateError() throws Exception {
            request.setEmail("test@test.com");

            String JsonRequest = mapper.writeValueAsString(request);

            mockMvc.perform(post("/user/create/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .accept(MediaType.APPLICATION_JSON)
                    .content(JsonRequest))
                    .andExpect(status().is(409))
                    .andExpect(jsonPath("message",
                            is("User with 'emile' : 'test@test.com' exists in the database")));
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
                    .with(httpBasic("test@test.com", "password")))
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
                    .andExpect(jsonPath("message", is("User not found by email : 'invalid@test.com'")))
                    .andExpect(jsonPath("httpStatus", is("NOT_FOUND")));
        }
    }

    @Nested
    @DisplayName("Change password")
    class UserControllerITChangePassword {
        private final String URL = "/user/changePassword/";

        @Test
        @DisplayName("Correct")
        void correct() throws Exception {
            User user = userDao.findByEmail("test@test.com").orElseThrow(RuntimeException::new);
            ConfirmationToken token = tokenService.createTokenForUser(user, TokenType.PASSWORD);
            UserServiceChangePasswordRequest request = new UserServiceChangePasswordRequest(
                    token.getConfirmationToken(),
                    "password",
                    "password2021");

            mockMvc.perform(put(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
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
                    .characterEncoding("UTF-8")
                    .content(mapper.writeValueAsString(request)))
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("message", is("Token not found by token : '" + request.getToken() + "'")))
                    .andExpect(jsonPath("httpStatus", is("NOT_FOUND")));
        }

        @Test
        @DisplayName("Invalid old password")
        void invalidOldPassword() throws Exception {
            User user = userDao.findByEmail("test@test.com").orElseThrow(RuntimeException::new);
            ConfirmationToken token = tokenService.createTokenForUser(user, TokenType.PASSWORD);
            UserServiceChangePasswordRequest request = new UserServiceChangePasswordRequest(
                    token.getConfirmationToken(),
                    "password2021",
                    "password2021");

            mockMvc.perform(put(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(mapper.writeValueAsString(request)))
                    .andExpect(status().is(409))
                    .andExpect(jsonPath("message",
                            is("Invalid value of 'Old password' : '" + request.getOldPassword() + "' ")))
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
                    .with(httpBasic("test@test.com", "password"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(mapper.writeValueAsString(request)))
                    .andExpect(content().string("User profile has been updated"))
                    .andExpect(status().isOk()).andReturn();
        }

        @Test
        @DisplayName("Invalid profile img name")
        void InvalidProfileImg() throws Exception {
            request.setProfileImgName("Test");

            mockMvc.perform(put(URL)
                    .with(httpBasic("test@test.com", "password"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(mapper.writeValueAsString(request)))
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("message", is("Profile img not found by name : 'standard'")))
                    .andExpect(jsonPath("httpStatus", is("NOT_FOUND")));
        }

        @Test
        @DisplayName("Unauthorized")
        void unauthorized() throws Exception {
            mockMvc.perform(put(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(mapper.writeValueAsString(request)))
                    .andExpect(status().is(401));
        }
    }

    private User createTestUser() {
        Authority user = authorityDao.findByRole("ROLE_USER").orElseThrow(RuntimeException::new);
        ProfileImg profileImg = profileImgDao.findByName("SpiderMan").orElseThrow(RuntimeException::new);

        return userDao.save(User.builder()
                .username("ItTestUser")
                .email("test@test.com")
                .password(passwordEncoder.encode("password"))
                .enabled(true)
                .profileImg(profileImg)
                .authority(user).build());
    }
}