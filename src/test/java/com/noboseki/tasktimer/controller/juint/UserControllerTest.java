package com.noboseki.tasktimer.controller.juint;

import com.google.gson.Gson;
import com.noboseki.tasktimer.controller.UserController;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.DeleteException;
import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.playload.ApiResponse;
import com.noboseki.tasktimer.service.UserService;
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
class UserControllerTest {
    @Mock
    UserService service;
    @InjectMocks
    UserController controller;

    MockMvc mockMvc;
    Gson gson = new Gson();
    ResponseEntity<ApiResponse> response = ResponseEntity.ok(new ApiResponse(true,"Test Ok"));
    User.UserDto dto;
    ControllerMvcMethod method;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        method = new ControllerMvcMethod(mockMvc);

        dto = User.UserDto.builder()
                .privateID(UUID.randomUUID())
                .publicId(1L)
                .email("test@test.com")
                .emailVerified(true)
                .imageUrl("test")
                .password("Password").build();
    }

    @Test
    @Order(1)
    @DisplayName("Create correct")
    void createCorrect() throws Exception {
        //When
        String jsonTaskDto = gson.toJson(dto);
        when(service.create(any(User.UserDto.class))).thenReturn(response);

        //Then
        method.createCorrect("/user/create/", jsonTaskDto);
    }

    @Test
    @Order(2)
    @DisplayName("Create valid")
    void createValid() throws Exception {
        //When
        String jsonTaskDto = gson.toJson(dto);
        when(service.create(any(User.UserDto.class))).thenThrow(SaveException.class);

        //Then
        method.createValid("/user/create", jsonTaskDto);
    }

    @Test
    @Order(3)
    @DisplayName("Get correct")
    void getCorrect() throws Exception {
        //When
        String uuid = UUID.randomUUID().toString();
        when(service.get(any(UUID.class))).thenReturn(ResponseEntity.ok(dto));

        //Then
        mockMvc.perform(get("/user/get/" + uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.privateID",is(dto.getPrivateID().toString())))
                .andExpect(jsonPath("$.publicId",is(1)))
                .andExpect(jsonPath("$.email",is("test@test.com")))
                .andExpect(jsonPath("$.imageUrl",is("test")))
                .andExpect(jsonPath("$.password",is("Password")))
                .andExpect(jsonPath("$.emailVerified",is(true)));
    }

    @Test
    @Order(4)
    @DisplayName("Get correct")
    void getValid() throws Exception {
        //When
        String uuid = UUID.randomUUID().toString();
        when(service.get(any(UUID.class))).thenThrow(ResourceNotFoundException.class);

        //Then
        method.getValid("/user/get/" + uuid);
    }

    @Test
    @Order(5)
    @DisplayName("Update correct")
    void updateCorrect() throws Exception {
        //When
        String jsonTaskDto = gson.toJson(dto);
        when(service.update(any(User.UserDto.class))).thenReturn(response);

        //Then
        method.updateCorrect("/user/update", jsonTaskDto);
    }

    @Test
    @Order(6)
    @DisplayName("Update valid findByID")
    void updateValidFindById() throws Exception {
        //When
        String jsonTaskDto = gson.toJson(dto);
        when(service.update(any(User.UserDto.class))).thenThrow(ResourceNotFoundException.class);

        //Then
        method.updateValid("/user/update", jsonTaskDto);
    }

    @Test
    @Order(7)
    @DisplayName("Update valid save")
    void updateValidSave() throws Exception {
        //When
        String jsonTaskDto = gson.toJson(dto);
        when(service.update(any(User.UserDto.class))).thenThrow(SaveException.class);

        //Then
        method.updateValid("/user/update", jsonTaskDto);
    }

    @Test
    @Order(8)
    @DisplayName("Delete correct")
    void deleteCorrect() throws Exception {
        //When
        String uuid = UUID.randomUUID().toString();
        when(service.delete(any(UUID.class))).thenReturn(response);

        //Then
        method.deleteCorrect("/user/delete/" + uuid);
    }

    @Test
    @Order(9)
    @DisplayName("Delete valid findById")
    void deleteValidFindByID() throws Exception {
        //When
        String uuid = UUID.randomUUID().toString();
        when(service.delete(any(UUID.class))).thenThrow(ResourceNotFoundException.class);

        //Then
        method.deleteValid("/user/delete/" + uuid);
    }

    @Test
    @Order(10)
    @DisplayName("Delete valid delete")
    void deleteValidDelete() throws Exception {
        //When
        String uuid = UUID.randomUUID().toString();
        when(service.delete(any(UUID.class))).thenThrow(DeleteException.class);

        //Then
        method.deleteValid("/user/delete/" + uuid);
    }
}