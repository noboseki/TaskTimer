package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.*;
import com.noboseki.tasktimer.exeption.*;
import com.noboseki.tasktimer.playload.UserServiceChangePasswordRequest;
import com.noboseki.tasktimer.playload.UserServiceCreateRequest;
import com.noboseki.tasktimer.playload.UserServiceUpdateRequest;
import com.noboseki.tasktimer.repository.UserDao;
import com.noboseki.tasktimer.service.util.UserService.UserServiceUtil;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringJUnitWebConfig
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class UserServiceTest {
    @Mock
    private UserDao userDao;
    @Mock
    private UserServiceUtil userServiceUtil;
    @Mock
    private AuthorityService authorityService;
    @Mock
    private ProfileImgService profileImgService;
    @Mock
    private ConfirmationTokenService tokenService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Spy
    @InjectMocks
    private UserService service;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .username("test")
                .email("test@test.com")
                .password("test")
                .enabled(true).build();
    }

    @Nested
    @DisplayName("Save")
    class UserServiceTestSave {

        @BeforeEach
        void setUp() {
            when(userDao.save(any())).thenReturn(user);
        }

        @AfterEach
        void tearDown() {
            verify(userDao, times(1)).save(any(User.class));
            verify(userDao, times(1)).findByEmailAndPassword(anyString(), anyString());
        }

        @Test
        @DisplayName("Correct")
        void correct() {
            //When
            when(userDao.findByEmailAndPassword(anyString(), anyString())).thenReturn(Optional.of(user));

            User response = service.saveUser(user);

            //Then
            assertEquals(response.getEmail(), user.getEmail());
            assertEquals(response.getUsername(), user.getUsername());
            assertEquals(response.getPassword(), user.getPassword());
            assertEquals(response.getId(), user.getId());
        }

        @Test
        @DisplayName("Save Error")
        void saveError() {
            //When
            when(userDao.save(any())).thenReturn(null);

            //Then
            Throwable response = assertThrows(SaveException.class,
                    () -> service.saveUser(user));
            assertEquals(ExceptionTextConstants.save(user.getClass().getSimpleName(), user.getEmail()), response.getMessage());
        }

        @Test
        @DisplayName("Find by email and password Error")
        void findByEmailAndPasswordError() {
            //When
            when(userDao.findByEmailAndPassword(anyString(), anyString())).thenReturn(Optional.empty());

            //Then
            Throwable response = assertThrows(SaveException.class, () -> service.saveUser(user));
            assertEquals(ExceptionTextConstants.save(user.getClass().getSimpleName(), user.getEmail()), response.getMessage());
        }
    }

    @Nested
    @DisplayName("Delete")
    class UserServiceTestDelete {

        @AfterEach
        void tearDown() {
            verify(userDao, times(1)).findByEmail(anyString());
        }

        @Test
        @DisplayName("Correct")
        void correct() {
            //When
            when(userDao.findByEmail(anyString())).thenReturn(Optional.of(user));

            boolean response = service.deleteUser(user);
            assertTrue(response);
        }

        @Test
        @DisplayName("Find by email error")
        void findByEmailError() {
            Throwable response = assertThrows(DeleteException.class, () -> service.deleteUser(user));
            assertEquals(ExceptionTextConstants.delete("email", user.getEmail()), response.getMessage());
        }
    }

    @Nested
    @DisplayName("Find by email")
    class UserServiceTestFindByEmile {
        @AfterEach
        void tearDown() {
            verify(userDao, times(1)).findByEmail(anyString());
        }

        @Test
        @DisplayName("Correct")
        void correct() {
            //When
            when(userDao.findByEmail(anyString())).thenReturn(Optional.of(user));
            User response = service.findByEmile("test@test.com");

            //Then
            assertEquals(response.getEmail(), user.getEmail());
            assertEquals(response.getUsername(), user.getUsername());
            assertEquals(response.getPassword(), user.getPassword());
            assertEquals(response.getId(), user.getId());
        }

        @Test
        @DisplayName("Find by email error")
        void findByEmailError() {
            when(userDao.findByEmail(anyString())).thenReturn(Optional.empty());
            Throwable response = assertThrows(ResourceNotFoundException.class, () -> service.findByEmile("test@test.com"));
            assertEquals(ExceptionTextConstants.resourceNotFound(user.getClass().getSimpleName(), user.getEmail()), response.getMessage());
        }
    }

    @Nested
    @DisplayName("Update profile")
    class UserServiceTestUpdateProfile {

        @Test
        @DisplayName("Correct")
        void correct() {
            UserServiceUpdateRequest request = new UserServiceUpdateRequest(
                    "updateTest",
                    "updateTest@test.com",
                    "updateUrl");

            //When
            when(userDao.findByEmailAndPassword(anyString(), anyString())).thenReturn(Optional.of(user));
            when(userDao.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(userDao.save(any())).thenReturn(user);

            String response = service.updateProfile(user, request);

            //That
            assertEquals("User profile has been updated", response);
            verify(userDao, times(1)).findByEmailAndPassword(anyString(), anyString());
            verify(userDao, times(1)).findByEmail(anyString());
            verify(userDao, times(1)).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Change password")
    class UserServiceTestChangePassword {
        ConfirmationToken token;
        UserServiceChangePasswordRequest request;

        @BeforeEach
        void setUp() {
            request = new UserServiceChangePasswordRequest(
                    "tokenTest",
                    "oldPassword",
                    "password");

            token = new ConfirmationToken();
            token.setUser(user);

            when(tokenService.getByTokenAndType(anyString(), any(TokenType.class))).thenReturn(token);
            when(userDao.findByEmail(anyString())).thenReturn(Optional.of(user));
        }

        @Test
        @DisplayName("Correct")
        void correct() {
            //When
            when(passwordEncoder.matches(any(), anyString())).thenReturn(true);
            doReturn(user).when(service).saveUser(any(User.class));

            String response = service.changePassword(request);

            //Then
            assertEquals("Password has been changed", response);
            verify(tokenService, times(1)).getByTokenAndType(anyString(), any(TokenType.class));
            verify(userDao, times(1)).findByEmail(anyString());
            verify(passwordEncoder, times(1)).matches(any(), anyString());
        }

        @Test
        @DisplayName("Password encoder error")
        void passwordEncoderError() {
            //Then
            Throwable response = assertThrows(InvalidException.class, () -> service.changePassword(request));
            assertEquals(ExceptionTextConstants.invalid("Old password", request.getOldPassword()), response.getMessage());
        }
    }

    @Nested
    @DisplayName("Change password request")
    class UserServiceTestChangePasswordRequest {

        @BeforeEach
        void setUp() {
            ConfirmationToken token = new ConfirmationToken();
            token.setConfirmationToken(UUID.randomUUID().toString());

            when(userDao.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(tokenService.createTokenForUser(any(User.class), any(TokenType.class))).thenReturn(token);
        }

        @Test
        @DisplayName("Correct")
        void correct() {
            //When
            when(userServiceUtil.changePasswordEmileSender(anyString(), anyString())).thenReturn(true);

            String response = service.changePasswordRequest("test@test.com");

            //Then
            assertEquals("Emile has been sent", response);
            verify(userDao, times(1)).findByEmail(anyString());
            verify(tokenService, times(1)).createTokenForUser(any(User.class), any(TokenType.class));
            verify(userServiceUtil, times(1)).changePasswordEmileSender(anyString(), anyString());
        }

        @Test
        @DisplayName("Save error")
        void saveError() {
            //When
            when(userServiceUtil.changePasswordEmileSender(anyString(), anyString())).thenThrow(SaveException.class);

            //Then
            Throwable response = assertThrows(SaveException.class, () -> service.changePasswordRequest("test@test.com"));
            assertEquals(ExceptionTextConstants.save("token", "password token"), response.getMessage());
            verify(userDao, times(1)).findByEmail(anyString());
            verify(tokenService, times(1)).createTokenForUser(any(User.class), any(TokenType.class));
            verify(userServiceUtil, times(1)).changePasswordEmileSender(anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("Create")
    class UserServiceTestCreate {
        UserServiceCreateRequest request;
        ConfirmationToken token;

        @BeforeEach
        void setUp() {
            request = new UserServiceCreateRequest(
                    "test@test.com",
                    "password",
                    "username");

            token = new ConfirmationToken();
            token.setConfirmationToken(UUID.randomUUID().toString());

            doReturn(user).when(service).saveUser(any(User.class));
            when(authorityService.findByRole(anyString())).thenReturn(new Authority());
            when(profileImgService.findByName(anyString())).thenReturn(new ProfileImg());
            when(tokenService.createTokenForUser(any(User.class), any(TokenType.class))).thenReturn(token);
            when(userServiceUtil.mapToUser(
                    any(UserServiceCreateRequest.class), any(Authority.class), any(ProfileImg.class))).thenReturn(user);
        }

        @Test
        @DisplayName("Correct")
        void correct() {
            //When
            when(userServiceUtil.activationEmileSender(anyString(), anyString())).thenReturn(true);

            String response = service.create(request);

            //Then
            assertEquals("User has been created", response);
            verify(authorityService, times(1)).findByRole(anyString());
            verify(profileImgService, times(1)).findByName(anyString());
            verify(userServiceUtil, times(1)).activationEmileSender(anyString(), anyString());
            verify(tokenService, times(1)).createTokenForUser(any(User.class), any(TokenType.class));
            verify(userServiceUtil, times(1)).mapToUser(
                    any(UserServiceCreateRequest.class), any(Authority.class), any(ProfileImg.class));
        }

        @Test
        @DisplayName("Duplicate error")
        void duplicateError() {
            //When
            when(userDao.findByEmail(anyString())).thenReturn(Optional.of(user));


            Throwable response = assertThrows(DuplicateException.class, () -> service.create(request));
            assertEquals(ExceptionTextConstants.duplicate(user.getClass().getSimpleName(), user.getEmail()), response.getMessage());
            verify(userDao, times(1)).findByEmail(anyString());
        }

        @Test
        @DisplayName("Save error")
        void saveError() {
            //When
            doReturn(true).when(service).deleteUser(any(User.class));
            when(tokenService.createTokenForUser(any(User.class), any(TokenType.class))).thenThrow(SaveException.class);

            //Then
            Throwable response = assertThrows(SaveException.class, () -> service.create(request));
            assertEquals(ExceptionTextConstants.save("token", "activate token"), response.getMessage());
            verify(authorityService, times(1)).findByRole(anyString());
            verify(profileImgService, times(1)).findByName(anyString());
            verify(tokenService, times(1)).createTokenForUser(any(User.class), any(TokenType.class));
            verify(userServiceUtil, times(1)).mapToUser(
                    any(UserServiceCreateRequest.class), any(Authority.class), any(ProfileImg.class));
        }
    }

    @Nested
    @DisplayName("Validate")
    class UserServiceTestValidate {

        @Test
        @DisplayName("Create")
        void create() {
            //Given
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();

            UserServiceCreateRequest minPassword = new UserServiceCreateRequest("test@test.com", "passw", "username");
            UserServiceCreateRequest maxPassword = new UserServiceCreateRequest("test@test.com", "password123456789123456789123456", "username");
            UserServiceCreateRequest minUsername = new UserServiceCreateRequest("test@test.com", "password", "usern");
            UserServiceCreateRequest maxUsername = new UserServiceCreateRequest("test@test.com", "password", "username1234567");
            UserServiceCreateRequest email = new UserServiceCreateRequest("test.com", "password", "username");
            UserServiceCreateRequest allIncorrect = new UserServiceCreateRequest("test.com", "passw", "usern");
            UserServiceCreateRequest allCorrect = new UserServiceCreateRequest("test@test.com", "password", "username");

            //When
            Set<ConstraintViolation<UserServiceCreateRequest>> violationMinPassword = validator.validate(minPassword);
            Set<ConstraintViolation<UserServiceCreateRequest>> violationMaxPassword = validator.validate(maxPassword);
            Set<ConstraintViolation<UserServiceCreateRequest>> violationMinUsername = validator.validate(minUsername);
            Set<ConstraintViolation<UserServiceCreateRequest>> violationMaxUsername = validator.validate(maxUsername);
            Set<ConstraintViolation<UserServiceCreateRequest>> violationEmail = validator.validate(email);
            Set<ConstraintViolation<UserServiceCreateRequest>> violationAllIncorrect = validator.validate(allIncorrect);
            Set<ConstraintViolation<UserServiceCreateRequest>> violationAllCorrect = validator.validate(allCorrect);

            //Then
            assertThat(violationMinPassword.size()).isEqualTo(1);
            assertThat(violationMaxPassword.size()).isEqualTo(1);
            assertThat(violationMinUsername.size()).isEqualTo(1);
            assertThat(violationMaxUsername.size()).isEqualTo(1);
            assertThat(violationEmail.size()).isEqualTo(1);
            assertThat(violationAllIncorrect.size()).isEqualTo(3);
            assertTrue(violationAllCorrect.isEmpty());
        }

        @Test
        @DisplayName("Change password")
        void changePassword() {
            //Given
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();

            UserServiceChangePasswordRequest minNewPassword = new UserServiceChangePasswordRequest("tokenTest", "oldPassword", "passw");
            UserServiceChangePasswordRequest maxNewPassword = new UserServiceChangePasswordRequest("tokenTest", "oldPassword", "password12345678912345678912345");
            UserServiceChangePasswordRequest nullOldPassword = new UserServiceChangePasswordRequest("tokenTest", null, "password");
            UserServiceChangePasswordRequest nullToken = new UserServiceChangePasswordRequest(null, "oldPassword", "password");
            UserServiceChangePasswordRequest allIncorrect = new UserServiceChangePasswordRequest(null, null, "pass");
            UserServiceChangePasswordRequest allCorrect = new UserServiceChangePasswordRequest("tokenTest", "oldPassword", "password");

            //When
            Set<ConstraintViolation<UserServiceChangePasswordRequest>> violationMinNewPassword = validator.validate(minNewPassword);
            Set<ConstraintViolation<UserServiceChangePasswordRequest>> violationMaxNewPassword = validator.validate(maxNewPassword);
            Set<ConstraintViolation<UserServiceChangePasswordRequest>> violationNullOldPassword = validator.validate(nullOldPassword);
            Set<ConstraintViolation<UserServiceChangePasswordRequest>> violationNullToken = validator.validate(nullToken);
            Set<ConstraintViolation<UserServiceChangePasswordRequest>> violationAllIncorrect = validator.validate(allIncorrect);
            Set<ConstraintViolation<UserServiceChangePasswordRequest>> violationAllCorrect = validator.validate(allCorrect);

            //Then
            assertThat(violationMinNewPassword.size()).isEqualTo(1);
            assertThat(violationMaxNewPassword.size()).isEqualTo(1);
            assertThat(violationNullOldPassword.size()).isEqualTo(1);
            assertThat(violationNullToken.size()).isEqualTo(1);
            assertThat(violationAllIncorrect.size()).isEqualTo(3);
            assertTrue(violationAllCorrect.isEmpty());
        }

        @Test
        @DisplayName("Update profile")
        void updateProfile() {
            //Given
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();

            UserServiceUpdateRequest minUsername = new UserServiceUpdateRequest("test", "test@test.com", "testUrl");
            UserServiceUpdateRequest maxUsername = new UserServiceUpdateRequest("test123456789123456789", "test@test.com", "testUrl");
            UserServiceUpdateRequest email = new UserServiceUpdateRequest("test12", "test", "testUrl");
            UserServiceUpdateRequest notNull = new UserServiceUpdateRequest("test123456789123", "test@test.com", null);
            UserServiceUpdateRequest allIncorrect = new UserServiceUpdateRequest("test", "test", null);
            UserServiceUpdateRequest allCorrect = new UserServiceUpdateRequest("test12", "test@test.com", "testUrl");

            //When
            Set<ConstraintViolation<UserServiceUpdateRequest>> violationMinUsername = validator.validate(minUsername);
            Set<ConstraintViolation<UserServiceUpdateRequest>> violationMaxUsername = validator.validate(maxUsername);
            Set<ConstraintViolation<UserServiceUpdateRequest>> violationEmail = validator.validate(email);
            Set<ConstraintViolation<UserServiceUpdateRequest>> violationNotNull = validator.validate(notNull);
            Set<ConstraintViolation<UserServiceUpdateRequest>> violationAllIncorrect = validator.validate(allIncorrect);
            Set<ConstraintViolation<UserServiceUpdateRequest>> violationAllCorrect = validator.validate(allCorrect);

            //Then
            assertThat(violationMinUsername.size()).isEqualTo(1);
            assertThat(violationMaxUsername.size()).isEqualTo(1);
            assertThat(violationEmail.size()).isEqualTo(1);
            assertThat(violationNotNull.size()).isEqualTo(1);
            assertThat(violationAllIncorrect.size()).isEqualTo(3);
            assertTrue(violationAllCorrect.isEmpty());
        }
    }
}