package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.*;
import com.noboseki.tasktimer.exeption.*;
import com.noboseki.tasktimer.playload.UserServiceChangePasswordRequest;
import com.noboseki.tasktimer.playload.UserServiceCreateRequest;
import com.noboseki.tasktimer.playload.UserServiceUpdateRequest;
import com.noboseki.tasktimer.repository.UserDao;
import com.noboseki.tasktimer.service.constants.ServiceTextConstants;
import com.noboseki.tasktimer.service.util.UserService.UserServiceUtil;
import net.bytebuddy.utility.RandomString;
import org.assertj.core.api.AbstractIntegerAssert;
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
    private final static String TEST = "test";
    private final String TEST_EMAIL = "test@test.com";
    private final String USER = ServiceTextConstants.getUser();
    private final String PASSWORD = "Password";
    private final String USERNAME = "Username";

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
                .username(TEST)
                .email(TEST_EMAIL)
                .password(TEST)
                .enabled(true).build();
    }

    @Nested
    @DisplayName("Save")
    class UserServiceTestSave {
        private String SAVE_EXCEPTION_MESSAGE;

        @BeforeEach
        void setUp() {
            SAVE_EXCEPTION_MESSAGE = ExceptionTextConstants.save(user.getClass().getSimpleName(), user.getEmail());
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
            assertEqualsUserValues(response);
        }

        @Test
        @DisplayName("Save Error")
        void saveError() {
            //When
            when(userDao.save(any())).thenReturn(null);

            //Then
            Throwable response = assertThrows(SaveException.class, () -> service.saveUser(user));
            assertEquals(SAVE_EXCEPTION_MESSAGE, response.getMessage());
        }

        @Test
        @DisplayName("Find by email and password Error")
        void findByEmailAndPasswordError() {
            //When
            when(userDao.findByEmailAndPassword(anyString(), anyString())).thenReturn(Optional.empty());

            //Then
            Throwable response = assertThrows(SaveException.class, () -> service.saveUser(user));
            assertEquals(SAVE_EXCEPTION_MESSAGE, response.getMessage());
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
            final String deleteExceptionMessage = ExceptionTextConstants.delete(USER, UserServiceTest.this.user.getEmail());

            Throwable response = assertThrows(DeleteException.class, () -> service.deleteUser(user));
            assertEquals(deleteExceptionMessage, response.getMessage());
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
            User response = service.findByEmile(TEST_EMAIL);

            //Then
            assertEqualsUserValues(response);
        }

        @Test
        @DisplayName("Find by email error")
        void findByEmailError() {
            final String exceptionNotFoundMessage = ExceptionTextConstants.resourceNotFound(user.getClass().getSimpleName(), user.getEmail());

            when(userDao.findByEmail(anyString())).thenReturn(Optional.empty());
            Throwable response = assertThrows(ResourceNotFoundException.class, () -> service.findByEmile(TEST_EMAIL));
            assertEquals(exceptionNotFoundMessage, response.getMessage());
        }
    }

    @Nested
    @DisplayName("Update profile")
    class UserServiceTestUpdateProfile {

        @Test
        @DisplayName("Correct")
        void correct() {
            final String updateTest = "updateTest";
            final String updateEmail = "updateTest@test.com";
            final String updateUrl = "updateUrl";
            final String responseMessage = ServiceTextConstants.hasBeenUpdated(USER);

            UserServiceUpdateRequest request = new UserServiceUpdateRequest(
                    updateTest,
                    updateEmail,
                    updateUrl);

            //When
            when(userDao.findByEmailAndPassword(anyString(), anyString())).thenReturn(Optional.of(user));
            when(userDao.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(userDao.save(any())).thenReturn(user);

            String response = service.updateProfile(user, request);

            //That
            assertEquals(responseMessage, response);
            verify(userDao, times(1)).findByEmailAndPassword(anyString(), anyString());
            verify(userDao, times(1)).findByEmail(anyString());
            verify(userDao, times(1)).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Change password")
    class UserServiceTestChangePassword {
        private final String TOKEN_TEST = "tokenTest";
        private final String OLD_PASSWORD = "Old password";

        ConfirmationToken token;
        UserServiceChangePasswordRequest request;

        @BeforeEach
        void setUp() {
            request = new UserServiceChangePasswordRequest(
                    TOKEN_TEST,
                    OLD_PASSWORD,
                    PASSWORD);

            token = new ConfirmationToken();
            token.setUser(user);

            when(tokenService.getByTokenAndType(anyString(), any(TokenType.class))).thenReturn(token);
            when(userDao.findByEmail(anyString())).thenReturn(Optional.of(user));
        }

        @Test
        @DisplayName("Correct")
        void correct() {
            final String responseMessage = ServiceTextConstants.hasBeenUpdated(PASSWORD);

            //When
            when(passwordEncoder.matches(any(), anyString())).thenReturn(true);
            doReturn(user).when(service).saveUser(any(User.class));

            String response = service.changePassword(request);

            //Then
            assertEquals(responseMessage, response);
            verify(tokenService, times(1)).getByTokenAndType(anyString(), any(TokenType.class));
            verify(userDao, times(1)).findByEmail(anyString());
            verify(passwordEncoder, times(1)).matches(any(), anyString());
        }

        @Test
        @DisplayName("Password encoder error")
        void passwordEncoderError() {
            Throwable response = assertThrows(InvalidException.class, () -> service.changePassword(request));
            assertEquals(ExceptionTextConstants.invalid(OLD_PASSWORD, request.getOldPassword()), response.getMessage());
        }
    }

    @Nested
    @DisplayName("Change password request")
    class UserServiceTestChangePasswordRequest {
        private final String PASSWORD_TOKEN = "password token";

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
            final String expectedMessage = ServiceTextConstants.emailHasBeenSend(TEST_EMAIL);

            when(userServiceUtil.changePasswordEmileSender(anyString(), anyString())).thenReturn(true);

            String response = service.changePasswordRequest(TEST_EMAIL);

            //Then
            assertEquals(expectedMessage, response);
            verify(userDao, times(1)).findByEmail(anyString());
            verify(tokenService, times(1)).createTokenForUser(any(User.class), any(TokenType.class));
            verify(userServiceUtil, times(1)).changePasswordEmileSender(anyString(), anyString());
        }

        @Test
        @DisplayName("Save error")
        void saveError() {
            final String expectedMessage = ExceptionTextConstants.save(ServiceTextConstants.getToken(), PASSWORD_TOKEN);

            when(userServiceUtil.changePasswordEmileSender(anyString(), anyString())).thenThrow(SaveException.class);


            Throwable response = assertThrows(SaveException.class, () -> service.changePasswordRequest(TEST_EMAIL));

            assertEquals(expectedMessage, response.getMessage());
            verify(userDao, times(1)).findByEmail(anyString());
            verify(tokenService, times(1)).createTokenForUser(any(User.class), any(TokenType.class));
            verify(userServiceUtil, times(1)).changePasswordEmileSender(anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("Create")
    class UserServiceTestCreate {
        private final String TOKEN = "Token";
        private final String ACTIVATE_TOKEN = "activate token";

        UserServiceCreateRequest request;
        ConfirmationToken token;

        @BeforeEach
        void setUp() {
            request = new UserServiceCreateRequest(
                    TEST_EMAIL,
                    PASSWORD,
                    USERNAME);

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
            assertEquals(ServiceTextConstants.hasBeenCreate(USER), response);
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
            final String duplicateExceptionMessage = ExceptionTextConstants.duplicate(user.getClass().getSimpleName(), user.getEmail());

            when(userDao.findByEmail(anyString())).thenReturn(Optional.of(user));

            Throwable response = assertThrows(DuplicateException.class, () -> service.create(request));

            assertEquals(duplicateExceptionMessage, response.getMessage());
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
            assertEquals(ExceptionTextConstants.save(TOKEN, ACTIVATE_TOKEN), response.getMessage());
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
        private final String RANDOM5 = RandomString.make(5);
        private final String RANDOM15 = RandomString.make(15);
        private final String RANDOM31 = RandomString.make(31);

        private final String NULL = null;
        private final String TOKEN_TEST = "tokenTest";
        private final String OLD_PASSWORD = "oldPassword";
        private final String TEST_URL = "testUrl";

        @Test
        @DisplayName("Create")
        void create() {
            //Given
            Validator validator = getValidator();

            UserServiceCreateRequest minPassword = new UserServiceCreateRequest(TEST_EMAIL, RANDOM5, USERNAME);
            UserServiceCreateRequest maxPassword = new UserServiceCreateRequest(TEST_EMAIL, RANDOM31, USERNAME);
            UserServiceCreateRequest minUsername = new UserServiceCreateRequest(TEST_EMAIL, PASSWORD, RANDOM5);
            UserServiceCreateRequest maxUsername = new UserServiceCreateRequest(TEST_EMAIL, PASSWORD, RANDOM15);
            UserServiceCreateRequest email = new UserServiceCreateRequest(RANDOM5, PASSWORD, USERNAME);
            UserServiceCreateRequest allIncorrect = new UserServiceCreateRequest(RANDOM5, RANDOM5, RANDOM5);
            UserServiceCreateRequest allCorrect = new UserServiceCreateRequest(TEST_EMAIL, PASSWORD, USERNAME);

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
            Validator validator = getValidator();

            UserServiceChangePasswordRequest minNewPassword = new UserServiceChangePasswordRequest(TOKEN_TEST, OLD_PASSWORD, RANDOM5);
            UserServiceChangePasswordRequest maxNewPassword = new UserServiceChangePasswordRequest(TOKEN_TEST, OLD_PASSWORD, RANDOM31);
            UserServiceChangePasswordRequest nullOldPassword = new UserServiceChangePasswordRequest(TOKEN_TEST, NULL, PASSWORD);
            UserServiceChangePasswordRequest nullToken = new UserServiceChangePasswordRequest(NULL, OLD_PASSWORD, PASSWORD);
            UserServiceChangePasswordRequest allIncorrect = new UserServiceChangePasswordRequest(NULL, NULL, RANDOM5);
            UserServiceChangePasswordRequest allCorrect = new UserServiceChangePasswordRequest(TOKEN_TEST, OLD_PASSWORD, PASSWORD);

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
            Validator validator = getValidator();

            UserServiceUpdateRequest minUsername = new UserServiceUpdateRequest(RANDOM5, TEST_EMAIL, TEST_URL);
            UserServiceUpdateRequest maxUsername = new UserServiceUpdateRequest(RANDOM31, TEST_EMAIL, TEST_URL);
            UserServiceUpdateRequest email = new UserServiceUpdateRequest(USERNAME, RANDOM5, TEST_URL);
            UserServiceUpdateRequest notNull = new UserServiceUpdateRequest(USERNAME, TEST_EMAIL, NULL);
            UserServiceUpdateRequest allIncorrect = new UserServiceUpdateRequest(RANDOM5, RANDOM5, NULL);
            UserServiceUpdateRequest allCorrect = new UserServiceUpdateRequest(USERNAME, TEST_EMAIL, TEST_URL);

            Set<ConstraintViolation<UserServiceUpdateRequest>> violationMinUsername = validator.validate(minUsername);
            Set<ConstraintViolation<UserServiceUpdateRequest>> violationMaxUsername = validator.validate(maxUsername);
            Set<ConstraintViolation<UserServiceUpdateRequest>> violationEmail = validator.validate(email);
            Set<ConstraintViolation<UserServiceUpdateRequest>> violationNotNull = validator.validate(notNull);
            Set<ConstraintViolation<UserServiceUpdateRequest>> violationAllIncorrect = validator.validate(allIncorrect);
            Set<ConstraintViolation<UserServiceUpdateRequest>> violationAllCorrect = validator.validate(allCorrect);

            checkValidatorSetsSizeOne(Set.of(violationMinUsername, violationMaxUsername,
                    violationEmail, violationNotNull));
            checkValidatorSet(violationAllIncorrect, 3);
            checkValidatorSet(violationAllCorrect, 0);
        }

        private Validator getValidator() {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            return factory.getValidator();
        }

        private void checkValidatorSetsSizeOne(Set<Set<?>> validatorSets) {
            for (Set<?> set : validatorSets) {
                checkValidatorSet(set, 1);
            }
        }

        private AbstractIntegerAssert<?> checkValidatorSet(Set<?> validatorSet, Integer setSize) {
            return assertThat(validatorSet.size()).isEqualTo(setSize);
        }
    }

    private void assertEqualsUserValues(User expected) {
        assertEquals(expected.getEmail(), user.getEmail());
        assertEquals(expected.getUsername(), user.getUsername());
        assertEquals(expected.getPassword(), user.getPassword());
        assertEquals(expected.getId(), user.getId());
    }
}