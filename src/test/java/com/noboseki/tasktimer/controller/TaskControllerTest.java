package com.noboseki.tasktimer.controller;

import com.google.gson.Gson;
import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.exeption.DeleteException;
import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.playload.ApiResponse;
import com.noboseki.tasktimer.service.TaskService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskControllerTest {
    @Mock
    TaskService service;
    @InjectMocks
    TaskController controller;

    MockMvc mockMvc;
    Gson gson = new Gson();
    ResponseEntity<ApiResponse> response = ResponseEntity.ok(new ApiResponse(true,"Test Ok"));
    Task.TaskDto dto;
    ControllerMvcMethod method;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        method = new ControllerMvcMethod(mockMvc);

        dto = Task.TaskDto.builder()
                .privateID(UUID.randomUUID())
                .name("Test")
                .complete(true).build();
    }

    @Test
    @Order(1)
    @DisplayName("Create correct")
    void createCorrect() throws Exception {
        //When
        String jsonTaskDto = gson.toJson(dto);
        when(service.create(any(Task.TaskDto.class))).thenReturn(response);

        //Then
        method.createCorrect("/task/create/", jsonTaskDto);
    }

    @Test
    @Order(2)
    @DisplayName("Create valid")
    void createValid() throws Exception {
        //When
        String jsonTaskDto = gson.toJson(dto);
        when(service.create(any(Task.TaskDto.class))).thenThrow(SaveException.class);

        //Then
        method.createValid("/task/create", jsonTaskDto);
    }

    @Test
    @Order(3)
    @DisplayName("Get correct")
    void getCorrect() throws Exception {
        //When
        String uuid = UUID.randomUUID().toString();
        when(service.get(any(UUID.class))).thenReturn(ResponseEntity.ok(dto));

        //Then
        mockMvc.perform(get("/task/get/" + uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.privateID",is(dto.getPrivateID().toString())))
                .andExpect(jsonPath("$.name",is("Test")))
                .andExpect(jsonPath("$.complete",is(true)));
    }

    @Test
    @Order(4)
    @DisplayName("Get correct")
    void getValid() throws Exception {
        //When
        String uuid = UUID.randomUUID().toString();
        when(service.get(any(UUID.class))).thenThrow(ResourceNotFoundException.class);

        //Then
        method.getValid("/task/get/" + uuid);
    }

    @Test
    @Order(5)
    @DisplayName("Update correct")
    void updateCorrect() throws Exception {
        //When
        String jsonTaskDto = gson.toJson(dto);
        when(service.update(any(Task.TaskDto.class))).thenReturn(response);

        //Then
        method.updateCorrect("/task/update", jsonTaskDto);
    }

    @Test
    @Order(6)
    @DisplayName("Update valid findByID")
    void updateValidFindById() throws Exception {
        //When
        String jsonTaskDto = gson.toJson(dto);
        when(service.update(any(Task.TaskDto.class))).thenThrow(ResourceNotFoundException.class);

        //Then
        method.updateValid("/task/update", jsonTaskDto);
    }

    @Test
    @Order(7)
    @DisplayName("Update valid save")
    void updateValidSave() throws Exception {
        //When
        String jsonTaskDto = gson.toJson(dto);
        when(service.update(any(Task.TaskDto.class))).thenThrow(SaveException.class);

        //Then
        method.updateValid("/task/update", jsonTaskDto);
    }

    @Test
    @Order(8)
    @DisplayName("Delete correct")
    void deleteCorrect() throws Exception {
        //When
        String uuid = UUID.randomUUID().toString();
        when(service.delete(any(UUID.class))).thenReturn(response);

        //Then
        method.deleteCorrect("/task/delete/" + uuid);
    }

    @Test
    @Order(9)
    @DisplayName("Delete valid findById")
    void deleteValidFindByID() throws Exception {
        //When
        String uuid = UUID.randomUUID().toString();
        when(service.delete(any(UUID.class))).thenThrow(ResourceNotFoundException.class);

        //Then
        method.deleteValid("/task/delete/" + uuid);
    }

    @Test
    @Order(10)
    @DisplayName("Delete valid delete")
    void deleteValidDelete() throws Exception {
        //When
        String uuid = UUID.randomUUID().toString();
        when(service.delete(any(UUID.class))).thenThrow(DeleteException.class);

        //Then
        method.deleteValid("/task/delete/" + uuid);
    }
}