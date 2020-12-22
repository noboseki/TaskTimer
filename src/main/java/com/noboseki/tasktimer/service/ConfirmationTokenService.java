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

    public String activateAccount(String token) {
        Optional<ConfirmationToken> confirmationToken = tokenDao.findByConfirmationToken(token);

        if (confirmationToken.isPresent()) {
            Optional<User> user = userDao.findByEmail(confirmationToken.get().getUser().getEmail());

            if (user.isPresent()) {
                user.get().setEnabled(true);
                userDao.save(user.get());
                return "Congratulations! Your account has been activated and email is verified!";
            } else {
                return "Verification error";
            }

        } else {
            return "Valid token";
        }
    }
}
