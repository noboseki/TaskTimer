package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.ConfirmationToken;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.repository.ConfirmationTokenDao;
import com.noboseki.tasktimer.repository.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationTokenDao tokenDao;
    private final UserDao userDao;

    public ConfirmationToken createTokenForUser(User user) {
        userDao.findByEmail(user.getEmail());
        ConfirmationToken token = new ConfirmationToken(user);

        try {
            return tokenDao.save(token);
        } catch (Exception e) {
            throw e;
        }
    }

    public String activateAccount(String token) {
        Optional<ConfirmationToken> confirmationToken = tokenDao.findByConfirmationToken(token);

        if (confirmationToken.isPresent()) {
            User user = userDao.findByEmail(confirmationToken.get().getUser().getEmail()).get();
            user.setEnabled(true);
            userDao.save(user);
            return "Congratulations! Your account has been activated and email is verified!";
        } else {
            return "Valid token";
        }
    }

}
