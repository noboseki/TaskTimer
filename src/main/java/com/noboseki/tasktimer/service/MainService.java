package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import com.noboseki.tasktimer.playload.ApiResponse;
import com.noboseki.tasktimer.repository.SessionDao;
import com.noboseki.tasktimer.repository.TaskDao;
import com.noboseki.tasktimer.repository.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

@RequiredArgsConstructor
public class MainService {
    protected final String TASK = "Task";
    protected final String USER = "User";

    protected final TaskDao taskDao;
    protected final UserDao userDao;
    protected final SessionDao sessionDao;

    protected User checkUserPresenceInDb(String email) {
        return userDao.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(USER, "email", email));
    }

    protected Task checkGetTask(User user, String name) {
        return taskDao.findByNameAndUser(name, user).orElseThrow(() -> new ResourceNotFoundException(TASK, "name", name));
    }

    protected ResponseEntity<ApiResponse> getApiResponse(boolean isCorrect, String message) {
        return ResponseEntity.ok().body(new ApiResponse(isCorrect, message));
    }

    protected Task getTaskByUserAndName(User user, String name) {
        User dbUser = checkUserPresenceInDb(user.getEmail());
        return checkGetTask(dbUser, name);
    }
}
