package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.Authority;
import com.noboseki.tasktimer.domain.ConfirmationToken;
import com.noboseki.tasktimer.domain.ProfileImg;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.playload.ApiResponse;
import com.noboseki.tasktimer.playload.UserServiceCreateRequest;
import com.noboseki.tasktimer.playload.UserServiceGetResponse;
import com.noboseki.tasktimer.playload.UserServiceUpdateRequest;
import com.noboseki.tasktimer.repository.*;
import com.noboseki.tasktimer.service.util.UserService.UserServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;
    private final UserServiceUtil userServiceUtil;
    private final AuthorityService authorityService;
    private final ProfileImgService profileImgService;
    private final ConfirmationTokenService tokenService;

    public ApiResponse create(@Valid UserServiceCreateRequest request) {
        if (userDao.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException();
        }
        Authority userAuthority = authorityService.findByRole("ROLE_USER");
        ProfileImg profileImg = profileImgService.findByName("Yondu");
        User user = userServiceUtil.mapToUser(request, userAuthority, profileImg);
        User dbUser = saveUser(user);

        try {
            ConfirmationToken token = tokenService.createTokenForUser(dbUser);
            userServiceUtil.activationEmileSender(token.getConfirmationToken(), request.getEmail());
            return new ApiResponse(true, "User has been created");
        } catch (Exception e) {
            throw e;
        }
    }

    public UserServiceGetResponse get(User user) {
        User dbUser = findByEmile(user.getEmail());
        return userServiceUtil.mapToResponse(dbUser);
    }

    public ApiResponse updateProfile(User user, @Valid UserServiceUpdateRequest request) {
        User dbUser = findByEmile(user.getEmail());
        ProfileImg profileImg = profileImgService.findByName(request.getProfileImgName());

        dbUser.setUsername(request.getUsername());
        dbUser.setEmail(request.getEmail());
        dbUser.setProfileImg(profileImg);

        saveUser(dbUser);

        return new ApiResponse(true, "User profile has been updated");
    }

    public User findByEmile(String email) {
        return userDao.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    public User saveUser(User user) {
        try {
            User dbUser = userDao.save(user);
            if (userDao.findByEmailAndPassword(user.getEmail(), user.getPassword()).isPresent()) {
                System.out.println("User has been created");
                return dbUser;
            } else {
                throw new SaveException("User", user);
            }
        } catch (SaveException e) {
            throw new SaveException("User", user);
        }
    }
}