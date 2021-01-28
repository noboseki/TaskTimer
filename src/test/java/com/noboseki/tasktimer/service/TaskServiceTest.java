package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.DeleteException;
import com.noboseki.tasktimer.exeption.ExceptionTextConstants;
import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.playload.TaskServiceGetTaskList;
import com.noboseki.tasktimer.repository.TaskDao;
import com.noboseki.tasktimer.service.util.TaskService.TaskServiceUtil;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.util.ArrayList;
import java.util.List;
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

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        //Given
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

            String response = service.create(user, "testName");

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
            Throwable response = assertThrows(SaveException.class, () -> service.create(user, "task Name"));
            assertEquals(ExceptionTextConstants.save(task.getClass().getSimpleName(), "task Name"), response.getMessage());
            verify(userService, times(1)).findByEmile(anyString());
        }
    }

    @Nested
    @DisplayName("Get tasks")
    class TaskServiceTestGetTask {

        @Test
        @DisplayName("Correct")
        void correct() {
            List<Task> tasks = new ArrayList<>();
            tasks.add(Task.builder()
                    .name("testName 1")
                    .archived(true).build());
            tasks.add(Task.builder()
                    .name("testName 2")
                    .archived(false).build());
            tasks.add(Task.builder()
                    .name("testName 3")
                    .archived(false).build());

            //When
            when(taskDao.findAllByUser(any(User.class))).thenReturn(tasks);

            List<TaskServiceGetTaskList> response = service.getTasks(user);

            //Then
            assertEquals(2, response.size());
            verify(taskDao, times(1)).findAllByUser(any(User.class));
        }
    }

    @Nested
    @DisplayName("Change task complete")
    class TaskServiceTestChangeTaskComplete {

        @Test
        @DisplayName("Correct")
        void correct() {
            //When
            when(taskDao.save(any(Task.class))).thenReturn(task);
            when(taskDao.findByNameAndUser(anyString(), any(User.class))).thenReturn(Optional.of(task));

            String response = service.changeTaskComplete(user, "taskName");

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
            when(taskDao.save(any(Task.class))).thenReturn(task);
            when(taskDao.findByNameAndUser(anyString(), any(User.class))).thenReturn(Optional.of(task));

            String response = service.changeArchiveTask(user, "taskTest");

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
            when(taskDao.findByNameAndUser(anyString(), any(User.class))).thenReturn(Optional.of(task));

            String response = service.delete(user, "taskName");

            //Then
            assertEquals("taskName has been deleted", response);
            verify(taskDao, times(1)).findByNameAndUser(anyString(), any(User.class));
        }

        @Test
        @DisplayName("Delete error")
        void deleteError() {
            //When
            when(taskDao.findByNameAndUser(anyString(), any(User.class))).thenReturn(Optional.of(task));
            when(taskDao.findById(any(UUID.class))).thenReturn(Optional.of(task));

            Throwable response = assertThrows(DeleteException.class, () -> service.delete(user, "task Name"));

            //Then
            assertEquals(ExceptionTextConstants.delete("name", "task Name"), response.getMessage());
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
            assertEquals(ExceptionTextConstants.resourceNotFound("Task",  "testName"), response.getMessage());
        }
    }
}