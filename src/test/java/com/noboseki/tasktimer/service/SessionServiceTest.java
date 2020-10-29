package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.Session;
import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.playload.ApiResponse;
import com.noboseki.tasktimer.playload.CreateSessionRequest;
import com.noboseki.tasktimer.repository.SessionDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class SessionServiceTest extends ServiceSetupClass {

    @Mock
    SessionDao sessionDao;

    @InjectMocks
    SessionService service;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
    }

    @Nested
    @DisplayName("Create")
    class SessionServiceTestCreate  {
        private CreateSessionRequest request;

        @BeforeEach
        void setUp() {
            task = Task.builder()
                    .id(UUID.randomUUID())
                    .name("Test name")
                    .complete(false).build();

            request = new CreateSessionRequest(Date.valueOf(LocalDate.now()), Time.valueOf(LocalTime.of(2,5)));

            when(userDao.findByEmail(anyString())).thenReturn(Optional.of(user));
        }

        @Test
        @DisplayName("Correct")
        void createCorrect() {
            //When
            when(taskDao.findByNameAndUser(anyString(),any(User.class))).thenReturn(Optional.of(task));
            ResponseEntity<ApiResponse> response = service.create(user, TEST_NAME, request);

            //Then
            verify(sessionDao, times(1)).save(any(Session.class));
            checkApiResponse(response.getBody(),"Session has been created", true);
        }

        @Test
        @DisplayName("User Not Found")
        void createUserNotFound() {
            testUserNotFound(() -> service.create(user, TEST_NAME, request));
        }

        @Test
        @DisplayName("Task Not Found")
        void createTaskNotFound() {
            testTaskNotFound(() -> service.create(user, TEST_NAME, request));
        }

        @Test
        @DisplayName("Save error")
        void createSaveError() {
            when(taskDao.findByNameAndUser(anyString(),any(User.class))).thenReturn(Optional.of(task));
            when(sessionDao.save(any(Session.class))).thenThrow(SaveException.class);
            assertThrows(SaveException.class, () -> service.create(user, TEST_NAME, request));
        }
    }
}
