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

    User user;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        user = userDao.findAll().get(0);
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println(user);
    }

    @Test
    @Order(1)
    @DisplayName("Get correct")
    void getCorrect() throws Exception {
        mockMvc.perform(get("/user/get/" + user.getId().toString())
                    .with(httpBasic(user.getPublicId().toString(), "spring")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId",is(user.getPublicId().intValue())))
                .andExpect(jsonPath("$.email",is(user.getEmail())))
                .andExpect(jsonPath("$.imageUrl",is(user.getImageUrl())))
                .andExpect(jsonPath("$.username",is(user.getUsername())))
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
                user.getPublicId().toString(),
                "password");
    }

    @Test
    @Order(4)
    @DisplayName("Delete correct")
    void deleteCorrect() throws Exception {
        deleteCorrect("/user/delete/" + user.getId().toString(),
                user.getPublicId().toString(),
                "password");
    }
    @Test
    @Order(5)
    @DisplayName("Delete valid not found")
    void deleteValidNotFound() throws Exception {
        deleteValidNotFound("/user/delete/" + UUID.randomUUID().toString(), user.getPublicId().toString(), "password");
    }

    @Test
    @Order(6)
    @DisplayName("Delete valid unauthorized")
    void deleteValidUnauthorized() throws Exception {
        deleteValidUnauthorized("/user/delete/" + UUID.randomUUID().toString());
    }
}