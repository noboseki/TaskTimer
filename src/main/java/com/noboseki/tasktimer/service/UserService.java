package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.DeleteException;
import com.noboseki.tasktimer.exeption.ForbiddenException;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.playload.ApiResponse;
import com.noboseki.tasktimer.playload.UserCreateRequest;
import com.noboseki.tasktimer.playload.UserGetResponse;
import com.noboseki.tasktimer.repository.TaskDao;
import com.noboseki.tasktimer.repository.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService  extends MainService{
    private final String USER_HAS_BEEN = "User has been ";
    private final String ADMIN_ROLE = "ROLE_ADMIN";

    private PasswordEncoder passwordEncoder;

    public UserService(TaskDao taskDao, UserDao userDao, PasswordEncoder passwordEncoder) {
        super(taskDao, userDao);
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<ApiResponse> create(UserCreateRequest request) {
        User user = mapToUser(request);
        return getApiResponse(checkSaveUser(user), USER_HAS_BEEN + "created");
    }

    public ResponseEntity<UserGetResponse> get(User user) {
        User dbUser = checkGetUser(user.getEmail());
        return ResponseEntity.ok(mapToResponse(dbUser));
    }

    public ResponseEntity<UserGetResponse> getByEmail(String email) {
        User user = checkGetUser(email);
        checkAdminAuthority(user.getAuthorities());
        return ResponseEntity.ok(mapToResponse(user));
    }

    public ResponseEntity<ApiResponse> updateImageUrl(String url, User user) {
        User updateUser = checkGetUser(user.getEmail());
        updateUser.setImageUrl(url);
        return getApiResponse(checkSaveUser(updateUser),"Image has been changed");
    }

    public ResponseEntity<ApiResponse> updateName(String name, User user) {
        User updateUser = checkGetUser(user.getEmail());
        updateUser.setUsername(name);
        return getApiResponse(checkSaveUser(updateUser), "Username has been changed");
    }

    public ResponseEntity<ApiResponse> delete(String email) {
        User deleteUser = checkGetUser(email);
        checkAdminAuthority(deleteUser.getAuthorities());
        return getApiResponse(checkDeleteUser(deleteUser), USER_HAS_BEEN + "deleted");
    }

    private boolean checkAdminAuthority(Set<GrantedAuthority> authorities) {
        Set<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(s -> s.equals(ADMIN_ROLE))
                .collect(Collectors.toSet());

        if (roles.size() > 0) {
            throw new ForbiddenException(USER, "admin", "deleted");
        } else {
            return false;
        }
    }

    private boolean checkDeleteUser(User user) {
        try {
            userDao.delete(user);
            log.info(USER_HAS_BEEN + "deleted");
            return true;
        } catch (Exception e) {
            log.error("Delete error", e);
            throw new DeleteException(USER, user);
        }
    }

    private boolean checkSaveUser(User user){
        try {
            userDao.save(user);
            log.info(USER_HAS_BEEN + "saved");
            return true;
        } catch (Exception e) {
            log.error("User save error", e);
            throw new SaveException(USER, user.getEmail());
        }
    }

    private UserGetResponse mapToResponse(User user) {
        return UserGetResponse.builder()
                .email(user.getEmail())
                .publicId(user.getPublicId())
                .username(user.getUsername())
                .imageUrl(user.getImageUrl()).build();
    }

    private User mapToUser(UserCreateRequest request) {
        return User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .username(request.getUserName())
                .imageUrl(request.getImageUrl()).build();
    }
}