package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.ConfirmationToken;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.InvalidException;
import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.repository.ConfirmationTokenDao;
import com.noboseki.tasktimer.repository.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Email;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationTokenDao tokenDao;
    private final UserDao userDao;

    public ConfirmationToken createTokenForUser(User user) {
        userDao.findByEmail(user.getEmail());
        ConfirmationToken token = new ConfirmationToken(user);

        return saveConfirmationToken(token);
    }

    public String activateAccount(String token) {
        ConfirmationToken confirmationToken = tokenDao.findByConfirmationToken(token).orElseThrow(() -> new InvalidException("Token", token));

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
            if (tokenDao.findByUser_Email(token.getUser().getEmail()).isPresent()) {
                return dbToken;
            } else {
                throw saveException;
            }
        } catch (SaveException e) {
            throw saveException;
        }
    }

}
