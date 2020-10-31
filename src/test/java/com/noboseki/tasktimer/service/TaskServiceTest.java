package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.playload.ApiResponse;
import com.noboseki.tasktimer.playload.TaskGetResponse;
import com.noboseki.tasktimer.repository.TaskDao;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringJUnitWebConfig
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskServiceTest extends ServiceSetupClass{
    @InjectMocks
    private TaskService service;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        task = Task.builder()
                .id(UUID.randomUUID())
                .name("Test name")
                .complete(false).build();

        when(userDao.findByEmail(anyString())).thenReturn(Optional.of(user));
    }

    @AfterEach
    void tearDown() {
        verify(userDao, times(1)).findByEmail(anyString());
    }

    @Nested
    @DisplayName("Create")
    class TaskServiceTestCreate {

        @Test
        @DisplayName("Correct")
        void createCorrect() {
            //When
            ResponseEntity<ApiResponse> response = service.create(user,TEST_NAME);

            //Then
            verify(taskDao, times(1)).findByNameAndUser(anyString(),any(User.class));
            verify(taskDao, times(1)).save(any(Task.class));
            checkApiResponse(response.getBody(),"Task has been created", true);
        }

        @Test
        @DisplayName("Duplicate name")
        void createDuplicateName() {
            //When
            when(taskDao.findByNameAndUser(anyString(),any(User.class))).thenReturn(Optional.of(task));
            ResponseEntity<ApiResponse> response = service.create(user,TEST_NAME);

            //Then
            verify(taskDao, times(1)).findByNameAndUser(anyString(),any(User.class));
            verify(taskDao, times(0)).save(any(Task.class));
            checkApiResponse(response.getBody(),"Duplicate task name", false);
        }

        @Test
        @DisplayName("User not found")
        void createUserNotFound() {
            testUserNotFound(() -> service.create(user,TEST_NAME));
        }

        @Test
        @DisplayName("Save error")
        void createSaveError() {
            //When
            when(taskDao.save(any(Task.class))).thenThrow(SaveException.class);

            //Then
            assertThrows(SaveException.class, () -> service.create(user,TEST_NAME));
        }
    }

    @Nested
    @DisplayName("Get")
    class TaskServiceTestGet {

        @Test
        @DisplayName("Correct")
        void getCorrect() {
            //when
            when(taskDao.findByNameAndUser(anyString(),any(User.class))).thenReturn(Optional.of(task));

            ResponseEntity<TaskGetResponse> response = service.get(user,TEST_NAME);

            //Then
            verify(taskDao, times(1)).findByNameAndUser(anyString(),any(User.class));
            assertThat(response.getBody().getName()).isEqualTo(task.getName());
            assertThat(response.getBody().isComplete()).isEqualTo(task.getComplete());
        }

        @Test
        @DisplayName("User not found")
        void getUserNotFound() {
            testUserNotFound(() -> service.get(user,TEST_NAME));
        }

        @Test
        @DisplayName("Task not found")
        void getTaskNotFound() {
            testTaskNotFound(() -> service.get(user,TEST_NAME));
        }
    }

    @Nested
    @DisplayName("Get All")
    class TaskServiceTestGetAll {

        @Test
        @DisplayName("Correct")
        void getAllCorrect() {
            //Given
            List<Task> tasks = new ArrayList<>();
            tasks.add(task);

            //When
            when(taskDao.findAllByUser(any(User.class))).thenReturn(tasks);

            ResponseEntity<List<TaskGetResponse>> response = service.getAll(user);

            //Then
            verify(taskDao, times(1)).findAllByUser(any(User.class));
            assertThat(response.getBody().size()).isEqualTo(1);
        }

        @Test
        @DisplayName("Correct empty")
        void getAllCorrectEmpty() {
            //Given
            List<Task> tasks = new ArrayList<>();

            //When
            when(taskDao.findAllByUser(any(User.class))).thenReturn(tasks);

            ResponseEntity<List<TaskGetResponse>> response = service.getAll(user);

            //Then
            verify(taskDao, times(1)).findAllByUser(any(User.class));
            assertThat(response.getBody().size()).isEqualTo(0);
        }

        @Test
        @DisplayName("User not found")
        void getAllUserNotFound() {
            testUserNotFound(() -> service.getAll(user));
        }
    }

    @Nested
    @DisplayName("Update Name")
    class TaskServiceTestUpdateName {
        private final String NEW_NAME = "Test new name";

        @BeforeEach
        void setUp() {
            when(taskDao.findByNameAndUser(eq(NEW_NAME),any(User.class))).thenReturn(Optional.empty());
        }

        @Test
        @DisplayName("Correct")
        void updateNameCorrect() {
            //When
            when(taskDao.findByNameAndUser(eq(TEST_NAME),any(User.class))).thenReturn(Optional.of(task));
            ResponseEntity<ApiResponse> response = service.updateName(user, TEST_NAME, NEW_NAME);

            //Then
            verify(taskDao, times(2)).findByNameAndUser(anyString(),any(User.class));
            verify(taskDao, times(1)).save(any(Task.class));
            checkApiResponse(response.getBody(),"Task name has been updated", true);
        }

        @Test
        @DisplayName("Duplicate name")
        void updateNameDuplicateName() {
            //When
            when(taskDao.findByNameAndUser(eq(NEW_NAME),any(User.class))).thenReturn(Optional.of(task));
            ResponseEntity<ApiResponse> response = service.updateName(user, TEST_NAME, NEW_NAME);

            //Then
            verify(taskDao, times(1)).findByNameAndUser(anyString(),any(User.class));
            verify(taskDao, times(0)).save(any(Task.class));
            checkApiResponse(response.getBody(),"Duplicate task name", false);
        }

        @Test
        @DisplayName("User not found")
        void updateNameUserNotFound() {
            testUserNotFound(() ->  service.updateName(user, TEST_NAME, NEW_NAME));
        }

        @Test
        @DisplayName("Task not found")
        void UpdateNameTaskNotFound() {
            testTaskNotFound(() -> service.updateName(user, TEST_NAME, NEW_NAME));
            verify(taskDao, times(2)).findByNameAndUser(anyString(),any(User.class));
        }

        @Test
        @DisplayName("Save error")
        void UpdateNameSaveError() {
            //When
            when(taskDao.findByNameAndUser(eq(TEST_NAME),any(User.class))).thenReturn(Optional.of(task));
            when(taskDao.save(any(Task.class))).thenThrow(SaveException.class);

            //Then
            assertThrows(SaveException.class, () -> service.updateName(user, TEST_NAME, NEW_NAME));
            verify(taskDao, times(2)).findByNameAndUser(anyString(),any(User.class));
        }
    }

    @Nested
    @DisplayName("Update status")
    class TaskServiceTestUpdateStatus {

        @Test
        @DisplayName("Correct")
        void updateStatusCorrect() {
            //When
            when(taskDao.findByNameAndUser(anyString(),any(User.class))).thenReturn(Optional.of(task));
            ResponseEntity<ApiResponse> response = service.updateIsComplete(user, TEST_NAME);

            //Then
            verify(taskDao, times(1)).findByNameAndUser(anyString(),any(User.class));
            verify(taskDao, times(1)).save(any(Task.class));
            checkApiResponse(response.getBody(),"Task status has been updated", true);
        }

        @Test
        @DisplayName("User not found")
        void updateStatusUserNotFound() {
            testUserNotFound(() ->  service.updateIsComplete(user, TEST_NAME));
        }

        @Test
        @DisplayName("Task not found")
        void updateStatusTaskNotFound() {
            testTaskNotFound(() -> service.updateIsComplete(user, TEST_NAME));
            verify(taskDao, times(1)).findByNameAndUser(anyString(),any(User.class));
        }

        @Test
        @DisplayName("Save error")
        void updateStatusSaveError() {
            //When
            when(taskDao.findByNameAndUser(eq(TEST_NAME),any(User.class))).thenReturn(Optional.of(task));
            when(taskDao.save(any(Task.class))).thenThrow(SaveException.class);

            //Then
            assertThrows(SaveException.class, () -> service.updateIsComplete(user, TEST_NAME));
            verify(taskDao, times(1)).findByNameAndUser(anyString(),any(User.class));
        }
    }

    @Nested
    @DisplayName("Delete")
    class TaskServiceTestDelete {

        @Test
        @DisplayName("Correct")
        void updateStatusCorrect() {
            //When
            when(taskDao.findByNameAndUser(anyString(),any(User.class))).thenReturn(Optional.of(task));
            ResponseEntity<ApiResponse> response = service.delete(user, TEST_NAME);

            //Then
            verify(taskDao, times(1)).findByNameAndUser(anyString(),any(User.class));
            checkApiResponse(response.getBody(),"Task has been deleted", true);
        }

        @Test
        @DisplayName("User not found")
        void DeleteStatusUserNotFound() {
            testUserNotFound(() -> service.delete(user, TEST_NAME));
        }

        @Test
        @DisplayName("Task not found")
        void updateStatusTaskNotFound() {
            testTaskNotFound(() -> service.delete(user, TEST_NAME));
            verify(taskDao, times(1)).findByNameAndUser(anyString(),any(User.class));
        }
    }
}