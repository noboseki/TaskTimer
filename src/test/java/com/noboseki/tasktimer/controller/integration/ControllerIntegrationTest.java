package com.noboseki.tasktimer.controller.integration;

import com.google.gson.Gson;
import com.noboseki.tasktimer.controller.ClassCreator;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.repository.SessionDao;
import com.noboseki.tasktimer.repository.TaskDao;
import com.noboseki.tasktimer.repository.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public abstract class ControllerIntegrationTest {

    protected final String TEST_NAME = "Test Name";

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    protected Gson gson = new Gson();

    @Autowired
    TaskDao taskDao;

    @Autowired
    UserDao userDao;

    @Autowired
    SessionDao sessionDao;

    protected User admin;
    protected User user;

    @BeforeEach
    void setUp() {
        user = userDao.findByUsername("user").orElseThrow();
        admin = userDao.findByUsername("admin").orElseThrow();
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();
    }

    protected MvcResult useBasicMvc(HttpMethod httpMethod, String url,
                                  Integer status) throws Exception {
        switch (httpMethod) {
            case POST:
                return mockMvc.perform(post(url))
                        .andExpect(status().is(status)).andReturn();
            case GET:
                return mockMvc.perform(get(url))
                        .andExpect(status().is(status)).andReturn();
            case PUT:
                return mockMvc.perform(put(url))
                        .andExpect(status().is(status)).andReturn();
            case DELETE:
                return mockMvc.perform(delete(url))
                        .andExpect(status().is(status)).andReturn();
            default:
                throw new RuntimeException();
        }
    }
}
