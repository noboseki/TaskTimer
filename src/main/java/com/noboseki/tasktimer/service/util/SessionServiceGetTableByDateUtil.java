package com.noboseki.tasktimer.service.util;

import com.noboseki.tasktimer.domain.Session;
import com.noboseki.tasktimer.playload.GetTableByDateResponse;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor
public class SessionServiceGetTableByDateUtil {

    public List<Session> extractSessionsByDateAndRemove(List<Session> dbList, LocalDate localFrom) {
        List<Session> returnList = new ArrayList<>();

        for (Session session : dbList) {
            boolean theSameYear = session.getDate().toLocalDate().getYear() == localFrom.getYear();
            boolean theSameMonth = session.getDate().toLocalDate().getMonth() == localFrom.getMonth();
            boolean theSameDay = session.getDate().toLocalDate().getDayOfMonth() == localFrom.getDayOfMonth();
            if (theSameYear && theSameMonth && theSameDay) {
                returnList.add(session);
            }
        }

        return returnList;
    }

    public void fillingResponseListByDate(List<Session> tempSessionsByDateList, List<GetTableByDateResponse> responseList, LocalDate date) {
        if (tempSessionsByDateList.isEmpty()) {
            responseList.add(new GetTableByDateResponse(
                    date,
                    "00:00:00",
                    0));
        } else {
            GetTableByDateResponse response = mapToGetBetweenDateSessionResponse(tempSessionsByDateList, date);
            responseList.add(response);
        }
    }

    private GetTableByDateResponse mapToGetBetweenDateSessionResponse(List<Session> list, LocalDate date) {
        int session = list.size();
        int hours = 0;
        int minutes = 0;

        for (Session s : list) {
            hours += s.getTime().getHours();
            minutes += s.getTime().getMinutes();
        }

        hours += minutes / 60;
        minutes = minutes % 60;

        return new GetTableByDateResponse(date, mapToTimeString(hours, minutes), session);
    }

    private String mapToTimeString(int hours, int minutes) {
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
