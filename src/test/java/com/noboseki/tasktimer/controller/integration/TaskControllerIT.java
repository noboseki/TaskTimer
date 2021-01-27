package com.noboseki.tasktimer.controller.integration;

import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.ExceptionTextConstants;
import com.noboseki.tasktimer.repository.TaskDao;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TaskControllerIT extends BaseControllerTest {
    private final String PASSWORD = "password";

    @Autowired
    private TaskDao taskDao;

    private User user;

    @BeforeEach
    void setUp() {
        super.setUp();
        user = createTestUser(PASSWORD);
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
        private final String TASKNAME = "Task";

        @Test
        @DisplayName("Correct")
        void correct() throws Exception {
            mockMvc.perform(post(URL)
                    .with(httpBasic(user.getEmail(), PASSWORD))
                    .content(TASKNAME))
                    .andExpect(status().isOk())
                    .andExpect(content().string(TASKNAME + " has been created"));

            assertTrue(taskDao.findByNameAndUser(TASKNAME, user).isPresent());
        }

        @Test
        @DisplayName("Unauthorized")
        void unauthorized() throws Exception {
            mockMvc.perform(post(URL)
                    .content(TASKNAME))
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
            String test1 = "test 1";
            taskDao.save(Task.builder().name(test1).user(user).build());
            taskDao.save(Task.builder().name("test 2").user(user).complete(true).build());
            taskDao.save(Task.builder().name("test 3").user(user).build());

            mockMvc.perform(get(URL)
                    .with(httpBasic(user.getEmail(), PASSWORD)))
                    .andExpect(status().isOk())
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
                    .with(httpBasic(user.getEmail(), PASSWORD))
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
                    .with(httpBasic(user.getEmail(), PASSWORD))
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
        private final String URL = "/task/test";

        @Test
        @DisplayName("Correct")
        void correct() throws Exception {
            taskDao.save(Task.builder().name("test").user(user).build());

            mockMvc.perform(delete(URL)
                    .with(httpBasic(user.getEmail(), PASSWORD)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("test has been deleted"));

            assertTrue(taskDao.findByNameAndUser("test", user).isEmpty());
        }

        @Test
        @DisplayName("Invalid task name")
        void invalidTaskName() throws Exception {
            mockMvc.perform(delete(URL)
                    .with(httpBasic(user.getEmail(), PASSWORD)))
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("message",
                            is(ExceptionTextConstants.resourceNotFound("Task", "name", "test"))))
                    .andExpect(jsonPath("httpStatus", is("NOT_FOUND")));
        }

        @Test
        @DisplayName("Unauthorized")
        void unauthorized() throws Exception {
            mockMvc.perform(delete(URL))
                    .andExpect(status().is(401));
        }
    }

}