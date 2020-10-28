package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import com.noboseki.tasktimer.playload.ApiResponse;
import com.noboseki.tasktimer.repository.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringJUnitWebConfig
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class ServiceSetupClass {
    protected final String TEST_NAME = "TestName";
    protected final String TEST_PASSWORD = "TestPassword";
    protected final String TEST_EMAIL = "test@email.com";
    protected final String TEST_IMAGE = "test.url.com";

    @Mock
    protected UserDao userDao;

    protected User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username(TEST_NAME)
                .publicId(10001L)
                .password(TEST_PASSWORD)
                .email(TEST_EMAIL)
                .imageUrl(TEST_IMAGE).build();
    }

    protected void checkApiResponse(ApiResponse response, String message, boolean isTrue) {
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.isSuccess()).isEqualTo(isTrue);
    }

    protected void testUserNotFound(Executable executable) {
        when(userDao.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, executable);
    }
}
