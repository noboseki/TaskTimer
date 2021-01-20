package com.noboseki.tasktimer.controller.integration;

import com.noboseki.tasktimer.domain.Authority;
import com.noboseki.tasktimer.domain.ProfileImg;
import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.repository.AuthorityDao;
import com.noboseki.tasktimer.repository.ProfileImgDao;
import com.noboseki.tasktimer.repository.TaskDao;
import com.noboseki.tasktimer.repository.UserDao;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class TaskControllerIT {
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private UserDao userDao;
    @Autowired
    private AuthorityDao authorityDao;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ProfileImgDao profileImgDao;
    @Autowired
    private TaskDao taskDao;

    private User user;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();
        user = createTestUser();
    }

    @AfterEach
    void tearDown() {
        List<Task> tasks = taskDao.findAllByUser(user);
        taskDao.deleteAll(tasks);
        userDao.delete(user);
    }

    @Nested
    @DisplayName("Create")
    class TaskControllerITCreate {
        private final String URL = "/task/";

        @Test
        @DisplayName("Correct")
        void correct() throws Exception {
            mockMvc.perform(post(URL)
                    .with(httpBasic("test@test.com", "password"))
                    .content("test"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("test has been created"));

            assertTrue(taskDao.findByNameAndUser("test", user).isPresent());
        }

        @Test
        @DisplayName("Unauthorized")
        void unauthorized() throws Exception {
            mockMvc.perform(post(URL)
                    .content("test task"))
                    .andExpect(status().is(401));
        }
    }

    @Nested
    @DisplayName("Get tasks")
    class TaskControllerITGetTasks {
        private final String URL = "/task/getTasks/";

        @Test
        @DisplayName("Correct")
        void correct() throws Exception {
            taskDao.save(Task.builder().name("test 1").user(user).build());
            taskDao.save(Task.builder().name("test 2").user(user).complete(true).build());
            taskDao.save(Task.builder().name("test 3").user(user).build());

            mockMvc.perform(get(URL)
                    .with(httpBasic("test@test.com", "password")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].taskName", is("test 1")))
                    .andExpect(jsonPath("$[1].taskName", is("test 2")))
                    .andExpect(jsonPath("$[2].taskName", is("test 3")))
                    .andExpect(jsonPath("$[0].complete", is(false)))
                    .andExpect(jsonPath("$[1].complete", is(true)))
                    .andExpect(jsonPath("$[2].complete", is(false)));
        }

        @Test
        @DisplayName("Unauthorized")
        void unauthorized() throws Exception {
            mockMvc.perform(get(URL))
                    .andExpect(status().is(401));
        }
    }

    @Nested
    @DisplayName("Change task complete")
    class TaskControllerITChangeTaskComplete {
        private final String URL = "/task/changeTaskComplete/";

        @Test
        @DisplayName("Correct")
        void correct() throws Exception {
            taskDao.save(Task.builder().name("test").user(user).build());

            mockMvc.perform(put(URL)
                    .with(httpBasic(user.getEmail(), "password"))
                    .content("test"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("test complete changed to true"));

            Task task = taskDao.findByNameAndUser("test", user).orElseThrow();
            assertTrue(task.getComplete());
        }

        @Test
        @DisplayName("Unauthorized")
        void unauthorized() throws Exception {
            mockMvc.perform(put(URL)
                    .content("test"))
                    .andExpect(status().is(401));
        }
    }

    @Nested
    @DisplayName("Change task archive")
    class TaskControllerITChangeTaskArchive {
        private final String URL = "/task/changeTaskArchive/";

        @Test
        @DisplayName("Correct")
        void correct() throws Exception {
            taskDao.save(Task.builder().name("test").user(user).build());

            mockMvc.perform(put(URL)
                    .with(httpBasic(user.getEmail(), "password"))
                    .content("test"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("test archive changed to true"));

            Task task = taskDao.findByNameAndUser("test", user).orElseThrow();
            assertTrue(task.getArchived());
        }

        @Test
        @DisplayName("Unauthorized")
        void unauthorized() throws Exception {
            mockMvc.perform(get(URL)
                    .content("test"))
                    .andExpect(status().is(401));
        }
    }

    @Nested
    @DisplayName("Delete")
    class TaskControllerITDelete {
        private final String URL = "/task/";

        @Test
        @DisplayName("Correct")
        void correct() throws Exception {
            taskDao.save(Task.builder().name("test").user(user).build());

            mockMvc.perform(delete(URL + "/test")
                    .with(httpBasic(user.getEmail(), "password")))
                    .andExpect(status().isOk())
                    .andExpect(content().string("test has been deleted"));

            assertTrue(taskDao.findByNameAndUser("test", user).isEmpty());
        }

        @Test
        @DisplayName("Invalid task name")
        void invalidTaskName() throws Exception {
            mockMvc.perform(delete(URL + "/test")
                    .with(httpBasic(user.getEmail(), "password")))
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("message", is("Task not found by name : 'test'")))
                    .andExpect(jsonPath("httpStatus", is("NOT_FOUND")));
        }

        @Test
        @DisplayName("Unauthorized")
        void unauthorized() throws Exception {
            mockMvc.perform(delete(URL + "/test"))
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

//    private final String POST_URL = "/task/create/";
//    private final String GET_URL = "/task/get/";
//    private final String GET_ALL_URL = "/task/getAll/";
//    private final String PUT_UPDATE_NAME = "/task/updateName/";
//    private final String PUT_UPDATE_STATUS = "/task/updateStatus/";
//    private final String DELETE_URL = "/task/delete/";
//
//    @Override
//    @BeforeEach
//    void setUp() {
//        super.setUp();
//        Task task = Task.builder()
//                .name(TEST_NAME)
//                .user(user).build();
//
//        taskDao.save(task);
//    }
//
//    @Nested
//    @DisplayName("Create")
//    class TaskControllerITCreate extends ControllerIntegrationTest {
//
//        @Test
//        @DisplayName("Correct")
//        @WithUserDetails("user@test.com")
//        void createCorrect() throws Exception {
//            //Then
//            mvcCreateForm(POST_URL + "New Test",true, "Task has been created");
//        }
//
//        @Test
//        @DisplayName("Duplicate name")
//        @WithUserDetails("user@test.com")
//        void createDuplicateName() throws Exception {
//            //Then
//            mvcCreateForm(POST_URL + TEST_NAME ,false, "Duplicate task name");
//        }
//
//        @Test
//        @DisplayName("Unauthorized")
//        void createUnauthorized() throws Exception {
//            //Then
//            useBasicMvc(HttpMethod.POST, POST_URL + TEST_NAME, 401);
//        }
//
//        private MvcResult mvcCreateForm(String url,boolean success, String message) throws Exception {
//            return mockMvc.perform(post(url))
//                        .andExpect(status().isOk())
//                    .andExpect(jsonPath("success",is(success)))
//                    .andExpect(jsonPath("message",is(message))).andReturn();
//        }
//    }
//
//    @Nested
//    @DisplayName("Get")
//    class TaskControllerITGet extends ControllerIntegrationTest {
//
//        @Test
//        @DisplayName("Correct")
//        @WithUserDetails("user@test.com")
//        void getCorrect() throws Exception {
//            //Then
//            mockMvc.perform(get(GET_URL + TEST_NAME))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("name",is(TEST_NAME)))
//                    .andExpect(jsonPath("complete",is(false)));
//        }
//
//        @Test
//        @DisplayName("Resource not found")
//        @WithUserDetails("user@test.com")
//        void getResourceNotFound() throws Exception {
//            //Then
//            useBasicMvc(HttpMethod.GET, GET_URL + "", 404);
//        }
//
//        @Test
//        @DisplayName("Unauthorized")
//        void getUnauthorized() throws Exception {
//            //Then
//            useBasicMvc(HttpMethod.GET, GET_URL + TEST_NAME, 401);
//        }
//    }
//
//    @Nested
//    @DisplayName("GetAll")
//    class TaskControllerITGetAll extends ControllerIntegrationTest {
//
//        @Test
//        @DisplayName("Correct")
//        @WithUserDetails("user@test.com")
//        void getAllCorrect() throws Exception {
//            //Then
//            mockMvc.perform(get(GET_ALL_URL))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.[1].name",is(TEST_NAME)))
//                    .andExpect(jsonPath("$.[1].complete",is(false)));
//        }
//
//        @Test
//        @DisplayName("Unauthorized")
//        void getAllUnauthorized() throws Exception {
//            //Then
//            useBasicMvc(HttpMethod.GET, GET_ALL_URL, 401);
//        }
//    }
//
//    @Nested
//    @DisplayName("Update Name")
//    class TaskControllerITUpdateName extends ControllerIntegrationTest {
//
//        @Test
//        @DisplayName("Correct")
//        @WithUserDetails("user@test.com")
//        void UpdateNameCorrect() throws Exception {
//            mvcUpdateNameForm(PUT_UPDATE_NAME + TEST_NAME + "/New Name", true, "Task name has been updated");
//        }
//
//        @Test
//        @DisplayName("Duplicate")
//        @WithUserDetails("user@test.com")
//        void UpdateNameDuplicate() throws Exception {
//            mvcUpdateNameForm(PUT_UPDATE_NAME + TEST_NAME + "/" + TEST_NAME, false, "Duplicate task name");
//        }
//
//        @Test
//        @DisplayName("Task not found")
//        @WithUserDetails("user@test.com")
//        void UpdateNameTaskNotFound() throws Exception {
//            useBasicMvc(HttpMethod.PUT, PUT_UPDATE_NAME + "not found/New Name", 404);
//        }
//
//        @Test
//        @DisplayName("Unauthorized")
//        void UpdateNameUnauthorized() throws Exception {
//            //Then
//            useBasicMvc(HttpMethod.PUT, PUT_UPDATE_NAME + TEST_NAME + "/New Name", 401);
//        }
//
//        private MvcResult mvcUpdateNameForm(String url, boolean success, String message) throws Exception {
//            return mockMvc.perform(put(url))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("success",is(success)))
//                    .andExpect(jsonPath("message",is(message))).andReturn();
//        }
//    }
//
//    @Nested
//    @DisplayName("Update Status")
//    class TaskControllerITUpdateStatus extends ControllerIntegrationTest {
//
//        @Test
//        @DisplayName("Correct")
//        @WithUserDetails("user@test.com")
//        void UpdateStatusCorrect() throws Exception {
//            mockMvc.perform(put(PUT_UPDATE_STATUS + TEST_NAME))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("success",is(true)))
//                    .andExpect(jsonPath("message",is("Task status has been updated")));
//        }
//
//        @Test
//        @DisplayName("Task not found")
//        @WithUserDetails("user@test.com")
//        void UpdateStatusTaskNotFound() throws Exception {
//            useBasicMvc(HttpMethod.PUT, PUT_UPDATE_STATUS + "Not Found", 404);
//        }
//
//        @Test
//        @DisplayName("Unauthorized")
//        void UpdateNameUnauthorized() throws Exception {
//            useBasicMvc(HttpMethod.PUT, PUT_UPDATE_STATUS + TEST_NAME, 401);
//        }
//    }
//
//    @Nested
//    @DisplayName("Delete")
//    class TaskControllerITDelete extends ControllerIntegrationTest {
//
//        @Test
//        @DisplayName("Correct")
//        @WithUserDetails("user@test.com")
//        void DeleteCorrect() throws Exception {
//            mockMvc.perform(delete(DELETE_URL + TEST_NAME))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("success",is(true)))
//                    .andExpect(jsonPath("message",is("Task has been deleted")));
//        }
//
//        @Test
//        @DisplayName("Task not found")
//        @WithUserDetails("user@test.com")
//        void DeleteTaskNotFound() throws Exception {
//            useBasicMvc(HttpMethod.DELETE, DELETE_URL + "Not Found", 404);
//        }
//
//        @Test
//        @DisplayName("Unauthorized")
//        void DeleteUnauthorized() throws Exception {
//            useBasicMvc(HttpMethod.DELETE, DELETE_URL + TEST_NAME, 401);
//        }
//    }
//}
