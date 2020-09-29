package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.playload.ApiResponse;
import com.noboseki.tasktimer.repository.UserDao;
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
class UserServiceTest {
    @Mock
    UserDao dao;

    @InjectMocks
    UserService service;

    User user;
    User.UserDto dto;
    ResponseEntity<ApiResponse> response;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .privateID(UUID.randomUUID())
                .publicId(1L)
                .email("test@email.com")
                .imageUrl("test URL")
                .emailVerified(true)
                .password("Password").build();

        dto = User.UserDto.builder()
                .privateID(UUID.randomUUID())
                .publicId(1L)
                .email("test@email.com")
                .imageUrl("test URL")
                .emailVerified(true)
                .password("Password").build();
    }

    @Test
    @Order(1)
    @DisplayName("Create correct")
    void createCorrect() {
        //When
        when(dao.save(any(User.class))).thenReturn(user);
        response = service.create(dto);

        //Then
        verify(dao, times(1)).save(any(User.class));
        assertThat(response.getBody().getMessage()).isEqualTo("User has been created");
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    @Order(2)
    @DisplayName("Create valid save")
    void createValid() {
        //When
        when(dao.save(any(User.class))).thenThrow(SaveException.class);

        //Then
        assertThrows(SaveException.class, () -> {
            service.create(dto);
        });
    }

    @Test
    @Order(3)
    @DisplayName("Get correct")
    void getCorrect() {
        //When
        when(dao.findById(any(UUID.class))).thenReturn(Optional.of(user));
        ResponseEntity<User.UserDto> response = service.get(UUID.randomUUID());

        //Then
        verify(dao, times(1)).findById(any(UUID.class));
        assertThat(response.getBody()).isEqualTo(EntityMapper.mapToDto(user));
    }

    @Test
    @Order(4)
    @DisplayName("Get valid findById")
    void getValidFindById() {
        //When
        when(dao.findById(any(UUID.class))).thenReturn(Optional.empty());

        //Then
        assertThrows(ResourceNotFoundException.class, () -> {
            service.get(UUID.randomUUID());
        });
    }

    @Test
    @Order(5)
    @DisplayName("Update correct")
    void updateCorrect() {
        //When
        when(dao.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(dao.save(any(User.class))).thenReturn(user);
        response = service.update(dto);

        //Then
        verify(dao, times(1)).findById(any(UUID.class));
        verify(dao, times(1)).save(any(User.class));
        assertThat(response.getBody().getMessage()).isEqualTo("User has been updated");
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    @Order(6)
    @DisplayName("Update valid findBy")
    void UpdateValidFindBy() {
        //When
        when(dao.findById(any(UUID.class))).thenThrow(ResourceNotFoundException.class);

        //Then
        assertThrows(ResourceNotFoundException.class, () -> {
            service.get(UUID.randomUUID());
        });
    }

    @Test
    @Order(6)
    @DisplayName("Update valid save")
    void updateValidSave() {
        //When
        when(dao.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(dao.save(any(User.class))).thenThrow(SaveException.class);

        //Then
        assertThrows(SaveException.class, () -> {
            service.update(dto);
        });
    }

    @Test
    @Order(7)
    @DisplayName("Delete correct")
    void deleteCorrect() {
        //When
        when(dao.findById(any(UUID.class))).thenReturn(Optional.of(user));
        response = service.delete(UUID.randomUUID());

        //Then
        verify(dao, times(1)).findById(any(UUID.class));
        verify(dao, times(1)).deleteById(any());
        assertThat(response.getBody().getMessage()).isEqualTo("User has been deleted");
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    @Order(8)
    @DisplayName("Delete valid findById")
    void deleteValidFindById() {
        //When
        when(dao.findById(any(UUID.class))).thenThrow(ResourceNotFoundException.class);

        //Then
        assertThrows(ResourceNotFoundException.class, () -> {
            service.get(UUID.randomUUID());
        });
    }
}
