package com.noboseki.tasktimer.util;

import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.domain.WorkTime;

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

    public static WorkTime mapToEntity(WorkTime.WorkTimeDto dto) {
        return WorkTime.builder()
                .privateID(dto.getPrivateID())
                .date(dto.getDate())
                .time(dto.getTime()).build();
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

    public static Task.TaskDto mapToDto(Task task) {
        return Task.TaskDto.builder()
                .privateID(task.getPrivateID())
                .name(task.getName())
                .complete(task.getComplete()).build();
    }

    public static WorkTime.WorkTimeDto mapToDto(WorkTime workTime) {
        return WorkTime.WorkTimeDto.builder()
                .privateID(workTime.getPrivateID())
                .date(workTime.getDate())
                .time(workTime.getTime()).build();
    }

}
