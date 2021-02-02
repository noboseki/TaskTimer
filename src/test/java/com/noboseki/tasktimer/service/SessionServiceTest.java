package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.Session;
import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.DateTimeException;
import com.noboseki.tasktimer.exeption.ExceptionTextConstants;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.playload.GetByTaskSessionResponse;
import com.noboseki.tasktimer.playload.SessionServiceChainByDateResponse;
import com.noboseki.tasktimer.playload.SessionServiceCreateRequest;
import com.noboseki.tasktimer.playload.SessionServiceTableByDateResponse;
import com.noboseki.tasktimer.repository.SessionDao;
import com.noboseki.tasktimer.service.util.ServiceUtil;
import com.noboseki.tasktimer.service.util.session_service.SessionServiceGetBarChainByDateUtil;
import com.noboseki.tasktimer.service.util.session_service.SessionServiceGetTableByDateUtil;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringJUnitWebConfig
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SessionServiceTest {
    @Spy
    private SessionServiceGetTableByDateUtil getTableByDateUtil;
    @Spy
    private SessionServiceGetBarChainByDateUtil getBarChainByDateUtil;
    @Spy
    private ServiceUtil serviceUtil;
    @Mock
    private TaskService taskService;
    @Mock
    private UserService userService;
    @Mock
    private SessionDao sessionDao;
    @InjectMocks
    private SessionService service;

    private User user;
    private Task task;
    private final UnitTestUtil util = new UnitTestUtil();

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .username("test")
                .email("test@test.com")
                .password("test")
                .enabled(true).build();

        task = Task.builder()
                .id(UUID.randomUUID())
                .name("task Name")
                .user(user)
                .archived(false).build();
    }

    @Nested
    @DisplayName("Create")
    class SessionServiceTestCreate {
        private SessionServiceCreateRequest request;
        private Session session;

        @BeforeEach
        void setUp() {
            request = new SessionServiceCreateRequest(
                    "2020-10-20",
                    "00:20:00",
                    "task name");

            session = Session.builder()
                    .id(UUID.randomUUID())
                    .date(Date.valueOf("2020-10-20"))
                    .task(task)
                    .time(Time.valueOf("00:40:00")).build();
        }

        @Test
        @DisplayName("Correct")
        void correct() {
            //When
            when(sessionDao.save(any(Session.class))).thenReturn(session);
            when(sessionDao.findById(any(UUID.class))).thenReturn(Optional.of(session));

            String response = service.create(user, request);

            //Then
            assertEquals("Session has been created", response);
            verify(sessionDao, times(1)).save(any(Session.class));
            verify(sessionDao, times(1)).findById(any(UUID.class));
        }

        @Test
        @DisplayName("DateTime date exception")
        void DateTimeExceptionDate() {
            //Given
            request.setDate("2020-50-46");

            //Then
            Throwable response = assertThrows(DateTimeException.class, () -> service.create(user, request));
            assertEquals(ExceptionTextConstants.dateTime("Date", request.getDate()), response.getMessage());
        }

        @Test
        @DisplayName("DateTime time exception")
        void DateTimeExceptionTime() {
            //Given
            request.setTime("46:80");

            //Then
            Throwable response = assertThrows(DateTimeException.class, () -> service.create(user, request));
            assertEquals(ExceptionTextConstants.dateTime("Time", request.getTime()), response.getMessage());
        }

        @Test
        @DisplayName("Save exception")
        void saveException() {
            //Then
            Throwable response = assertThrows(SaveException.class, () -> service.create(user, request));
            assertEquals(ExceptionTextConstants.save("Session", request.getTime()), response.getMessage());
        }
    }

    @Nested
    @DisplayName("Get table by date")
    class SessionServiceTestGetTableByDate {

        @Test
        @DisplayName("Correct")
        void correct() {
            //When
            when(sessionDao.findAllByTask_UserAndDateBetween(any(User.class), any(Date.class), any(Date.class)))
                    .thenReturn(util.getDefaultSessionList());

            List<SessionServiceTableByDateResponse> responses = service.getTableByDate(
                    user,
                    "2020-10-20",
                    "2020-10-21");

            //Then
            assertEquals(2, responses.size());
            assertEquals(LocalDate.parse("2020-10-20"), responses.get(0).getDate());
            assertEquals("07:50", responses.get(0).getTime());
            assertEquals(3, responses.get(0).getSessions());

            assertEquals(LocalDate.parse("2020-10-21"), responses.get(1).getDate());
            assertEquals("00:00", responses.get(1).getTime());
            assertEquals(0, responses.get(1).getSessions());

            verify(sessionDao, times(1))
                    .findAllByTask_UserAndDateBetween(any(User.class), any(Date.class), any(Date.class));
        }

        @Test
        @DisplayName("Correct empty")
        void correctEmpty() {
            //When
            List<SessionServiceTableByDateResponse> responses = service.getTableByDate(
                    user,
                    "2020-10-20",
                    "2020-10-21");

            //Then
            assertEquals(2, responses.size());
        }
    }

    @Nested
    @DisplayName("Get bar chain by date")
    class SessionServiceTestGetBarChainByDate {

        @Test
        @DisplayName("Correct")
        void correct() {
            //When
            when(sessionDao.findAllByTask_UserAndDateBetween(any(User.class), any(Date.class), any(Date.class)))
                    .thenReturn(util.getDefaultSessionList());

            SessionServiceChainByDateResponse response = service.getBarChainByDate(
                    user,
                    "2020-10-20",
                    "2020-10-21");

            //Then
            assertEquals(3, response.getDataList().size());
            assertEquals(2, response.getDateLabel().size());
            verify(sessionDao, times(1))
                    .findAllByTask_UserAndDateBetween(any(User.class), any(Date.class), any(Date.class));
        }

        @Test
        @DisplayName("Correct empty")
        void correctEmpty() {
            //When
            SessionServiceChainByDateResponse response = service.getBarChainByDate(
                    user,
                    "2020-10-20",
                    "2020-10-20");

            //Then
            assertEquals(0, response.getDataList().size());
            assertEquals(1, response.getDateLabel().size());
        }
    }

    @Nested
    @DisplayName("Get all by task")
    class SessionServiceTestGetAllByTask {

        @Test
        @DisplayName("Correct empty")
        void correctEmpty() {
            //When
            List<GetByTaskSessionResponse> responses = service.getAllByTask(user, "taskname");

            //Then
            assertEquals(0, responses.size());
        }

        @Test
        @DisplayName("Correct")
        void correct() {
            //When
            when(taskService.findByNameAndUser(any(User.class), anyString())).thenReturn(task);
            when(sessionDao.findAllByTask(any(Task.class))).thenReturn(util.getDefaultSessionList());

            List<GetByTaskSessionResponse> responses = service.getAllByTask(user, "taskname");

            //Then
            assertEquals(5, responses.size());
        }
    }
}
