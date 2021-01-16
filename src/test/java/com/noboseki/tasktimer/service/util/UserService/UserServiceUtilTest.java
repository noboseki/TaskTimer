package com.noboseki.tasktimer.service.util.UserService;

import com.noboseki.tasktimer.domain.Authority;
import com.noboseki.tasktimer.domain.ProfileImg;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.playload.UserServiceCreateRequest;
import com.noboseki.tasktimer.playload.UserServiceGetResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringJUnitWebConfig
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class UserServiceUtilTest {
    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserServiceUtil util;

    @Test
    @DisplayName("Map to response")
    void mapToResponse() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("test")
                .email("test@test.com")
                .password("test")
                .profileImg(new ProfileImg())
                .enabled(true).build();

        UserServiceGetResponse response = util.mapToResponse(user);

        assertEquals(response.getPublicId(), user.getPublicId());
        assertEquals(response.getEmail(), user.getEmail());
        assertEquals(response.getUsername(), user.getUsername());
        assertEquals(response.getProfileImg(), user.getProfileImg());
    }

    @Test
    @DisplayName("Map to user")
    void mapToUser() {
        //Given
        UserServiceCreateRequest request = new UserServiceCreateRequest(
                "test@email.com",
                "Password",
                "test name");

        //When
        when(encoder.encode(anyString())).thenReturn("Password");

        User user = util.mapToUser(request, new Authority(), new ProfileImg());

        //Then
        assertEquals(request.getEmail(), user.getEmail());
        assertEquals(request.getPassword(), user.getPassword());
        assertEquals(request.getUsername(), user.getUsername());
        assertNotNull(user.getProfileImg());
    }
}