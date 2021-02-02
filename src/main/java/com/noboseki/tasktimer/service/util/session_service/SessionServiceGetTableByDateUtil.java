package com.noboseki.tasktimer.service.util.session_service;

import com.noboseki.tasktimer.domain.Session;
import com.noboseki.tasktimer.playload.SessionServiceTableByDateResponse;
import com.noboseki.tasktimer.service.constants.ServiceUtilTextConstants;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class SessionServiceGetTableByDateUtil {
    private static final String ZERO_TIME = ServiceUtilTextConstants.getZeroTime();

    public List<SessionServiceTableByDateResponse> fillEmptyResponseList(LocalDate fromDate, LocalDate toDate) {
        List<SessionServiceTableByDateResponse> responseList = new ArrayList<>();

        while (fromDate.isBefore(toDate.plusDays(1))) {
            responseList.add(new SessionServiceTableByDateResponse(
                    fromDate,
                    ZERO_TIME,
                    0));
            fromDate = fromDate.plusDays(1);
        }
        return responseList;
    }

    public List<SessionServiceTableByDateResponse> fillResponseList(List<Session> sessionsListBetweenDate, LocalDate fromDate, LocalDate toDate) {
        List<SessionServiceTableByDateResponse> response = new ArrayList<>();

        while (fromDate.isBefore(toDate) || fromDate.equals(toDate)) {
            List<Session> tempSessionsByDateList = extractSessionsByDateAndRemove(sessionsListBetweenDate, fromDate);
            fillingResponseListByDate(tempSessionsByDateList, response, fromDate);
            fromDate = fromDate.plusDays(1);
        }
        return response;
    }

    public List<Session> extractSessionsByDateAndRemove(List<Session> dbList, LocalDate localFrom) {
        List<Session> returnList = dbList.stream()
                .filter(session -> session.getDate().toLocalDate().isEqual(localFrom))
                .collect(Collectors.toList());

        dbList.removeAll(returnList);
        return returnList;
    }

    public void fillingResponseListByDate(List<Session> tempSessionsByDateList, List<SessionServiceTableByDateResponse> responseList, LocalDate date) {
        if (tempSessionsByDateList.isEmpty()) {
            responseList.add(new SessionServiceTableByDateResponse(
                    date,
                    ZERO_TIME,
                    0));
        } else {
            SessionServiceTableByDateResponse response = mapToGetBetweenDateSessionResponse(tempSessionsByDateList, date);
            responseList.add(response);
        }
    }

    private SessionServiceTableByDateResponse mapToGetBetweenDateSessionResponse(List<Session> list, LocalDate date) {
        int session = list.size();
        int hours = 0;
        int minutes = 0;

        for (Session s : list) {
            hours += s.getTime().getHours();
            minutes += s.getTime().getMinutes();
        }

        hours += minutes / 60;
        minutes = minutes % 60;

        return new SessionServiceTableByDateResponse(date, mapTimeToString(hours, minutes), session);
    }

    //    !!!!!!!!
    private String mapTimeToString(int hours, int minutes) {
        String time = "";

        if (hours >= 10) {
            time += String.valueOf(hours);
        } else {
            time += "0" + hours;
        }

        if (minutes >= 10) {
            time += ":" + minutes;
        } else {
            time += ":0" + minutes;
        }

        return time;
    }
}
