package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.playload.ApiResponse;
import com.noboseki.tasktimer.playload.UserCreateRequest;
import com.noboseki.tasktimer.playload.UserGetResponse;
import com.noboseki.tasktimer.repository.UserDao;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringJUnitWebConfig
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class UserServiceTest {
    private final String TEST_NAME = "TestName";
    private final String TEST_PASSWORD = "TestPassword";
    private final String TEST_EMAIL = "test@email.com";
    private final String TEST_IMAGE = "test.url.com";

    @Mock
    private UserDao dao;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserService service;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username(TEST_NAME)
                .publicId(10001L)
                .password(TEST_PASSWORD)
                .email(TEST_EMAIL)
                .imageUrl(TEST_IMAGE).build();
    }

    @Nested
    @DisplayName("Create")
    class UserServiceTestCreate {
        private UserCreateRequest request;

        @BeforeEach
        void setUp() {
            request = UserCreateRequest.builder()
                    .userName(TEST_NAME)
                    .password(TEST_PASSWORD)
                    .email(TEST_EMAIL)
                    .imageUrl(TEST_IMAGE).build();
        }

        @AfterEach
        void tearDown() {
            verify(dao, times(1)).save(any(User.class));
            verify(encoder, times(1)).encode(anyString());
        }

        @Test
        @DisplayName("Correct")
        void createCorrect() {
            //When
            ResponseEntity<ApiResponse> response = service.create(request);

            //Then
            checkApiResponse(response.getBody(), "User has been created");
        }

        @Test
        @DisplayName("Save error")
        void createSaveError() {
            //When
            when(dao.save(any(User.class))).thenThrow(SaveException.class);

            //Then
            assertThrows(SaveException.class, () -> service.create(request));
        }
    }

    @Nested
    @DisplayName("Get")
    class UserServiceTestGet {

        @AfterEach
        void tearDown() {
            verify(dao,times(1)).findByEmailAndPassword(anyString(),anyString());
        }

        @Test
        @DisplayName("Correct")
        void getCorrect() {
            //When
            when(dao.findByEmailAndPassword(anyString(),anyString())).thenReturn(Optional.of(user));
            ResponseEntity<UserGetResponse> response = service.get(TEST_EMAIL, TEST_PASSWORD);

            //Then
            checkUserGetResponse(response.getBody(), user);
        }

        @Test
        @DisplayName("Resource not found")
        void getResourceNotFound() {
            //When
            when(dao.findByEmailAndPassword(anyString(),anyString())).thenThrow(ResourceNotFoundException.class);

            //Then
            assertThrows(ResourceNotFoundException.class, () -> service.get(TEST_EMAIL, TEST_PASSWORD));
        }
    }

    @Nested
    @DisplayName("Get by email")
    class UserServiceTestGetByEmail {

        @AfterEach
        void tearDown() {
            verify(dao, times(1)).findByEmail(anyString());
        }

        @Test
        @DisplayName("Correct")
        void getByEmailCorrect() {
            //When
            when(dao.findByEmail(anyString())).thenReturn(Optional.of(user));
            ResponseEntity<UserGetResponse> response = service.getByEmail(TEST_EMAIL);

            //Then
            checkUserGetResponse(response.getBody(), user);
        }

        @Test
        @DisplayName("Resource not found")
        void getByEmailResourceNotFound() {
            //When
            when(dao.findByEmail(anyString())).thenThrow(ResourceNotFoundException.class);

            //Then
            assertThrows(ResourceNotFoundException.class, () -> service.getByEmail(TEST_EMAIL));
        }
    }

    @Nested
    @DisplayName("Update image")
    class UserServiceTestUpdateImage {

        @BeforeEach
        void setUp() {
            when(dao.findByEmailAndPassword(anyString(),anyString())).thenReturn(Optional.of(user));
        }

        @AfterEach
        void tearDown() {
            verify(dao, times(1)).findByEmailAndPassword(anyString(),anyString());
        }

        @Test
        @DisplayName("Correct")
        void updateImageCorrect() {
            //When
            ResponseEntity<ApiResponse> response = service.updateImageUrl(TEST_IMAGE, user);

            //Then
            verify(dao, times(1)).save(any(User.class));
            checkApiResponse(response.getBody(),"Image has been changed");
        }

        @Test
        @DisplayName("Resource not found")
        void updateImageResourceNotFound() {
            //When
            when(dao.findByEmailAndPassword(anyString(),anyString())).thenThrow(ResourceNotFoundException.class);

            //Then
            assertThrows(ResourceNotFoundException.class, () -> service.updateImageUrl(TEST_IMAGE, user));
        }

        @Test
        @DisplayName("Save error")
        void updateImageSaveError() {
            //When
            when(dao.save(any(User.class))).thenThrow(SaveException.class);

            //Then
            assertThrows(SaveException.class, () -> service.updateImageUrl(TEST_EMAIL, user));
            verify(dao, times(1)).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Update username")
    class UserServiceTestUpdateUsername {

        @BeforeEach
        void setUp() {
            when(dao.findByEmailAndPassword(anyString(),anyString())).thenReturn(Optional.of(user));
        }

        @AfterEach
        void tearDown() {
            verify(dao, times(1)).findByEmailAndPassword(anyString(),anyString());
        }

        @Test
        @DisplayName("Correct")
        void updateNameCorrect() {
            //When
            ResponseEntity<ApiResponse> response = service.updateName(TEST_NAME, user);

            //Then
            verify(dao, times(1)).save(any(User.class));
            checkApiResponse(response.getBody(),"Username has been changed");
        }

        @Test
        @DisplayName("Resource not found")
        void updateNameResourceNotFound() {
            //When
            when(dao.findByEmailAndPassword(anyString(),anyString())).thenThrow(ResourceNotFoundException.class);

            //Then
            assertThrows(ResourceNotFoundException.class, () -> service.updateImageUrl(TEST_NAME, user));
            verify(dao,times(1)).findByEmailAndPassword(anyString(), anyString());
        }

        @Test
        @DisplayName("Save error")
        void updateNameSaveError() {
            //When
            when(dao.save(any(User.class))).thenThrow(SaveException.class);

            //Then
            assertThrows(SaveException.class, () -> service.updateName(TEST_NAME, user));
            verify(dao, times(1)).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Delete")
    class UserServiceTestDelete {

        @AfterEach
        void tearDown() {
            verify(dao, times(1)).findByEmail(anyString());
        }

        @Test
        @DisplayName("Correct")
        void deleteCorrect() {
            //When
            when(dao.findByEmail(anyString())).thenReturn(Optional.of(user));
            ResponseEntity<ApiResponse> response = service.delete(TEST_EMAIL);

            //Then
            verify(dao, times(1)).delete(any(User.class));
            checkApiResponse(response.getBody(),"User has been deleted");
        }

        @Test
        @DisplayName("Resource not found")
        void deleteResourceNotFound() {
            //When
            when(dao.findByEmail(anyString())).thenThrow(ResourceNotFoundException.class);

            //Then
            assertThrows(ResourceNotFoundException.class, () -> service.delete(TEST_EMAIL));
        }
    }

    private void checkUserGetResponse(UserGetResponse response,User user) {
        assertThat(response.getEmail()).isEqualTo(user.getEmail());
        assertThat(response.getPublicId()).isEqualTo(user.getPublicId());
        assertThat(response.getUsername()).isEqualTo(user.getUsername());
        assertThat(response.getImageUrl()).isEqualTo(user.getImageUrl());
    }

    private void checkApiResponse(ApiResponse response, String message) {
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.isSuccess()).isEqualTo(true);
    }
}