package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.ConfirmationToken;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.repository.ConfirmationTokenDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationTokenDao tokenDao;
    private final UserService userService;

    public ConfirmationToken createTokenForUser(User user) {
        userService.findByEmile(user.getEmail());
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
            User user = userService.findByEmile(confirmationToken.get().getUser().getEmail());
            user.setEnabled(true);
            userService.saveUser(user);
            return "Congratulations! Your account has been activated and email is verified!";
        } else {
            return "Valid token";
        }
    }

}
