package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.DeleteException;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.playload.ApiResponse;
import com.noboseki.tasktimer.playload.TaskGetResponse;
import com.noboseki.tasktimer.repository.ProfileImgDao;
import com.noboseki.tasktimer.repository.SessionDao;
import com.noboseki.tasktimer.repository.TaskDao;
import com.noboseki.tasktimer.repository.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TaskService extends MainService {
    private final String TASK_HAS_BEEN = "Task has been ";
    private final String THE_SAME_NAME = "Duplicate task name";

    public TaskService(TaskDao taskDao, UserDao userDao, SessionDao sessionDao, ProfileImgDao profileImgDao) {
        super(taskDao, userDao, sessionDao, profileImgDao);
    }

    public ResponseEntity<ApiResponse> create(User user, @Min(6) @Max(40) String name) {
        User dbUser = checkUserPresenceInDb(user.getEmail());

        if (taskDao.findByNameAndUser(name, dbUser).isPresent()) {
            return getApiResponse(false, THE_SAME_NAME);
        }

        Task task = Task.builder()
                .name(name)
                .user(dbUser).build();

        return getApiResponse(checkSaveTask(task), TASK_HAS_BEEN + "created");
    }

    public ResponseEntity<TaskGetResponse> get(User user, String name) {
        Task task = getTaskByUserAndName(user, name);
        return ResponseEntity.ok(mapToGetResponse(task));
    }

    public ResponseEntity<List<TaskGetResponse>> getAll(User user) {
        User dbUser = checkUserPresenceInDb(user.getEmail());
        List<TaskGetResponse> tasks = taskDao.findAllByUser(dbUser).stream()
                .map(this::mapToGetResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(tasks);
    }

    public ResponseEntity<ApiResponse> updateName(User user, String oldName, @Min(6) @Max(40) String newName) {
        User dbUser = checkUserPresenceInDb(user.getEmail());
        if (taskDao.findByNameAndUser(newName, dbUser).isPresent()) {
            return getApiResponse(false, THE_SAME_NAME);
        }
        Task task = checkTaskPresenceInDbForUser(dbUser, oldName);
        task.setName(newName);
        return getApiResponse(checkSaveTask(task), "Task name has been updated");
    }

    public ResponseEntity<ApiResponse> updateIsComplete(User user, String name) {
        Task task = getTaskByUserAndName(user, name);
        task.setComplete(!task.getComplete());
        return getApiResponse(checkSaveTask(task), "Task status has been updated");
    }

    public ResponseEntity<ApiResponse> delete(User user, String name) {
        Task task = getTaskByUserAndName(user, name);
        return getApiResponse(checkDeleteTask(task), TASK_HAS_BEEN + "deleted");
    }

    private boolean checkDeleteTask(Task task) {
        try {
            taskDao.deleteById(task.getId());
            log.info(TASK_HAS_BEEN + "deleted");
            return true;
        } catch (Exception e) {
            log.error("Task delete error", e);
            throw new DeleteException(TASK, task.getName());
        }
    }

    private boolean checkSaveTask(Task task) {
        try {
            taskDao.save(task);
            log.info(TASK_HAS_BEEN + "saved");
            return true;
        } catch (Exception e) {
            log.error("Task save error", e);
            throw new SaveException(TASK, task);
        }
    }

    private TaskGetResponse mapToGetResponse(Task task) {
        return new TaskGetResponse(task.getName(), task.getComplete());
    }
}
