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
import com.noboseki.tasktimer.service.util.UserServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@Slf4j
@Service
public class UserService extends MainService {

    private UserServiceUtil userServiceUtil;
    private ConfirmationTokenDao confirmationTokenDao;

    public UserService(TaskDao taskDao, UserDao userDao, SessionDao sessionDao,
                       ProfileImgDao profileImgDao, AuthorityDao authorityDao,
                       UserServiceUtil userServiceUtil, ConfirmationTokenDao confirmationTokenDao) {
        super(taskDao, userDao, sessionDao, profileImgDao, authorityDao);
        this.userServiceUtil = userServiceUtil;
        this.confirmationTokenDao = confirmationTokenDao;
    }

    public ApiResponse create(@Valid UserServiceCreateRequest request) {
        if (userDao.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException();
        }
        Authority userAuthority = authorityDao.findByRole("ROLE_USER").orElseThrow(() -> new ResourceNotFoundException("Authority", "name", "role"));
        ProfileImg profileImg = profileImgDao.findByName("Yondu").orElseThrow(() -> new ResourceNotFoundException("Profile img", "name", "standard"));
        User user = userServiceUtil.mapToUser(request, userAuthority, profileImg);
        ApiResponse response = new ApiResponse(checkUserSave(user), "User has been created");
        ConfirmationToken token = confirmationTokenDao.save(new ConfirmationToken(userDao.findByEmail(request.getEmail()).get()));
        userServiceUtil.activationEmileSender(token.getConfirmationToken(), request.getEmail());
        return response;
    }

    public UserServiceGetResponse get(User user) {
        User dbUser = checkUserPresenceInDb(user.getEmail());
        return userServiceUtil.mapToResponse(dbUser);
    }

    public ApiResponse updateProfile(User user, @Valid UserServiceUpdateRequest request) {
        User dbUser = checkUserPresenceInDb(user.getEmail());
        ProfileImg profileImg = profileImgDao.findByName(request.getProfileImgName()).orElseThrow(() -> new ResourceNotFoundException("Profile Img", "profile img", profileImgDao));

        dbUser.setUsername(request.getUsername());
        dbUser.setEmail(request.getEmail());
        dbUser.setProfileImg(profileImg);

        return new ApiResponse(checkUserSave(dbUser), "User profile has been updated");
    }

    private boolean checkUserSave(User user) {
        try {
            userDao.save(user);
            if (userDao.findByEmailAndPassword(user.getEmail(), user.getPassword()).isPresent()) {
                System.out.println("User has been created");
                return true;
            } else {
                throw new SaveException("User", user);
            }
        } catch (SaveException e) {
            throw new SaveException("User", user);
        }
    }
}