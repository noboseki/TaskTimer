package com.noboseki.tasktimer.util;

import com.noboseki.tasktimer.domain.User;

public class EntityMapper {

    public static User mapToEntity (User.UserDto dto) {
        return User.builder()
                .privateID(dto.getPrivateID())
                .publicId(dto.getPublicId())
                .email(dto.getEmail())
                .imageUrl(dto.getImageUrl())
                .emailVerified(dto.getEmailVerified())
                .password(dto.getPassword()).build();
    }

    public static User.UserDto mapToDto(User user) {
        return User.UserDto.builder()
                .privateID(user.getPrivateID())
                .publicId(user.getPublicId())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .emailVerified(user.getEmailVerified())
                .password(user.getPassword()).build();
    }
}
