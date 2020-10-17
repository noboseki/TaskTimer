package com.noboseki.tasktimer.controller.integration;

import com.noboseki.tasktimer.controller.ClassCreator;
import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import com.noboseki.tasktimer.repository.TaskDao;
import com.noboseki.tasktimer.repository.UserDao;
import com.noboseki.tasktimer.repository.SessionDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class ControllerIntegrationTest {
    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    protected String userName = "user";
    protected String adminName = "admin";
    protected String userPassword = "password";
    protected String adminPassword = "admin";

    @Autowired
    ClassCreator classCreator;

    @Autowired
    TaskDao taskDao;

    @Autowired
    UserDao userDao;

    @Autowired
    SessionDao sessionDao;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();
    }

    protected MvcResult getValidUnauthorized(String url) throws Exception {
        return mockMvc.perform(get(url))
                .andExpect(status().is(401)).andReturn();
    }

    protected MvcResult getValidNotFound(String url, String username, String password) throws Exception {
        return mockMvc.perform(get(url)
                    .with(httpBasic(username, password)))
                .andExpect(status().is(404)).andReturn();
    }

    protected MvcResult deleteCorrect(String url, String username, String password) throws Exception {
        return mockMvc.perform(delete(url)
                .with(httpBasic(username, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success",is(true)))
                .andExpect(jsonPath("message",is("User has been deleted"))).andReturn();
    }

    protected MvcResult deleteValidUnauthorized(String url) throws Exception {
        return mockMvc.perform(delete(url))
                .andExpect(status().is(401)).andReturn();
    }

    protected MvcResult deleteValidNotFound(String url, String username, String password) throws Exception {
        return mockMvc.perform(delete(url)
                    .with(httpBasic(username, password)))
                .andExpect(status().is(404)).andReturn();
    }

    protected User getUserByName( String username) {
        return userDao.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User: ", "name", username));
    }

    public static Stream<Arguments> getAllRoles(){
        return Stream.of(Arguments.of("admin", "spring"),
                Arguments.of("user", "password"));
    }
}
