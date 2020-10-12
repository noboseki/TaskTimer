package com.noboseki.tasktimer.controller.integration;

import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.domain.WorkTime;
import com.noboseki.tasktimer.playload.ApiResponse;
import com.noboseki.tasktimer.repository.TaskDao;
import com.noboseki.tasktimer.repository.UserDao;
import com.noboseki.tasktimer.repository.WorkTimeDao;
import com.noboseki.tasktimer.service.TaskService;
import com.noboseki.tasktimer.service.UserService;
import com.noboseki.tasktimer.service.WorkTimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class ControllerIntegrationTest {
    @Autowired
    WebApplicationContext wac;

    @Value("${noboseki.security.admin.name}")
    String adminName;

    @Value("${noboseki.security.admin.password}")
    String adminPassword;

    @Value("${noboseki.security.user.name}")
    String userName;

    @Value("${noboseki.security.user.password}")
    String userPassword;

    MockMvc mockMvc;
    User.UserDto userDto;
    String uuid ;
    Task.TaskDto taskDto;
    WorkTime.WorkTimeDto workTimeDto;
    ResponseEntity<ApiResponse> response = ResponseEntity.ok(new ApiResponse(true,"Test Ok"));

    @MockBean
    UserService userService;

    @MockBean
    TaskDao taskDao;

    @MockBean
    TaskService taskService;

    @MockBean
    UserDao userDao;

    @MockBean
    WorkTimeDao workTimeDao;

    @MockBean
    WorkTimeService workTimeService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();

        userDto = User.UserDto.builder()
                .privateID(UUID.randomUUID())
                .publicId(1L)
                .email("test@test.com")
                .emailVerified(true)
                .imageUrl("test")
                .password("password").build();

        taskDto = Task.TaskDto.builder()
                .privateID(UUID.randomUUID())
                .name("Test")
                .complete(true).build();

        workTimeDto = WorkTime.WorkTimeDto.builder()
                .privateID(UUID.randomUUID())
                .date(Date.valueOf(LocalDate.now()))
                .time(Time.valueOf(LocalTime.now())).build();

        uuid = UUID.randomUUID().toString();
    }

    protected MvcResult getValidUnauthorized(String url) throws Exception {
        return mockMvc.perform(get(url))
                .andExpect(status().is(401)).andReturn();
    }

    protected MvcResult getValidNotFound(String url) throws Exception {
        return mockMvc.perform(get(url)
                .with(httpBasic(userName, userPassword)))
                .andExpect(status().is(404)).andReturn();
    }

    protected MvcResult deleteCorrect(String url) throws Exception {
        return mockMvc.perform(delete(url)
                .with(httpBasic(userName, userPassword)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success",is(true)))
                .andExpect(jsonPath("message",is("Test Ok"))).andReturn();
    }

    protected MvcResult deleteValidUnauthorized(String url) throws Exception {
        return mockMvc.perform(delete(url))
                .andExpect(status().is(401)).andReturn();
    }

    protected MvcResult deleteValidNotFound(String url) throws Exception {
        return mockMvc.perform(delete(url)
                    .with(httpBasic(userName, userPassword)))
                .andExpect(status().is(404)).andReturn();
    }
}
