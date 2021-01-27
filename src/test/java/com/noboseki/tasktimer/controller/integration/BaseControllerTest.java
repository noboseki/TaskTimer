package com.noboseki.tasktimer.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.noboseki.tasktimer.controller.ClassCreator;
import com.noboseki.tasktimer.domain.Authority;
import com.noboseki.tasktimer.domain.ProfileImg;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DirtiesContextBeforeModesTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@Transactional
public abstract class BaseControllerTest {
    protected final String charEncoding = "UTF-8";
    protected final String userPassword = "password";

    @Autowired
    protected WebApplicationContext wac;
    @Autowired
    protected UserDao userDao;
    @Autowired
    private AuthorityDao authorityDao;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ProfileImgDao profileImgDao;

    protected final ObjectMapper mapper = new ObjectMapper();
    protected MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();
    }

    protected User createTestUser(String password) {
        Authority user = authorityDao.findByRole("ROLE_USER").orElseThrow(RuntimeException::new);
        ProfileImg profileImg = profileImgDao.findByName("SpiderMan").orElseThrow(RuntimeException::new);

        return userDao.save(User.builder()
                .username("ItTestUser")
                .email("test@test.com")
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .profileImg(profileImg)
                .authority(user).build());
    }

}
