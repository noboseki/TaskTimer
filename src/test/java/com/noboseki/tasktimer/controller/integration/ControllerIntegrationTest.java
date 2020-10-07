package com.noboseki.tasktimer.controller.integration;

import com.noboseki.tasktimer.controller.juint.ControllerMvcMethod;
import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.domain.WorkTime;
import com.noboseki.tasktimer.repository.TaskDao;
import com.noboseki.tasktimer.repository.UserDao;
import com.noboseki.tasktimer.repository.WorkTimeDao;
import com.noboseki.tasktimer.service.TaskService;
import com.noboseki.tasktimer.service.UserService;
import com.noboseki.tasktimer.service.WorkTimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@WebMvcTest
@ExtendWith(SpringExtension.class)
public class ControllerIntegrationTest {
    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;
    User.UserDto userDto;
    Task.TaskDto taskDto;
    WorkTime.WorkTimeDto workTimeDto;

    @Value("${spring.security.user.name}")
    String username;

    @Value("${spring.security.user.password}")
    String password;

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
                .password(password).build();

        taskDto = Task.TaskDto.builder()
                .privateID(UUID.randomUUID())
                .name("Test")
                .complete(true).build();

        workTimeDto = WorkTime.WorkTimeDto.builder()
                .privateID(UUID.randomUUID())
                .date(Date.valueOf(LocalDate.now()))
                .time(Time.valueOf(LocalTime.now())).build();
    }
}
