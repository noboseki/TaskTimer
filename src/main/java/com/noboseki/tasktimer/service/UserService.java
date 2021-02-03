package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.*;
import com.noboseki.tasktimer.exeption.*;
import com.noboseki.tasktimer.playload.UserServiceChangePasswordRequest;
import com.noboseki.tasktimer.playload.UserServiceCreateRequest;
import com.noboseki.tasktimer.playload.UserServiceGetResponse;
import com.noboseki.tasktimer.playload.UserServiceUpdateRequest;
import com.noboseki.tasktimer.repository.UserDao;
import com.noboseki.tasktimer.service.constants.ServiceTextConstants;
import com.noboseki.tasktimer.service.util.UserService.UserServiceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final String TOKEN = ServiceTextConstants.getToken();
    private static final String USER = ServiceTextConstants.getUser();
    private static final String ACTIVATE_TOKEN = "activate token";
    private static final String PASSWORD_TOKEN = "password token";
    private static final String OLD_PASSWORD = "Old password";
    private static final String PASSWORD = "Password";
    private static final String ROLE_USER = "ROLE_USER";

    private final UserDao userDao;
    private final UserServiceUtil userServiceUtil;
    private final AuthorityService authorityService;
    private final ProfileImgService profileImgService;
    private final ConfirmationTokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public String create(@Valid UserServiceCreateRequest request) {
        if (userDao.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateException(USER, request.getEmail());
        }

        Authority userAuthority = authorityService.findByRole(ROLE_USER);
        ProfileImg profileImg = profileImgService.findByName(ServiceTextConstants.getStandardAvatarName());
        User user = userServiceUtil.mapToUser(request, userAuthority, profileImg);
        User dbUser = saveUser(user);

        try {
            ConfirmationToken token = tokenService.createTokenForUser(dbUser, TokenType.ACTIVATE);
            userServiceUtil.activationEmileSender(token.getConfirmationToken(), request.getEmail());
            return ServiceTextConstants.hasBeenCreate(USER);
        } catch (SaveException e) {
            deleteUser(dbUser);
            throw new SaveException(TOKEN, ACTIVATE_TOKEN);
        }
    }

    public String changePasswordRequest(String emile) {
        User dbUser = findByEmile(emile);
        Optional<ConfirmationToken> dbToken = tokenService.getByUserEmailAndType(emile, TokenType.PASSWORD);

        dbToken.ifPresent(tokenService::deleteToken);

        try {
            ConfirmationToken token = tokenService.createTokenForUser(dbUser, TokenType.PASSWORD);
            userServiceUtil.changePasswordEmileSender(token.getConfirmationToken(), emile);
            return ServiceTextConstants.emailHasBeenSend(emile);
        } catch (SaveException e) {
            throw new SaveException(TOKEN, PASSWORD_TOKEN);
        }
    }

    public String changePassword(@Valid UserServiceChangePasswordRequest request) {
        ConfirmationToken token = tokenService.getByTokenAndType(request.getToken(), TokenType.PASSWORD);
        User dbUser = findByEmile(token.getUser().getEmail());

        if (!passwordEncoder.matches(request.getOldPassword(), dbUser.getPassword())) {
            throw new InvalidException(OLD_PASSWORD, request.getOldPassword());
        }

        dbUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        saveUser(dbUser);

        return ServiceTextConstants.hasBeenUpdated(PASSWORD);
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

        return ServiceTextConstants.hasBeenUpdated(USER);
    }

    public User findByEmile(String email) {
        return userDao.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(USER, email));
    }

    public User saveUser(User user) {
        SaveException saveException = new SaveException(USER, user.getEmail());

        User dbUser = userDao.save(user);
        if (userDao.findByEmailAndPassword(user.getEmail(), user.getPassword()).isPresent()) {
            return dbUser;
        } else {
            throw saveException;
        }
    }

    public boolean deleteUser(User user) {
        userDao.delete(user);
        if (userDao.findByEmail(user.getEmail()).isPresent()) {
            return true;
        } else {
            throw new DeleteException(USER, user.getEmail());
        }
    }
}