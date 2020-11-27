package com.noboseki.tasktimer.service.util;

import com.noboseki.tasktimer.domain.Session;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@NoArgsConstructor
public class SessionServiceGetBarChainByDateUtil {

    public List<String> createDateLabel(String fromDate, String toDate) {
        List<String> dataLabel = new ArrayList<>();
        LocalDate localTo = LocalDate.parse(toDate);

        for (LocalDate localFrom = LocalDate.parse(fromDate); localFrom.isBefore(localTo.plusDays(1)); localFrom = localFrom.plusDays(1)) {
            dataLabel.add(localFrom.toString());
        }

        return dataLabel;
    }

    public Set<String> getTaskNamesFromList(List<Session> list) {
        Set<String> taskNameSet = new HashSet<>();

        for (Session s : list) {
            taskNameSet.add(s.getTask().getName());
        }

        return taskNameSet;
    }

    public List<Time> extractSessionsTimeByDateAndTaskName(List<Session> startList, String taskName, LocalDate date) {
        List<Time> extractedTimeList = new ArrayList<>();
        for (Session session: startList) {

            boolean theSameYear = session.getDate().toLocalDate().getYear() == date.getYear();
            boolean theSameMonth = session.getDate().toLocalDate().getMonth() == date.getMonth();
            boolean theSameDay = session.getDate().toLocalDate().getDayOfMonth() == date.getDayOfMonth();

            if (theSameYear && theSameMonth &&  theSameDay && session.getTask().getName().equals(taskName)) {
                extractedTimeList.add(session.getTime());
            }
        }
        return extractedTimeList;
    }

    public float listToLongTime(List<Time> times) {
        int hours = 0;
        int minutes = 0;

        for (Time time : times) {
            hours += time.getHours();
            minutes += time.getMinutes();
        }

        hours += minutes / 60;
        minutes = minutes % 60;

        return hours + Math.round(((float) minutes / 60) * 100) / 100f;
    }
}
