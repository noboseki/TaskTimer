package com.noboseki.tasktimer.service.util;

import com.noboseki.tasktimer.domain.Session;
import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.playload.TaskServiceGetTaskList;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@NoArgsConstructor
public class TaskServiceUtil {
    private ServiceUtil serviceUtil = new ServiceUtil();


    public TaskServiceGetTaskList mapToGetTaskResponse(Task task) {
        int hours = 0;
        int minutes = 0;

        for (Session session : task.getSessions()) {
            hours += session.getTime().getHours();
            minutes += session.getTime().getMinutes();
        }

        hours += minutes / 60;
        minutes = minutes % 60;

        return new TaskServiceGetTaskList(task.getName(),
                serviceUtil.mapTimeToString(hours, minutes),
                task.getSessions().size(),
                task.getComplete());
    }
}
