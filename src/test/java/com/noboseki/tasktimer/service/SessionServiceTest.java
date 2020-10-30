package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.Session;
import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.playload.ApiResponse;
import com.noboseki.tasktimer.playload.CreateSessionRequest;
import com.noboseki.tasktimer.playload.GetByDateSessionResponse;
import com.noboseki.tasktimer.playload.GetByTaskSessionResponse;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        task = Task.builder()
                .id(UUID.randomUUID())
                .name("Test name")
                .complete(false)
                .user(user).build();

        when(userDao.findByEmail(anyString())).thenReturn(Optional.of(user));
    }

    @Nested
    @DisplayName("Create")
    class SessionServiceTestCreate  {
        private CreateSessionRequest request;

        @BeforeEach
        void setUp() {
            request = new CreateSessionRequest(LocalDate.now().toString(), LocalTime.of(2,5,20).toString());
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

    @Nested
    @DisplayName("Get by Task")
    class SessionServiceTestGetByTask {

        @Test
        @DisplayName("Correct")
        void getByTaskCorrect() {
            //Given
            Session session = Session.builder()
                    .date(Date.valueOf(LocalDate.now()))
                    .time(Time.valueOf(LocalTime.of(2,5)))
                    .task(task).build();

            List<Session> sessions = new ArrayList<>();
            sessions.add(session);

            //When
            when(taskDao.findByNameAndUser(anyString(),any(User.class))).thenReturn(Optional.of(task));
            when(sessionDao.findAllByTask(any(Task.class))).thenReturn(sessions);
            ResponseEntity<List<GetByTaskSessionResponse>> responses = service.getAllByTask(user, TEST_NAME);
            GetByTaskSessionResponse response = responses.getBody().get(0);

            //Then
            assertEquals(1,responses.getBody().size());
            assertEquals(session.getDate(), response.getDate());
            assertEquals(session.getTime(), response.getTime());
            verify(userDao, times(1)).findByEmail(anyString());
            verify(taskDao, times(1)).findByNameAndUser(anyString(),any(User.class));
            verify(sessionDao, times(1)).findAllByTask(any(Task.class));
        }

        @Test
        @DisplayName("Empty")
        void getByTaskEmpty() {
            //Given
            List<Session> sessions = new ArrayList<>();

            //When
            when(taskDao.findByNameAndUser(anyString(),any(User.class))).thenReturn(Optional.of(task));
            when(sessionDao.findAllByTask(any(Task.class))).thenReturn(sessions);
            ResponseEntity<List<GetByTaskSessionResponse>> responses = service.getAllByTask(user, TEST_NAME);

            //Then
            assertEquals(0,responses.getBody().size());
            verify(userDao, times(1)).findByEmail(anyString());
            verify(taskDao, times(1)).findByNameAndUser(anyString(),any(User.class));
            verify(sessionDao, times(1)).findAllByTask(any(Task.class));
        }

        @Test
        @DisplayName("User Not Found")
        void getByTaskUserNotFound() {
            testUserNotFound(() -> service.getAllByTask(user, TEST_NAME));
        }

        @Test
        @DisplayName("Task Not Found")
        void getByTaskTaskNotFound() {
            testTaskNotFound(() -> service.getAllByTask(user, TEST_NAME));
        }
    }

    @Nested
    @DisplayName("Get by Date")
    class SessionServiceTestGetByDate {

        @Test
        @DisplayName("Correct")
        void getByTaskCorrect() {
            //Given
            Session session = Session.builder()
                    .date(Date.valueOf(LocalDate.now()))
                    .time(Time.valueOf(LocalTime.of(2,5)))
                    .task(task).build();

            List<Session> sessions = new ArrayList<>();
            sessions.add(session);

            //When
            when(sessionDao.findAllByTask_User_EmailAndDate(anyString(),any(Date.class))).thenReturn(sessions);
            ResponseEntity<List<GetByDateSessionResponse>> responses = service.getByDate(user, Date.valueOf(LocalDate.now()));
            GetByDateSessionResponse response = responses.getBody().get(0);

            //Then
            assertEquals(1,responses.getBody().size());
            assertEquals(session.getDate(), response.getDate());
            assertEquals(session.getTime(), response.getTime());
            assertEquals(session.getTask().getName(), response.getTaskName());
            verify(userDao, times(1)).findByEmail(anyString());
            verify(sessionDao, times(1)).findAllByTask_User_EmailAndDate(anyString(),any(Date.class));
        }

        @Test
        @DisplayName("Empty")
        void getByTaskEmpty() {
            //Given
            List<Session> sessions = new ArrayList<>();

            //When
            when(sessionDao.findAllByTask_User_EmailAndDate(anyString(),any(Date.class))).thenReturn(sessions);
            ResponseEntity<List<GetByDateSessionResponse>> responses = service.getByDate(user, Date.valueOf(LocalDate.now()));

            //Then
            assertEquals(0,responses.getBody().size());
            verify(userDao, times(1)).findByEmail(anyString());
            verify(sessionDao, times(1)).findAllByTask_User_EmailAndDate(anyString(),any(Date.class));
        }

        @Test
        @DisplayName("User Not Found")
        void getByTaskUserNotFound() {
            testUserNotFound(() -> service.getByDate(user, Date.valueOf(LocalDate.now())));
        }
    }
}
