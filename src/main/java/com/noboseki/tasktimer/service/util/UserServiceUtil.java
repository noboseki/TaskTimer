package com.noboseki.tasktimer.service.util;

import com.noboseki.tasktimer.domain.Authority;
import com.noboseki.tasktimer.domain.ProfileImg;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.playload.UserServiceCreateRequest;
import com.noboseki.tasktimer.playload.UserServiceGetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserServiceUtil {

    private final PasswordEncoder passwordEncoder;

    public UserServiceGetResponse mapToResponse(User user) {
        return UserServiceGetResponse.builder()
                .publicId(user.getPublicId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profileImg(user.getProfileImg()).build();
    }

    public User mapToUser(UserServiceCreateRequest request, Authority authority, ProfileImg profileImg) {
        return User.builder()
                .email(request.getEmail())
                .username(request.getUserName())
                .password(passwordEncoder.encode(request.getPassword()))
                .authority(authority)
                .profileImg(profileImg).build();
    }

}
