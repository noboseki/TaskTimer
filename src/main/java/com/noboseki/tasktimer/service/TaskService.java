package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.DeleteException;
import com.noboseki.tasktimer.playload.ApiResponse;
import com.noboseki.tasktimer.playload.TaskServiceGetTaskList;
import com.noboseki.tasktimer.repository.ProfileImgDao;
import com.noboseki.tasktimer.repository.SessionDao;
import com.noboseki.tasktimer.repository.TaskDao;
import com.noboseki.tasktimer.repository.UserDao;
import com.noboseki.tasktimer.service.util.TaskServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TaskService extends MainService {

    private final TaskServiceUtil taskServiceUtil;

    public TaskService(TaskDao taskDao, UserDao userDao,
                       SessionDao sessionDao, ProfileImgDao profileImgDao,
                       TaskServiceUtil serviceUtil) {
        super(taskDao, userDao, sessionDao, profileImgDao);
        this.taskServiceUtil = serviceUtil;
    }

    public List<TaskServiceGetTaskList> getTasks(User user) {
        checkUserPresenceInDb(user.getEmail());
        return taskDao.findAllByUser(user).stream()
                .filter(task -> task.getArchived() == false)
                .map(taskServiceUtil::mapToGetTaskResponse)
                .collect(Collectors.toList());
    }

    public ApiResponse changeTaskComplete(User user, String taskName) {
        checkUserPresenceInDb(user.getEmail());
        Task task = checkTaskPresenceInDbForUser(user, taskName);
        task.setComplete(!task.getComplete());
        task = taskDao.save(task);

        return new ApiResponse(true, taskName + "complete changed to" + task.getComplete());
    }

    public ApiResponse changeArchiveTask(User user, String taskName) {
        checkUserPresenceInDb(user.getEmail());
        Task task = checkTaskPresenceInDbForUser(user, taskName);
        task.setArchived(!task.getArchived());
        task = taskDao.save(task);

        return new ApiResponse(true, taskName + "archive changed to" + task.getComplete());
    }

    public ApiResponse delete(User user, String taskName){
        Task task = checkTaskPresenceInDbForUser(user,taskName);
        return new ApiResponse(checkTaskDelete(task),taskName + "has been deleted");
    }

    private boolean checkTaskDelete(Task task) {
        try {
            taskDao.delete(task);
            if (taskDao.findById(task.getId()).isPresent()){
                throw new DeleteException("Task",task.getId());
            } else {
                return true;
            }
        } catch (DeleteException e) {
            throw new DeleteException("Task",task.getId());
        }
    }
}