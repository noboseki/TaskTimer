package com.noboseki.tasktimer.service.util.task_service;

import com.noboseki.tasktimer.domain.Session;
import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.playload.TaskServiceGetTaskList;
import com.noboseki.tasktimer.service.util.ServiceUtil;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Calendar;


@Component
@NoArgsConstructor
public class TaskServiceUtil {
    private ServiceUtil serviceUtil = new ServiceUtil();


    public TaskServiceGetTaskList mapToGetTaskResponse(Task task) {
        Calendar calendar = Calendar.getInstance();
        int hours = 0;
        int minutes = 0;

        for (Session session : task.getSessions()) {
            calendar.setTime(session.getTime());
            hours += calendar.get(Calendar.HOUR);
            minutes += calendar.get(Calendar.MINUTE);
        }

        hours += minutes / 60;
        minutes = minutes % 60;

        return new TaskServiceGetTaskList(task.getName(),
                serviceUtil.mapTimeToString(hours, minutes),
                task.getSessions().size(),
                task.getComplete());
    }
}
