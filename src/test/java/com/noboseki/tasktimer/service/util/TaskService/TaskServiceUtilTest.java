package com.noboseki.tasktimer.service.util.TaskService;

import com.noboseki.tasktimer.domain.Session;
import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.playload.TaskServiceGetTaskList;
import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TaskServiceUtilTest {
    TaskServiceUtil util = new TaskServiceUtil();

    @Test
    void mapToGetTaskResponse() {
        Session sessionA = Session.builder()
                .time(Time.valueOf("01:20:00")).build();

        Session sessionB = Session.builder()
                .time(Time.valueOf("02:45:00")).build();

        Task task = Task.builder()
                .name("task name")
                .sessions(List.of(sessionA, sessionB))
                .build();

        TaskServiceGetTaskList response = util.mapToGetTaskResponse(task);

        assertEquals(response.getTaskName(), "task name");
        assertEquals(response.getTime(), "04:05");
        assertEquals(response.getSessionsNumber(), 2);
        assertFalse(response.isComplete());
    }

    @Test
    void mapToGetTaskResponseEmpty() {
        Task task = Task.builder()
                .name("task name")
                .build();

        TaskServiceGetTaskList response = util.mapToGetTaskResponse(task);
        assertEquals(response.getTaskName(), "task name");
        assertEquals(response.getTime(), "00:00");
        assertEquals(response.getSessionsNumber(), 0);
        assertFalse(response.isComplete());
    }
}