package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.DeleteException;
import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.playload.TaskServiceGetTaskList;
import com.noboseki.tasktimer.repository.TaskDao;
import com.noboseki.tasktimer.security.UserDetailsImpl;
import com.noboseki.tasktimer.service.constants.ServiceTextConstants;
import com.noboseki.tasktimer.service.util.task_service.TaskServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private static final String TASK = ServiceTextConstants.getTask();
    private static final String CHANGE_COMPLETE = " complete changed to ";
    private static final String CHANGE_ARCHIVE = " archive changed to ";

    private final TaskServiceUtil taskServiceUtil;
    private final UserService userService;
    private final TaskDao taskDao;

    public String create(UserDetailsImpl userDetails, String taskName) {
        User dbUser = userService.findByEmile(userDetails.getUsername());
        taskSave(Task.builder()
                .name(taskName)
                .user(dbUser).build());

        return ServiceTextConstants.hasBeenCreate(taskName);
    }

    public List<TaskServiceGetTaskList> getTasks(UserDetailsImpl userDetails) {
        User user = userService.findByEmile(userDetails.getUsername());
        return taskDao.findAllByUser(user).stream()
                .filter(task -> !task.getArchived())
                .map(taskServiceUtil::mapToGetTaskResponse)
                .collect(Collectors.toList());
    }

    public String changeTaskComplete(UserDetailsImpl userDetails, String taskName) {
        User user = userService.findByEmile(userDetails.getUsername());
        Task task = findByNameAndUser(user, taskName);
        task.setComplete(!task.getComplete());
        task = taskSave(task);

        return taskName + CHANGE_COMPLETE + task.getComplete();
    }

    public String changeArchiveTask(UserDetailsImpl userDetails, String taskName) {
        User user = userService.findByEmile(userDetails.getUsername());
        Task task = findByNameAndUser(user, taskName);
        task.setArchived(!task.getArchived());
        task = taskSave(task);

        return taskName + CHANGE_ARCHIVE + task.getArchived();
    }

    public String delete(UserDetailsImpl userDetails, String taskName) {
        User user = userService.findByEmile(userDetails.getUsername());
        Task task = findByNameAndUser(user, taskName);
        deleteTask(task);
        return ServiceTextConstants.hasBeenDeleted(taskName);
    }

    public Task findByNameAndUser(User user, String name) {
        return taskDao.findByNameAndUser(name, user).orElseThrow(() -> new ResourceNotFoundException(TASK, name));
    }

    private Task taskSave(Task task) {
        SaveException saveException = new SaveException(TASK, task.getName());

        Task dbTask = taskDao.save(task);
        if (taskDao.findByNameAndUser(task.getName(), task.getUser()).isPresent()) {
            return dbTask;
        } else {
            throw saveException;
        }
    }

    private boolean deleteTask(Task task) {
        taskDao.delete(task);
        if (taskDao.findById(task.getId()).isPresent()) {
            throw new DeleteException(TASK, task.getName());
        } else {
            return true;
        }
    }
}