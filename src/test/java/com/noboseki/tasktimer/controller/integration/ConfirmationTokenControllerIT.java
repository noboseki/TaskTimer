package com.noboseki.tasktimer.controller.integration;

import com.noboseki.tasktimer.domain.*;
import com.noboseki.tasktimer.service.ConfirmationTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ConfirmationTokenControllerIT extends BaseControllerTest {
    @Autowired
    private ConfirmationTokenService tokenService;

    @Nested
    @DisplayName("Activate account")
    class ConfirmationTokenControllerITActivateAccount {
        private final String URL = "/confirm/confirm-account";

        @Test
        @DisplayName("Correct")
        void correct() throws Exception {
            ConfirmationToken token = tokenService.createTokenForUser(createTestUser("password"), TokenType.ACTIVATE);

            mockMvc.perform(get(URL + "?token=" + token.getConfirmationToken()))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("InvalidToken")
        void invalidToken() throws Exception {
            mockMvc.perform(get(URL + "?token=" + UUID.randomUUID().toString()))
                    .andExpect(status().is(409));
        }
    }

}