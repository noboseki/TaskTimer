package com.noboseki.tasktimer.controller.integration;

import com.noboseki.tasktimer.domain.Session;
import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.ExceptionTextConstants;
import com.noboseki.tasktimer.playload.SessionServiceCreateRequest;
import com.noboseki.tasktimer.repository.SessionDao;
import com.noboseki.tasktimer.repository.TaskDao;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SessionControllerIT extends BaseControllerTest {
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private SessionDao sessionDao;

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        super.setUp();
        user = createTestUser("password");
        task = Task.builder().name("Test").user(user).build();
        task = taskDao.save(task);
    }

    @AfterEach
    void tearDown() {
        sessionDao.deleteAll(sessionDao.findAllByTask(task));
        if (task != null) {
            taskDao.delete(task);
        } if(user != null) {
            userDao.delete(user);
        }
    }

    @Nested
    @DisplayName("Create")
    class SessionControllerITCreate {
        private final String URL = "/session/create/";
        SessionServiceCreateRequest request;

        @BeforeEach
        void setUp() {
            request = new SessionServiceCreateRequest();
            request.setDate("2020-10-20");
            request.setTime("00:40:00");
            request.setTaskName(task.getName());
        }

        @Test
        @DisplayName("Correct")
        void correct() throws Exception {
            String jsonRequest = mapper.writeValueAsString(request);

            mockMvc.perform(post(URL)
                    .with(httpBasic(user.getEmail(), "password"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonRequest))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Session has been created"));

            List<Session> sessions = sessionDao.findAllByTask(task);

            assertEquals("2020-10-20", sessions.get(0).getDate().toString());
            assertEquals("00:40:00", sessions.get(0).getTime().toString());
        }

        @Test
        @DisplayName("Unauthorized")
        void unauthorized() throws Exception {
            String jsonRequest = mapper.writeValueAsString(request);

            mockMvc.perform(post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonRequest)).andExpect(status().is(401));
        }

        @Test
        @DisplayName("Invalid task name")
        void invalidTaskName() throws Exception {
            request.setTaskName("Invalid task name");
            String jsonRequest = mapper.writeValueAsString(request);

            mockMvc.perform(post(URL)
                    .with(httpBasic(user.getEmail(), "password"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonRequest))
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("message",
                            is(ExceptionTextConstants.resourceNotFound("Task", "name", "Invalid task name"))))
                    .andExpect(jsonPath("httpStatus", is("NOT_FOUND")));
        }
    }

    @Nested
    @DisplayName("Get by task")
    class SessionControllerITGetByTask {
        private final String URL = "/session/getByTask/";

        @Test
        @DisplayName("Correct")
        void correct() throws Exception {
            sessionDao.save(Session.builder()
                    .date(Date.valueOf("2020-10-20"))
                    .time(Time.valueOf("00:40:00"))
                    .task(task).build());

            sessionDao.save(Session.builder()
                    .date(Date.valueOf("2020-10-21"))
                    .time(Time.valueOf("00:50:00"))
                    .task(task).build());

            mockMvc.perform(get(URL + task.getName() + "/")
                    .with(httpBasic(user.getEmail(), "password")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].date", is("2020-10-20")))
                    .andExpect(jsonPath("$[0].time", is("00:40:00")))
                    .andExpect(jsonPath("$[1].date", is("2020-10-21")))
                    .andExpect(jsonPath("$[1].time", is("00:50:00")));
        }

        @Test
        @DisplayName("Correct empty")
        void correctEmpty() throws Exception {
            mockMvc.perform(get(URL + task.getName() + "/")
                    .with(httpBasic(user.getEmail(), "password")))
                    .andExpect(status().isOk())
                    .andExpect(content().string("[]"));
        }

        @Test
        @DisplayName("Invalid task name")
        void invalidTaskName() throws Exception {
            mockMvc.perform(get(URL + "/invalid/")
                    .with(httpBasic(user.getEmail(), "password")))
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("message", is(ExceptionTextConstants.resourceNotFound("Task", "name", "invalid"))))
                    .andExpect(jsonPath("httpStatus", is("NOT_FOUND")));
        }

        @Test
        @DisplayName("Unauthorized")
        void unauthorized() throws Exception {
            mockMvc.perform(get(URL + task.getName() + "/"))
                    .andExpect(status().is(401));
        }
    }

    @Nested
    @DisplayName("Get chain by date")
    class SessionControllerITGetChainByDate {
        private final String URL = "/session/getChainByDate/";

        @Test
        @DisplayName("Correct empty session")
        void correctEmptySession() throws Exception {
            mockMvc.perform(get(URL + "2020-10-10/2020-10-10/")
                    .with(httpBasic(user.getEmail(), "password")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.dateLabel[0]", is("2020-10-10")));
        }

        @Test
        @DisplayName("Correct")
        void correct() throws Exception {
            sessionDao.save(Session.builder()
                    .date(Date.valueOf("2020-10-20"))
                    .time(Time.valueOf("00:40:00"))
                    .task(task).build());

            sessionDao.save(Session.builder()
                    .date(Date.valueOf("2020-10-21"))
                    .time(Time.valueOf("00:50:00"))
                    .task(task).build());

            mockMvc.perform(get(URL + "2020-10-20/2020-10-21/")
                    .with(httpBasic(user.getEmail(), "password")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.dateLabel[0]", is("2020-10-20")))
                    .andExpect(jsonPath("$.dateLabel[1]", is("2020-10-21")))
                    .andExpect(jsonPath("$.dataList[0].data[0]", is(0.67)))
                    .andExpect(jsonPath("$.dataList[0].data[1]", is(0.83)))
                    .andExpect(jsonPath("$.dataList[0].taskName", is("Test")));
        }

        @Test
        @DisplayName("Unauthorized")
        void unauthorized() throws Exception {
            mockMvc.perform(get(URL + "2020-10-10/2020-10-10/"))
                    .andExpect(status().is(401));
        }

        @Test
        @DisplayName("Invalid date")
        void invalidDate() throws Exception {
            mockMvc.perform(get(URL + "2020-100-10/2020-100-10/")
                    .with(httpBasic(user.getEmail(), "password")))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("message",
                            is(ExceptionTextConstants.dateTime("Date", "2020-100-10 or 2020-100-10"))))
                    .andExpect(jsonPath("httpStatus", is("BAD_REQUEST")));
        }
    }

    @Nested
    @DisplayName("Get table by date")
    class SessionControllerITGetTableByDate {
        private final String URL = "/session/getTableByDate/";

        @Test
        @DisplayName("Correct empty")
        void correctEmpty() throws Exception {
            mockMvc.perform(get(URL + "2020-10-20/2020-10-21/")
                    .with(httpBasic(user.getEmail(), "password")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].date", is("2020-10-20")))
                    .andExpect(jsonPath("$[0].time", is("00:00")))
                    .andExpect(jsonPath("$[0].sessions", is(0)))
                    .andExpect(jsonPath("$[1].date", is("2020-10-21")))
                    .andExpect(jsonPath("$[1].time", is("00:00")))
                    .andExpect(jsonPath("$[1].sessions", is(0)));
        }

        @Test
        @DisplayName("Correct")
        void correct() throws Exception {
            sessionDao.save(Session.builder()
                    .date(Date.valueOf("2020-10-20"))
                    .time(Time.valueOf("00:40:00"))
                    .task(task).build());

            sessionDao.save(Session.builder()
                    .date(Date.valueOf("2020-10-21"))
                    .time(Time.valueOf("00:50:00"))
                    .task(task).build());

            mockMvc.perform(get(URL + "2020-10-20/2020-10-21/")
                    .with(httpBasic(user.getEmail(), "password")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].date", is("2020-10-20")))
                    .andExpect(jsonPath("$[0].time", is("00:40")))
                    .andExpect(jsonPath("$[0].sessions", is(1)))
                    .andExpect(jsonPath("$[1].date", is("2020-10-21")))
                    .andExpect(jsonPath("$[1].time", is("00:50")))
                    .andExpect(jsonPath("$[1].sessions", is(1)));
        }

        @Test
        @DisplayName("Unauthorized")
        void unauthorized() throws Exception {
            mockMvc.perform(get(URL + "2020-10-20/2020-10-21/"))
                    .andExpect(status().is(401));
        }

        @Test
        @DisplayName("Invalid date")
        void invalidDate() throws Exception {
            mockMvc.perform(get(URL + "2020-100-10/2020-100-10/")
                    .with(httpBasic(user.getEmail(), "password")))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("message",
                            is(ExceptionTextConstants.dateTime("Date", "2020-100-10 or 2020-100-10"))))
                    .andExpect(jsonPath("httpStatus", is("BAD_REQUEST")));
        }
    }
}
