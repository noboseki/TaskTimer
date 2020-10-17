package com.noboseki.tasktimer.controller.integration;

import com.noboseki.tasktimer.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerIntegrationTest extends ControllerIntegrationTest {

    User admin;
    User user;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        user = userDao.findByUsername("user").orElseThrow();
        admin = userDao.findByUsername("admin").orElseThrow();
    }

    @Test
    @Order(1)
    @DisplayName("Get correct")
    void getCorrect() throws Exception {
        mockMvc.perform(get("/user/get/" + admin.getId().toString())
                    .with(httpBasic(admin.getEmail(), "spring")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId",is(admin.getPublicId().intValue())))
                .andExpect(jsonPath("$.email",is(admin.getEmail())))
                .andExpect(jsonPath("$.imageUrl",is(admin.getImageUrl())))
                .andExpect(jsonPath("$.username",is(admin.getUsername())))
                .andReturn();
    }

    @Test
    @Order(2)
    @DisplayName("Get valid unauthorized")
    void getValidUnauthorized() throws Exception {
        getValidUnauthorized("/user/get/" + user.getId().toString());
    }

    @Test
    @Order(3)
    @DisplayName("Get valid not found")
    void getValidNotFound() throws Exception {
        getValidNotFound("/user/get/" + UUID.randomUUID().toString(),
                admin.getEmail(),
                "spring");
    }

    @Test
    @Order(4)
    @DisplayName("Delete correct")
    void deleteCorrect() throws Exception {
        deleteCorrect("/user/delete/" + admin.getId().toString(),
                admin.getEmail(),
                "spring");
    }
    @Test
    @Order(5)
    @DisplayName("Delete valid not found")
    void deleteValidNotFound() throws Exception {
        deleteValidNotFound("/user/delete/" + UUID.randomUUID().toString(), admin.getEmail(), "spring");
    }

    @Test
    @Order(6)
    @DisplayName("Delete valid unauthorized")
    void deleteValidUnauthorized() throws Exception {
        deleteValidUnauthorized("/user/delete/" + UUID.randomUUID().toString());
    }
}