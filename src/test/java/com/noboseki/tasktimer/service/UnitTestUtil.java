package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.Session;
import com.noboseki.tasktimer.domain.Task;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor()
public class UnitTestUtil {

    public Session createSession(String date, String time, String taskName) {
        Task task = new Task();
        task.setName(taskName);

        return Session.builder()
                .date(Date.valueOf(date))
                .time(Time.valueOf(time))
                .task(task).build();
    }

    public List<Session> getDefaultSessionList() {
        List<Session> sessions = new ArrayList<>();
        sessions.add(createSession("2020-10-20", "01:15:00", "task name A"));
        sessions.add(createSession("2020-10-20", "02:31:00", "task name B"));
        sessions.add(createSession("2020-10-20", "04:04:00", "task name A"));
        sessions.add(createSession("2020-11-01", "01:36:00", "task name C"));
        sessions.add(createSession("2020-12-04", "01:49:00", "task name A"));

        return sessions;
    }
}
