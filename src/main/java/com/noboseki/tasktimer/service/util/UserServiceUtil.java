package com.noboseki.tasktimer.service.util;

import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.playload.UserServiceGetResponse;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class UserServiceUtil {

    public UserServiceGetResponse mapToResponse(User user) {
        return UserServiceGetResponse.builder()
                .publicId(user.getPublicId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profileImg(user.getProfileImg()).build();
    }
}
