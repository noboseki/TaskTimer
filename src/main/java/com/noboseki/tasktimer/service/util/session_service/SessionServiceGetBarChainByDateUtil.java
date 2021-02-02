package com.noboseki.tasktimer.service.util.session_service;

import com.noboseki.tasktimer.domain.Session;
import com.noboseki.tasktimer.playload.SessionServiceChainByDateResponse;
import com.noboseki.tasktimer.playload.TaskDataForSessionChain;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@NoArgsConstructor
public class SessionServiceGetBarChainByDateUtil {

    public SessionServiceChainByDateResponse fillBarChainByDate(List<Session> sessionsBetweenDateForUser, LocalDate fromDate, LocalDate toDate) {
        SessionServiceChainByDateResponse response = SessionServiceChainByDateResponse.builder()
                .dataList(new ArrayList<>())
                .dateLabel(createDateLabel(fromDate, toDate)).build();

        for (String taskName : getTaskNamesFromList(sessionsBetweenDateForUser)) {
            TaskDataForSessionChain data = new TaskDataForSessionChain(new ArrayList<>(), taskName);
            LocalDate tmpDate = fromDate;
            while (tmpDate.isBefore(toDate.plusDays(1))) {
                List<Time> sessions = extractSessionsTimeByDateAndTaskName(sessionsBetweenDateForUser, taskName, tmpDate);
                data.getData().add(listToLongTime(sessions));
                tmpDate = tmpDate.plusDays(1);
            }
            response.getDataList().add(data);
        }
        return response;
    }

    public List<String> createDateLabel(LocalDate fromDate, LocalDate toDate) {
        Stream<LocalDate> localDates = fromDate.datesUntil(toDate.plusDays(1));
        return localDates
                .map(LocalDate::toString)
                .collect(Collectors.toList());
    }

    public Set<String> getTaskNamesFromList(List<Session> list) {
        return list.stream().map(session -> session.getTask().getName())
                .collect(Collectors.toSet());
    }

    public List<Time> extractSessionsTimeByDateAndTaskName(List<Session> startList, String taskName, LocalDate date) {
        return startList.stream()
                .filter(session -> session.getDate().toLocalDate().isEqual(date) && session.getTask().getName().equals(taskName))
                .map(Session::getTime)
                .collect(Collectors.toList());
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
