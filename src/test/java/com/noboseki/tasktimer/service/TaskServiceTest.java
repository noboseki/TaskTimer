package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.playload.ApiResponse;
import com.noboseki.tasktimer.repository.TaskDao;
import com.noboseki.tasktimer.util.EntityMapper;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringJUnitWebConfig
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskServiceTest {
    @Mock
    TaskDao dao;

    @InjectMocks
    TaskService service;

    Task task;
    Task.TaskDto dto;
    ResponseEntity<ApiResponse> response;

    @BeforeEach
    void setUp() {
        task = Task.builder()
                .id(UUID.randomUUID())
                .name("Test name")
                .complete(false).build();

        dto = Task.TaskDto.builder()
                .privateID(UUID.randomUUID())
                .name("Test name")
                .complete(false).build();
    }

    @Test
    @Order(1)
    @DisplayName("Create correct")
    void createCorrect() {
        //When
        when(dao.save(any(Task.class))).thenReturn(task);
        response = service.create(dto);

        //Then
        verify(dao, times(1)).save(any(Task.class));
        assertThat(response.getBody().getMessage()).isEqualTo("Task has been created");
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    @Order(2)
    @DisplayName("Create valid save")
    void createValid() {
        //When
        when(dao.save(any(Task.class))).thenThrow(SaveException.class);

        //Then
        assertThrows(SaveException.class, () ->{
            service.create(dto);
        });
    }

    @Test
    @Order(3)
    @DisplayName("Get correct")
    void getCorrect() {
        //When
        when(dao.findById(any(UUID.class))).thenReturn(Optional.of(task));
        ResponseEntity<Task.TaskDto> response = service.get(UUID.randomUUID());

        //Then
        verify(dao, times(1)).findById(any(UUID.class));
        assertThat(response.getBody()).isEqualTo(EntityMapper.mapToDto(task));
    }

    @Test
    @Order(4)
    @DisplayName("Get valid findById")
    void getValidFindById() {
        //When
        when(dao.findById(any(UUID.class))).thenReturn(Optional.empty());

        //Then
        assertThrows(ResourceNotFoundException.class, () ->{
            service.get(UUID.randomUUID());
        });
    }

    @Test
    @Order(5)
    @DisplayName("Update correct")
    void updateCorrect() {
        //When
        when(dao.findById(any(UUID.class))).thenReturn(Optional.of(task));
        when(dao.save(any(Task.class))).thenReturn(task);
        response = service.update(dto);

        //Then
        verify(dao,times(1)).findById(any(UUID.class));
        verify(dao, times(1)).save(any(Task.class));
        assertThat(response.getBody().getMessage()).isEqualTo("Task has been updated");
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    @Order(6)
    @DisplayName("Update valid findBy")
    void UpdateValidFindBy() {
        //When
        when(dao.findById(any(UUID.class))).thenThrow(ResourceNotFoundException.class);

        //Then
        assertThrows(ResourceNotFoundException.class, () ->{
            service.get(UUID.randomUUID());
        });
    }

    @Test
    @Order(6)
    @DisplayName("Update valid save")
    void updateValidSave() {
        //When
        when(dao.findById(any(UUID.class))).thenReturn(Optional.of(task));
        when(dao.save(any(Task.class))).thenThrow(SaveException.class);

        //Then
        assertThrows(SaveException.class, () ->{
            service.update(dto);
        });
    }

    @Test
    @Order(7)
    @DisplayName("Delete correct")
    void deleteCorrect() {
        //When
        when(dao.findById(any(UUID.class))).thenReturn(Optional.of(task));
        response = service.delete(UUID.randomUUID());

        //Then
        verify(dao, times(1)).findById(any(UUID.class));
        verify(dao, times(1)).deleteById(any());
        assertThat(response.getBody().getMessage()).isEqualTo("Task has been deleted");
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    @Order(8)
    @DisplayName("Delete valid findById")
    void deleteValidFindById() {
        //When
        when(dao.findById(any(UUID.class))).thenThrow(ResourceNotFoundException.class);

        //Then
        assertThrows(ResourceNotFoundException.class, () ->{
            service.get(UUID.randomUUID());
        });
    }
}