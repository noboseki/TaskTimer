package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.ProfileImg;
import com.noboseki.tasktimer.domain.Session;
import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.DeleteException;
import com.noboseki.tasktimer.exeption.ForbiddenException;
import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.playload.ApiResponse;
import com.noboseki.tasktimer.playload.UserServiceGetResponse;
import com.noboseki.tasktimer.playload.UserServiceGetTaskList;
import com.noboseki.tasktimer.playload.UserServiceUpdateRequest;
import com.noboseki.tasktimer.repository.ProfileImgDao;
import com.noboseki.tasktimer.repository.SessionDao;
import com.noboseki.tasktimer.repository.TaskDao;
import com.noboseki.tasktimer.repository.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.sql.Time;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService extends MainService {
    private final String USER_HAS_BEEN = "User has been ";
    private final String ADMIN_ROLE = "ROLE_ADMIN";

    private PasswordEncoder passwordEncoder;

    public UserService(TaskDao taskDao, UserDao userDao, SessionDao sessionDao, ProfileImgDao profileImgDao, PasswordEncoder passwordEncoder) {
        super(taskDao, userDao, sessionDao, profileImgDao);
        this.passwordEncoder = passwordEncoder;
    }

    //    public ResponseEntity<ApiResponse> create(UserCreateRequest request) {
//        User user = mapToUser(request);
//        return getApiResponse(checkSaveUser(user), USER_HAS_BEEN + "created");
//    }

    public UserServiceGetResponse get(User user) {
        User dbUser = checkUserPresenceInDb(user.getEmail());
        return mapToResponse(dbUser);
    }

    public ApiResponse updateProfile(User user, @Valid UserServiceUpdateRequest request) {
        User dbUser = checkUserPresenceInDb(user.getEmail());
        ProfileImg profileImg = profileImgDao.findByName(request.getProfileImgName()).orElseThrow(() -> new ResourceNotFoundException("Profile Img", "profile img", profileImgDao));

        dbUser.setUsername(request.getUsername());
        dbUser.setEmail(request.getEmail());
        dbUser.setProfileImg(profileImg);

        return new ApiResponse(checkSaveUser(dbUser), "User profile has been updated");
    }

    //    public ResponseEntity<UserGetResponse> getByEmail(String email) {
//        User user = checkUserPresenceInDb(email);
//        checkAdminAuthority(user.getAuthorities());
//        return ResponseEntity.ok(mapToResponse(user));
//    }
//
//    public ResponseEntity<ApiResponse> updateImageUrl(String url, User user) {
//        User updateUser = checkUserPresenceInDb(user.getEmail());
//        updateUser.setImageUrl(url);
//        return getApiResponse(checkSaveUser(updateUser),"Image has been changed");
//    }
//
//    public ResponseEntity<ApiResponse> updateName(String name, User user) {
//        User updateUser = checkUserPresenceInDb(user.getEmail());
//        updateUser.setUsername(name);
//        return getApiResponse(checkSaveUser(updateUser), "Username has been changed");
//    }
//
//    public ResponseEntity<ApiResponse> delete(String email) {
//        User deleteUser = checkUserPresenceInDb(email);
//        checkAdminAuthority(deleteUser.getAuthorities());
//        return getApiResponse(checkDeleteUser(deleteUser), USER_HAS_BEEN + "deleted");
//    }
//
//    private boolean checkAdminAuthority(Set<GrantedAuthority> authorities) {
//        Set<String> roles = authorities.stream()
//                .map(GrantedAuthority::getAuthority)
//                .filter(s -> s.equals(ADMIN_ROLE))
//                .collect(Collectors.toSet());
//
//        if (roles.size() > 0) {
//            throw new ForbiddenException(USER, "admin", "deleted");
//        } else {
//            return false;
//        }
//    }
//
//    private boolean checkDeleteUser(User user) {
//        try {
//            userDao.delete(user);
//            log.info(USER_HAS_BEEN + "deleted");
//            return true;
//        } catch (Exception e) {
//            log.error("Delete error", e);
//            throw new DeleteException(USER, user);
//        }
//    }
//
    private boolean checkSaveUser(User user) {
        try {
            userDao.save(user);
            log.info(USER_HAS_BEEN + "saved");
            return true;
        } catch (Exception e) {
            log.error("User save error", e);
            throw new SaveException(USER, user.getEmail());
        }
    }

    private UserServiceGetResponse mapToResponse(User user) {
        return UserServiceGetResponse.builder()
                .publicId(user.getPublicId())
                .username(user.getUsername())
                .email(user.getEmail())
                .sex(user.getSex())
                .taskList(mapToGetTaskList(user.getTasks()))
                .profileImg(user.getProfileImg()).build();
    }

    private List<UserServiceGetTaskList> mapToGetTaskList(Set<Task> tasks) {
        return tasks.stream()
                .map(this::mapToGetTaskResponse)
                .collect(Collectors.toList());
    }

    private UserServiceGetTaskList mapToGetTaskResponse(Task task) {
        int hours = 0;
        int minutes = 0;

        for (Session session : task.getSessions()) {
            hours += session.getTime().getHours();
            minutes += session.getTime().getMinutes();
        }

        hours += minutes / 60;
        minutes = minutes % 60;

        return new UserServiceGetTaskList(task.getName(), mapTimeToString(hours, minutes), task.getComplete());
    }

    //!!!!!!!!!!!!
    private String mapTimeToString(int hours, int minutes) {
        String time = "";

        if (hours >= 10) {
            time += String.valueOf(hours);
        } else {
            time += "0" + hours;
        }

        if (minutes >= 10) {
            time += ":" + minutes;
        } else {
            time += ":0" + minutes;
        }

        return time;
    }
//
//    private User mapToUser(UserCreateRequest request) {
//        return User.builder()
//                .email(request.getEmail())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .username(request.getUserName())
//                .imageUrl(request.getImageUrl()).build();
//    }
}