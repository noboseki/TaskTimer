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
import com.noboseki.tasktimer.service.constants.ServiceTextConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Email;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenService {
    private static final String TOKEN = ServiceTextConstants.getToken();
    private static final String ACTIVATE_ACCOUNT = ServiceTextConstants.getActivateAccount();

    private final ConfirmationTokenDao tokenDao;
    private final UserDao userDao;

    public ConfirmationToken createTokenForUser(User user, TokenType type) {
        userDao.findByEmail(user.getEmail());
        ConfirmationToken token = new ConfirmationToken(user, type);

        return saveConfirmationToken(token);
    }

    public String activateAccount(String token) {
        ConfirmationToken confirmationToken = tokenDao.findByConfirmationTokenAndType(token, TokenType.ACTIVATE)
                .orElseThrow(() -> new InvalidException(TOKEN, token));

        @Email
        String email = confirmationToken.getUser().getEmail();

        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ServiceTextConstants.getUser(), email));
        user.setEnabled(true);
        userDao.save(user);

        return ACTIVATE_ACCOUNT;
    }

    public ConfirmationToken saveConfirmationToken(ConfirmationToken token) {
        SaveException saveException = new SaveException(TOKEN, token.getUser().getUsername());

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
        tokenDao.delete(token);
        if (tokenDao.findById(token.getId()).isPresent()) {
            throw new DeleteException(TOKEN, token.getConfirmationToken());
        }
        return true;
    }

    public ConfirmationToken getByTokenAndType(String token, TokenType type) {
        return tokenDao.findByConfirmationTokenAndType(token, type).orElseThrow(() -> new ResourceNotFoundException(TOKEN, token));
    }

    public Optional<ConfirmationToken> getByUserEmailAndType(String email, TokenType type) {
        return tokenDao.findByUser_EmailAndType(email, type);
    }

}
