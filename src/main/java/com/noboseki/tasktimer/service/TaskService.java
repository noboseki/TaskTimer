package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.DeleteException;
import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.playload.TaskServiceGetTaskList;
import com.noboseki.tasktimer.repository.TaskDao;
import com.noboseki.tasktimer.service.util.TaskService.TaskServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskServiceUtil taskServiceUtil;
    private final UserService userService;
    private final TaskDao taskDao;

    public String create(User user, String taskName) {
        User dbUser = userService.findByEmile(user.getEmail());
        taskSave(Task.builder()
                .name(taskName)
                .user(dbUser).build());

        return taskName + " has been created";
    }

    public List<TaskServiceGetTaskList> getTasks(User user) {
        userService.findByEmile(user.getEmail());
        return taskDao.findAllByUser(user).stream()
                .filter(task -> !task.getArchived())
                .map(taskServiceUtil::mapToGetTaskResponse)
                .collect(Collectors.toList());
    }

    public String changeTaskComplete(User user, String taskName) {
        userService.findByEmile(user.getEmail());
        Task task = findByNameAndUser(user, taskName);
        task.setComplete(!task.getComplete());
        task = taskSave(task);

        return taskName + " complete changed to " + task.getComplete();
    }

    public String changeArchiveTask(User user, String taskName) {
        userService.findByEmile(user.getEmail());
        Task task = findByNameAndUser(user, taskName);
        task.setArchived(!task.getArchived());
        task = taskSave(task);

        return taskName + " archive changed to " + task.getArchived();
    }

    public String delete(User user, String taskName) {
        Task task = findByNameAndUser(user, taskName);
        deleteTask(task);
        return taskName + " has been deleted";
    }

    public Task findByNameAndUser(User user, String name) {
        return taskDao.findByNameAndUser(name, user).orElseThrow(() -> new ResourceNotFoundException("Task", name));
    }

    private Task taskSave(Task task) {
        SaveException saveException = new SaveException("Task", task.getName());

        try {
            Task dbTask = taskDao.save(task);
            if (taskDao.findByNameAndUser(task.getName(), task.getUser()).isPresent()) {
                return dbTask;
            } else {
                throw saveException;
            }
        } catch (SaveException e) {
            throw saveException;
        }
    }

    private boolean deleteTask(Task task) {
        taskDao.delete(task);
        if (taskDao.findById(task.getId()).isPresent()) {
            throw new DeleteException("name", task.getName());
        } else {
            return true;
        }
    }
}