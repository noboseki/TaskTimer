package com.noboseki.tasktimer.controller.juint;

import com.google.gson.Gson;
import com.noboseki.tasktimer.controller.WorkTimeController;
import com.noboseki.tasktimer.domain.WorkTime;
import com.noboseki.tasktimer.exeption.DeleteException;
import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.playload.ApiResponse;
import com.noboseki.tasktimer.service.WorkTimeService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WorkTimeControllerTest {
    @Mock
    WorkTimeService service;
    @InjectMocks
    WorkTimeController controller;

    MockMvc mockMvc;
    Gson gson = new Gson();
    ResponseEntity<ApiResponse> response = ResponseEntity.ok(new ApiResponse(true,"Test Ok"));
    WorkTime.WorkTimeDto dto;
    ControllerMvcMethod method;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        method = new ControllerMvcMethod(mockMvc);

        dto = WorkTime.WorkTimeDto.builder()
                .privateID(UUID.randomUUID())
                .date(Date.valueOf(LocalDate.now()))
                .time(Time.valueOf(LocalTime.now())).build();
    }

    @Test
    @Order(1)
    @DisplayName("Create correct")
    void createCorrect() throws Exception {
        //When
        String jsonTaskDto = gson.toJson(dto);
        when(service.create(any(WorkTime.WorkTimeDto.class))).thenReturn(response);

        //Then
        method.createCorrect("/workTime/create/", jsonTaskDto);
    }

    @Test
    @Order(2)
    @DisplayName("Create valid")
    void createValid() throws Exception {
        //When
        String jsonTaskDto = gson.toJson(dto);
        when(service.create(any(WorkTime.WorkTimeDto.class))).thenThrow(SaveException.class);

        //Then
        method.createValid("/workTime/create", jsonTaskDto);
    }

    @Test
    @Order(3)
    @DisplayName("Get correct")
    void getCorrect() throws Exception {
        //When
        String uuid = UUID.randomUUID().toString();
        when(service.get(any(UUID.class))).thenReturn(ResponseEntity.ok(dto));

        //Then
        mockMvc.perform(get("/workTime/get/" + uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.privateID",is(dto.getPrivateID().toString())))
                .andExpect(jsonPath("$.date",is(dto.getDate().getTime())))
                .andExpect(jsonPath("$.time",is(dto.getTime().toString())));
    }

    @Test
    @Order(4)
    @DisplayName("Get correct")
    void getValid() throws Exception {
        //When
        String uuid = UUID.randomUUID().toString();
        when(service.get(any(UUID.class))).thenThrow(ResourceNotFoundException.class);

        //Then
        method.getValid("/workTime/get/" + uuid);
    }

    @Test
    @Order(5)
    @DisplayName("Update correct")
    void updateCorrect() throws Exception {
        //When
        String jsonTaskDto = gson.toJson(dto);
        when(service.update(any(WorkTime.WorkTimeDto.class))).thenReturn(response);

        //Then
        method.updateCorrect("/workTime/update", jsonTaskDto);
    }

    @Test
    @Order(6)
    @DisplayName("Update valid findByID")
    void updateValidFindById() throws Exception {
        //When
        String jsonTaskDto = gson.toJson(dto);
        when(service.update(any(WorkTime.WorkTimeDto.class))).thenThrow(ResourceNotFoundException.class);

        //Then
        method.updateValid("/workTime/update", jsonTaskDto);
    }

    @Test
    @Order(7)
    @DisplayName("Update valid save")
    void updateValidSave() throws Exception {
        //When
        String jsonTaskDto = gson.toJson(dto);
        when(service.update(any(WorkTime.WorkTimeDto.class))).thenThrow(SaveException.class);

        //Then
        method.updateValid("/workTime/update", jsonTaskDto);
    }

    @Test
    @Order(8)
    @DisplayName("Delete correct")
    void deleteCorrect() throws Exception {
        //When
        String uuid = UUID.randomUUID().toString();
        when(service.delete(any(UUID.class))).thenReturn(response);

        //Then
        method.deleteCorrect("/workTime/delete/" + uuid);
    }

    @Test
    @Order(9)
    @DisplayName("Delete valid findById")
    void deleteValidFindByID() throws Exception {
        //When
        String uuid = UUID.randomUUID().toString();
        when(service.delete(any(UUID.class))).thenThrow(ResourceNotFoundException.class);

        //Then
        method.deleteValid("/workTime/delete/" + uuid);
    }

    @Test
    @Order(10)
    @DisplayName("Delete valid delete")
    void deleteValidDelete() throws Exception {
        //When
        String uuid = UUID.randomUUID().toString();
        when(service.delete(any(UUID.class))).thenThrow(DeleteException.class);

        //Then
        method.deleteValid("/workTime/delete/" + uuid);
    }
}