package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.ConfirmationToken;
import com.noboseki.tasktimer.domain.TokenType;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.DeleteException;
import com.noboseki.tasktimer.exeption.InvalidException;
import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.repository.ConfirmationTokenDao;
import com.noboseki.tasktimer.repository.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Email;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationTokenDao tokenDao;
    private final UserDao userDao;

    public ConfirmationToken createTokenForUser(User user, TokenType type) {
        userDao.findByEmail(user.getEmail());
        ConfirmationToken token = new ConfirmationToken(user, type);

        return saveConfirmationToken(token);
    }

    public String activateAccount(String token) {
        ConfirmationToken confirmationToken = tokenDao.findByConfirmationTokenAndType(token, TokenType.ACTIVATE)
                .orElseThrow(() -> new InvalidException("Token", token));

        @Email
        String email = confirmationToken.getUser().getEmail();

        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        user.setEnabled(true);
        userDao.save(user);

        return "Congratulations! Your account has been activated and email is verified!";
    }

    public ConfirmationToken saveConfirmationToken(ConfirmationToken token) {
        SaveException saveException = new SaveException("Token", token.getUser().getUsername());

        try {
            ConfirmationToken dbToken = tokenDao.save(token);
            if (tokenDao.findByUser_EmailAndType(token.getUser().getEmail(), token.getType()).isPresent()) {
                return dbToken;
            } else {
                throw saveException;
            }
        } catch (SaveException e) {
            throw saveException;
        }
    }

    public boolean deleteToken(ConfirmationToken token) {
        DeleteException exception = new DeleteException("token", token.getConfirmationToken());

        try {
            tokenDao.delete(token);
            if (tokenDao.findById(token.getId()).isPresent()) {
                throw exception;
            }
            return true;
        } catch (Exception e) {
            throw exception;
        }
    }

    public ConfirmationToken getByTokenAndType(String token, TokenType type) {
        return tokenDao.findByConfirmationTokenAndType(token, type).orElseThrow(() -> new ResourceNotFoundException("Token", "token", token));
    }

    public Optional<ConfirmationToken> getByUser_EmailAndType(String email, TokenType type) {
        return tokenDao.findByUser_EmailAndType(email, type);
    }

}
