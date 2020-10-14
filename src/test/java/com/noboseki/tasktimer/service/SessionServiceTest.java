package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.Session;
import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.playload.ApiResponse;
import com.noboseki.tasktimer.repository.SessionDao;
import com.noboseki.tasktimer.util.EntityMapper;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringJUnitWebConfig
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SessionServiceTest {
    @Mock
    SessionDao dao;

    @InjectMocks
    SessionService service;

    Session session;
    Session.SessionDto dto;
    ResponseEntity<ApiResponse> response;

    @BeforeEach
    void setUp() {
        session = Session.builder()
                .privateID(UUID.randomUUID())
                .time(Time.valueOf(LocalTime.now()))
                .date(Date.valueOf(LocalDate.now())).build();

        dto = Session.SessionDto.builder()
                .privateID(UUID.randomUUID())
                .time(Time.valueOf(LocalTime.now()))
                .date(Date.valueOf(LocalDate.now())).build();
    }

    @Test
    @Order(1)
    @DisplayName("Create correct")
    void createCorrect() {
        //When
        when(dao.save(any(Session.class))).thenReturn(session);
        response = service.create(dto);

        //Then
        verify(dao, times(1)).save(any(Session.class));
        assertThat(response.getBody().getMessage()).isEqualTo("WorkTime has been created");
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    @Order(2)
    @DisplayName("Create valid save")
    void createValid() {
        //When
        when(dao.save(any(Session.class))).thenThrow(SaveException.class);

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
        when(dao.findById(any(UUID.class))).thenReturn(Optional.of(session));
        ResponseEntity<Session.SessionDto> response = service.get(UUID.randomUUID());

        //Then
        verify(dao, times(1)).findById(any(UUID.class));
        assertThat(response.getBody()).isEqualTo(EntityMapper.mapToDto(session));
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
        when(dao.findById(any(UUID.class))).thenReturn(Optional.of(session));
        when(dao.save(any(Session.class))).thenReturn(session);
        response = service.update(dto);

        //Then
        verify(dao, times(1)).findById(any(UUID.class));
        verify(dao, times(1)).save(any(Session.class));
        assertThat(response.getBody().getMessage()).isEqualTo("WorkTime has been updated");
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
        when(dao.findById(any(UUID.class))).thenReturn(Optional.of(session));
        when(dao.save(any(Session.class))).thenThrow(SaveException.class);

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
        when(dao.findById(any(UUID.class))).thenReturn(Optional.of(session));
        response = service.delete(UUID.randomUUID());

        //Then
        verify(dao, times(1)).findById(any(UUID.class));
        verify(dao, times(1)).deleteById(any());
        assertThat(response.getBody().getMessage()).isEqualTo("WorkTime has been deleted");
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
