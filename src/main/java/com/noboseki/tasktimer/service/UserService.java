package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.*;
import com.noboseki.tasktimer.exeption.*;
import com.noboseki.tasktimer.playload.UserServiceChangePasswordRequest;
import com.noboseki.tasktimer.playload.UserServiceCreateRequest;
import com.noboseki.tasktimer.playload.UserServiceGetResponse;
import com.noboseki.tasktimer.playload.UserServiceUpdateRequest;
import com.noboseki.tasktimer.repository.UserDao;
import com.noboseki.tasktimer.service.util.UserService.UserServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;
    private final UserServiceUtil userServiceUtil;
    private final AuthorityService authorityService;
    private final ProfileImgService profileImgService;
    private final ConfirmationTokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public String create(@Valid UserServiceCreateRequest request) {
        if (userDao.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateException("User", "emile", request.getEmail());
        }
        Authority userAuthority = authorityService.findByRole("ROLE_USER");
        ProfileImg profileImg = profileImgService.findByName("Yondu");
        User user = userServiceUtil.mapToUser(request, userAuthority, profileImg);
        User dbUser = saveUser(user);

        try {
            ConfirmationToken token = tokenService.createTokenForUser(dbUser, TokenType.ACTIVATE);
            userServiceUtil.activationEmileSender(token.getConfirmationToken(), request.getEmail());
            return "User has been created";
        } catch (SaveException e) {
            deleteUser(dbUser);
            throw new SaveException("token", "create activate token");
        }
    }

    public String changePasswordRequest(String emile) {
        User dbUser = findByEmile(emile);
        Optional<ConfirmationToken> dbToken = tokenService.getByUser_EmailAndType(emile, TokenType.PASSWORD);

        dbToken.ifPresent(tokenService::deleteToken);

        try {
            ConfirmationToken token = tokenService.createTokenForUser(dbUser, TokenType.PASSWORD);
            userServiceUtil.changePasswordEmileSender(token.getConfirmationToken(), emile);
            return "Emile has been send";
        } catch (SaveException e) {
            throw new SaveException("token", "create change password token");
        }
    }

    public String changePassword(UserServiceChangePasswordRequest request) {
        ConfirmationToken token = tokenService.getByTokenAndType(request.getToken(), TokenType.PASSWORD);
        User dbUser = findByEmile(token.getUser().getEmail());

        if (!passwordEncoder.matches(request.getOldPassword(), dbUser.getPassword())) {
            throw new InvalidException("OldPassword", request.getOldPassword());
        }

        dbUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        saveUser(dbUser);

        return "Password has been changed";
    }

    public UserServiceGetResponse get(User user) {
        User dbUser = findByEmile(user.getEmail());
        return userServiceUtil.mapToResponse(dbUser);
    }

    public String updateProfile(User user, @Valid UserServiceUpdateRequest request) {
        User dbUser = findByEmile(user.getEmail());
        ProfileImg profileImg = profileImgService.findByName(request.getProfileImgName());

        dbUser.setUsername(request.getUsername());
        dbUser.setEmail(request.getEmail());
        dbUser.setProfileImg(profileImg);

        saveUser(dbUser);

        return "User profile has been updated";
    }

    public User findByEmile(String email) {
        return userDao.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    public User saveUser(User user) {
        SaveException saveException = new SaveException("User", user.getEmail());

        try {
            User dbUser = userDao.save(user);
            if (userDao.findByEmailAndPassword(user.getEmail(), user.getPassword()).isPresent()) {
                return dbUser;
            } else {
                throw saveException;
            }
        } catch (SaveException e) {
            throw saveException;
        }
    }

    public boolean deleteUser(User user) {
        DeleteException deleteException = new DeleteException("emile", user.getEmail());

        try {
            userDao.delete(user);
            if (userDao.findByEmail(user.getEmail()).isPresent()) {
                return true;
            } else {
                throw deleteException;
            }
        } catch (DeleteException e) {
            throw deleteException;
        }
    }
}