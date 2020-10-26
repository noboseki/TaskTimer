/*
package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.exeption.DeleteException;
import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.playload.ApiResponse;
import com.noboseki.tasktimer.repository.TaskDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@Service
public class TaskService {
    private final String TASK_HAS_BEEN = "Task has been ";

    private TaskDao dao;

    public TaskService(TaskDao dao) {
        this.dao = dao;
    }

    public ResponseEntity<ApiResponse> create(@Valid Task.TaskDto dto) {
        checkSaveTask(dto);
        return getApiResponse(true, "created");
    }

    public ResponseEntity<Task.TaskDto> get(UUID taskID) {
        Task task = checkGetTask(taskID);
        log.info(TASK_HAS_BEEN + "taken");
        return ResponseEntity.ok(EntityMapper.mapToDto(task));
    }

    public ResponseEntity<ApiResponse> update(@Valid Task.TaskDto dto) {
        checkGetTask(dto.getPrivateID());
        checkSaveTask(dto);
        return getApiResponse(true, "updated");
    }

    public ResponseEntity<ApiResponse> delete(UUID taskId) {
        checkGetTask(taskId);
        boolean isDeleted = checkDeleteTask(taskId);
        return getApiResponse(isDeleted, "deleted");
    }

    private ResponseEntity<ApiResponse> getApiResponse(boolean isCorrect, String methodName) {
        return ResponseEntity.ok().body(new ApiResponse(isCorrect, TASK_HAS_BEEN + methodName));
    }

    private Task checkGetTask(UUID taskId) {
        return dao.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task: ", "id", taskId));
    }

    private boolean checkDeleteTask(UUID taskId) {
        try {
            dao.deleteById(taskId);
            log.info(TASK_HAS_BEEN + "deleted");
            return true;
        } catch (Exception e) {
            log.error("Delete error", e);
            throw new DeleteException("Task", taskId.toString());
        }
    }

    private boolean checkSaveTask(Task.TaskDto dto){
        try {
            dao.save(EntityMapper.mapToEntity(dto));
            log.info(TASK_HAS_BEEN + "saved");
            return true;
        } catch (Exception e) {
            log.error("Task save error", e);
            throw new SaveException("Task", dto);
        }
    }
}*/
