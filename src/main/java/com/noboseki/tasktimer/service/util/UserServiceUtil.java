package com.noboseki.tasktimer.service.util;

import com.noboseki.tasktimer.domain.Session;
import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.playload.UserServiceGetResponse;
import com.noboseki.tasktimer.playload.UserServiceGetTaskList;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class UserServiceUtil {

    private  ServiceUtil serviceUtil = new ServiceUtil();

    public UserServiceGetResponse mapToResponse(User user) {
        return UserServiceGetResponse.builder()
                .publicId(user.getPublicId())
                .username(user.getUsername())
                .email(user.getEmail())
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

        return new UserServiceGetTaskList(task.getName(), serviceUtil.mapTimeToString(hours, minutes), task.getComplete());
    }
}
