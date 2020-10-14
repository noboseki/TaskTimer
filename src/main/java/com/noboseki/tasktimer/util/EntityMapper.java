package com.noboseki.tasktimer.util;

import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.domain.Session;

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

    public static Task mapToEntity(Task.TaskDto dto) {
        return Task.builder()
                .privateID(dto.getPrivateID())
                .name(dto.getName())
                .complete(dto.getComplete()).build();
    }

    public static Session mapToEntity(Session.SessionDto dto) {
        return Session.builder()
                .privateID(dto.getPrivateID())
                .date(dto.getDate())
                .time(dto.getTime()).build();
    }

    public static User.UserDto mapToDto(User user) {
        return User.UserDto.builder()
                .publicId(user.getPublicId())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .build();
    }

    public static Task.TaskDto mapToDto(Task task) {
        return Task.TaskDto.builder()
                .privateID(task.getPrivateID())
                .name(task.getName())
                .complete(task.getComplete()).build();
    }

    public static Session.SessionDto mapToDto(Session session) {
        return Session.SessionDto.builder()
                .privateID(session.getPrivateID())
                .date(session.getDate())
                .time(session.getTime()).build();
    }

}
