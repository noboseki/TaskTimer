package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.ConfirmationToken;
import com.noboseki.tasktimer.domain.TokenType;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.*;
import com.noboseki.tasktimer.repository.ConfirmationTokenDao;
import com.noboseki.tasktimer.repository.UserDao;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringJUnitWebConfig
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConfirmationTokenServiceTest {
    @Mock
    private ConfirmationTokenDao tokenDao;
    @Mock
    private UserDao userDao;
    @InjectMocks
    private ConfirmationTokenService service;

    private User user;
    private ConfirmationToken token;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .username("test")
                .email("test@test.com")
                .password("test")
                .enabled(true).build();

        token = new ConfirmationToken();
        token.setId(UUID.randomUUID());
        token.setConfirmationToken(UUID.randomUUID().toString());
        token.setType(TokenType.ACTIVATE);
        token.setUser(user);
    }

    @Test
    @DisplayName("Create token for user")
    void createTokenForUser() {
        //When
        when(tokenDao.save(any(ConfirmationToken.class))).thenReturn(token);
        when(tokenDao.findByUser_EmailAndType(anyString(), any(TokenType.class)))
                .thenReturn(Optional.of(token));

        ConfirmationToken response = service.createTokenForUser(user, TokenType.PASSWORD);

        //Then
        assertEquals(TokenType.ACTIVATE, response.getType());
        assertEquals(user, response.getUser());
    }

    @Nested
    @DisplayName("Activate account")
    class activateAccount {

        @Test
        @DisplayName("Correct")
        void correct() {
            //When
            when(tokenDao.findByConfirmationTokenAndType(anyString(), any(TokenType.class)))
                    .thenReturn(Optional.of(token));
            when(userDao.findByEmail(anyString())).thenReturn(Optional.of(user));

            String response = service.activateAccount(UUID.randomUUID().toString());

            //Then
            assertEquals("Congratulations! Your account has been activated and email is verified!", response);
            verify(userDao, times(1)).findByEmail(anyString());
            verify(tokenDao, times(1))
                    .findByConfirmationTokenAndType(anyString(), any(TokenType.class));
        }

        @Test
        @DisplayName("Invalid token error")
        void invalidTokenException() {
            String uuid = UUID.randomUUID().toString();

            Throwable response = assertThrows(InvalidException.class, () -> service.activateAccount(uuid));

            assertEquals(ExceptionTextConstants.invalid("Token", uuid), response.getMessage());
        }

        @Test
        @DisplayName("User not found error")
        void userNotFound() {
            when(tokenDao.findByConfirmationTokenAndType(anyString(), any(TokenType.class)))
                    .thenReturn(Optional.of(token));

            Throwable response = assertThrows(ResourceNotFoundException.class,
                    () -> service.activateAccount(UUID.randomUUID().toString()));

            assertEquals(ExceptionTextConstants.resourceNotFound("User",  user.getEmail()), response.getMessage());
            verify(userDao, times(1)).findByEmail(anyString());
        }
    }

    @Nested
    @DisplayName("Save confirmationToken")
    class saveConfirmationToken {

        @Test
        @DisplayName("Correct")
        void correct() {
            //When
            when(tokenDao.save(any(ConfirmationToken.class))).thenReturn(token);
            when(tokenDao.findByUser_EmailAndType(anyString(), any(TokenType.class)))
                    .thenReturn(Optional.of(token));

            ConfirmationToken response = service.saveConfirmationToken(token);

            //Then
            assertEquals(response, token);
            verify(tokenDao, times(1)).save(any(ConfirmationToken.class));
            verify(tokenDao, times(1))
                    .findByUser_EmailAndType(anyString(), any(TokenType.class));
        }

        @Test
        @DisplayName("Save error")
        void saveError() {
            when(tokenDao.save(any(ConfirmationToken.class))).thenReturn(token);

            Throwable response = assertThrows(SaveException.class, () -> service.saveConfirmationToken(token));

            assertEquals(ExceptionTextConstants.save("Token", token.getUser().getUsername()), response.getMessage());
            verify(tokenDao, times(1)).save(any(ConfirmationToken.class));
        }
    }

    @Nested
    @DisplayName("Delete token")
    class deleteToken {

        @Test
        @DisplayName("Correct")
        void correct() {
            when(tokenDao.findById(any(UUID.class))).thenReturn(Optional.empty());

            boolean response = service.deleteToken(token);

            assertTrue(response);
            verify(tokenDao, times(1)).findById(any(UUID.class));
        }

        @Test
        @DisplayName("Delete error")
        void deleteError() {
            when(tokenDao.findById(any(UUID.class))).thenReturn(Optional.of(token));

            Throwable response = assertThrows(DeleteException.class, () -> service.deleteToken(token));

            assertEquals(ExceptionTextConstants.delete("Token", token.getConfirmationToken()), response.getMessage());
            verify(tokenDao, times(1)).findById(any(UUID.class));
        }
    }
}