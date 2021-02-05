package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.DeleteException;
import com.noboseki.tasktimer.exeption.ExceptionTextConstants;
import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.repository.TaskDao;
import com.noboseki.tasktimer.security.UserDetailsImpl;
import com.noboseki.tasktimer.service.util.task_service.TaskServiceUtil;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringJUnitWebConfig
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskServiceTest {
    @Mock
    private TaskServiceUtil taskServiceUtil;
    @Mock
    private UserService userService;
    @Mock
    private TaskDao taskDao;
    @Spy
    @InjectMocks
    private TaskService service;

    private UserDetailsImpl userDetails;
    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        //Given
        userDetails = UserDetailsImpl.builder()
                .username("test@test.com")
                .build();

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
    class TaskServiceTestCreate {

        @Test
        @DisplayName("Correct")
        void correct() {
            //When
            when(userService.findByEmile(anyString())).thenReturn(user);
            when(taskDao.save(any(Task.class))).thenReturn(task);
            when(taskDao.findByNameAndUser(anyString(), any(User.class))).thenReturn(Optional.of(task));

            String response = service.create(userDetails, "testName");

            //Then
            assertEquals("testName has been created", response);
            verify(userService, times(1)).findByEmile(anyString());
            verify(taskDao, times(1)).save(any(Task.class));
            verify(taskDao, times(1)).findByNameAndUser(anyString(), any(User.class));
        }

        @Test
        @DisplayName("Save error")
        void saveError() {
            //When
            when(userService.findByEmile(anyString())).thenReturn(user);

            //Then
            Throwable response = assertThrows(SaveException.class, () -> service.create(userDetails, "task Name"));
            assertEquals(ExceptionTextConstants.save(task.getClass().getSimpleName(), "task Name"), response.getMessage());
            verify(userService, times(1)).findByEmile(anyString());
        }
    }


    @Nested
    @DisplayName("Change task complete")
    class TaskServiceTestChangeTaskComplete {

        @Test
        @DisplayName("Correct")
        void correct() {
            //When
            when(userService.findByEmile(anyString())).thenReturn(user);
            when(taskDao.save(any(Task.class))).thenReturn(task);
            when(taskDao.findByNameAndUser(anyString(), any(User.class))).thenReturn(Optional.of(task));

            String response = service.changeTaskComplete(userDetails, "taskName");

            //Then
            assertEquals("taskName complete changed to true", response);
            verify(taskDao, times(1)).save(any(Task.class));
            verify(taskDao, times(2)).findByNameAndUser(anyString(), any(User.class));
        }
    }

    @Nested
    @DisplayName("Change archive task")
    class TaskServiceTestChangeChangeArchiveTask {

        @Test
        @DisplayName("Correct")
        void correct() {
            //When
            when(userService.findByEmile(anyString())).thenReturn(user);
            when(taskDao.save(any(Task.class))).thenReturn(task);
            when(taskDao.findByNameAndUser(anyString(), any(User.class))).thenReturn(Optional.of(task));

            String response = service.changeArchiveTask(userDetails, "taskTest");

            //Then
            assertEquals("taskTest archive changed to true", response);
            verify(taskDao, times(1)).save(any(Task.class));
            verify(taskDao, times(2)).findByNameAndUser(anyString(), any(User.class));
        }
    }

    @Nested
    @DisplayName("Delete")
    class TaskServiceTestDelete {

        @Test
        @DisplayName("Correct")
        void correct() {
            //When
            when(userService.findByEmile(anyString())).thenReturn(user);
            when(taskDao.findByNameAndUser(anyString(), any(User.class))).thenReturn(Optional.of(task));

            String response = service.delete(userDetails, "taskName");

            //Then
            assertEquals("taskName has been deleted", response);
            verify(taskDao, times(1)).findByNameAndUser(anyString(), any(User.class));
        }

        @Test
        @DisplayName("Delete error")
        void deleteError() {
            //When
            when(userService.findByEmile(anyString())).thenReturn(user);
            when(taskDao.findByNameAndUser(anyString(), any(User.class))).thenReturn(Optional.of(task));
            when(taskDao.findById(any(UUID.class))).thenReturn(Optional.of(task));

            Throwable response = assertThrows(DeleteException.class, () -> service.delete(userDetails, "task Name"));

            //Then
            assertEquals(ExceptionTextConstants.delete("Task", "task Name"), response.getMessage());
            verify(taskDao, times(1)).findByNameAndUser(anyString(), any(User.class));
            verify(taskDao, times(1)).findById(any(UUID.class));
        }
    }

    @Nested
    @DisplayName("Find by name and user")
    class TaskServiceTestFindByNameAndUser {

        @Test
        @DisplayName("Correct")
        void correct() {
            //When
            when(taskDao.findByNameAndUser(anyString(), any(User.class))).thenReturn(Optional.of(task));

            Task response = service.findByNameAndUser(user, "testName");

            //Then
            assertEquals(response, task);
            verify(taskDao, times(1)).findByNameAndUser(anyString(), any(User.class));
        }

        @Test
        @DisplayName("Not found error")
        void notFoundError() {
            //When
            Throwable response = assertThrows(ResourceNotFoundException.class,
                    () -> service.findByNameAndUser(user, "testName"));
            assertEquals(ExceptionTextConstants.resourceNotFound("Task", "testName"), response.getMessage());
        }
    }
}