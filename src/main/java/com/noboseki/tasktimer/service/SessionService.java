package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.Session;
import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.DateTimeException;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.playload.ApiResponse;
import com.noboseki.tasktimer.playload.CreateSessionRequest;
import com.noboseki.tasktimer.playload.GetBetweenDateSessionResponse;
import com.noboseki.tasktimer.playload.GetByTaskSessionResponse;
import com.noboseki.tasktimer.repository.SessionDao;
import com.noboseki.tasktimer.repository.TaskDao;
import com.noboseki.tasktimer.repository.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SessionService extends MainService {
    private final String SESSION_TIME_HAS_BEEN = "Session has been ";

    public SessionService(TaskDao taskDao, UserDao userDao, SessionDao sessionDao) {
        super(taskDao, userDao, sessionDao);
    }

    public ResponseEntity<ApiResponse> create(User user, String taskName, CreateSessionRequest request) {
        Task task = getTaskByUserAndName(user, taskName);
        Date date = checkDateFromString(request.getDate());
        Time time = checkTimeFromString(request.getTime());

        Session session = Session.builder()
                .date(date)
                .time(time)
                .task(task).build();

        return getApiResponse(checkSaveSession(session), SESSION_TIME_HAS_BEEN + "created");
    }

    public List<GetBetweenDateSessionResponse> getBetweenDate(User user, String fromDate, String toDate) {
        checkGetUser(user.getEmail());
        DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<Session> dbList;

        LocalDate localTo = LocalDate.parse(toDate);
        List<GetBetweenDateSessionResponse> responseList = new ArrayList<>();

        for (LocalDate localFrom = LocalDate.parse(fromDate); localFrom.isBefore(localTo.plusDays(1));localFrom.plusDays(1)) {
            dbList = sessionDao.findAllByTask_UserAndDate(user, Date.valueOf(localFrom));
            if (dbList.isEmpty()) {
                responseList.add(new GetBetweenDateSessionResponse(
                        localFrom,
                        "00:00:00",
                        0, 0));
            } else {
                GetBetweenDateSessionResponse response = listToGetBetweenDateSessionResponse(dbList, localFrom);
                responseList.add(response);
            }
        }
        return responseList;
    }

    public ResponseEntity<List<GetByTaskSessionResponse>> getAllByTask(User user, String taskName) {
        Task task = getTaskByUserAndName(user, taskName);
        List<GetByTaskSessionResponse> session = sessionDao.findAllByTask(task).stream()
                .map(this::mapToGetByTaskResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(session);
    }

    private GetByTaskSessionResponse mapToGetByTaskResponse(Session session) {
        return new GetByTaskSessionResponse(session.getDate(), session.getTime());
    }

    private GetBetweenDateSessionResponse listToGetBetweenDateSessionResponse(List<Session> list, LocalDate date) {
        String time;
        long timeByNumber;
        int session = 0;
        int hours = 0;
        int minutes = 0;

        for (Session s : list) {
            hours += s.getTime().getHours();
            minutes += s.getTime().getMinutes();
            session++;
        }

        hours += minutes / 60;
        minutes = minutes % 60;
        time = timeIntsToTimeString(hours, minutes);
        timeByNumber = hours + (long)Math.round((minutes / 60) * 100) / 100;

        return new GetBetweenDateSessionResponse(date, time, timeByNumber, session);
    }

    private String timeIntsToTimeString(int hours, int minutes) {
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

    private boolean checkSaveSession(Session session) {
        try {
            sessionDao.save(session);
            log.info(SESSION_TIME_HAS_BEEN + "saved");
            return true;
        } catch (Exception e) {
            log.error("Session save error", e);
            throw new SaveException("Session", session);
        }
    }

    private Date checkDateFromString(String date) {
        try {
            log.debug("Success format to Date");
            return Date.valueOf(date);
        } catch (Exception e) {
            log.error("Date error", e);
            throw new DateTimeException("Date", date);
        }
    }

    private Time checkTimeFromString(String time) {
        try {
            log.debug("Success format to Time");
            return Time.valueOf(time);
        } catch (Exception e) {
            log.error("Time error", e);
            throw new DateTimeException("Time", time);
        }
    }
}
