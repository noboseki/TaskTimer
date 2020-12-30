package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.Session;
import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.DateTimeException;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.playload.GetByTaskSessionResponse;
import com.noboseki.tasktimer.playload.SessionServiceChainByDateResponse;
import com.noboseki.tasktimer.playload.SessionServiceCreateRequest;
import com.noboseki.tasktimer.playload.SessionServiceTableByDateResponse;
import com.noboseki.tasktimer.repository.SessionDao;
import com.noboseki.tasktimer.service.util.SessionService.SessionServiceGetBarChainByDateUtil;
import com.noboseki.tasktimer.service.util.SessionService.SessionServiceGetTableByDateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionServiceGetTableByDateUtil getTableByDateUtil;
    private final SessionServiceGetBarChainByDateUtil getBarChainByDateUtil;
    private final TaskService taskService;
    private final UserService userService;
    private final SessionDao sessionDao;

    public String create(User user, @Valid SessionServiceCreateRequest request) {
        Task task = taskService.findByNameAndUser(user, request.getTaskName());
        Date date = checkDateFromString(request.getDate());
        Time time = checkTimeFromString(request.getTime());

        saveSession(Session.builder()
                .date(date)
                .time(time)
                .task(task).build());

        return "Session has been " + "created";
    }

    public List<SessionServiceTableByDateResponse> getTableByDate(User user, LocalDate fromDate, LocalDate toDate) {
        userService.findByEmile(user.getEmail());

        List<Session> sessionsListBetweenDate = sessionDao.findAllByTask_UserAndDateBetween(user, Date.valueOf(fromDate), Date.valueOf(toDate));

        if (sessionsListBetweenDate.isEmpty()) {
            return getTableByDateUtil.fillEmptyResponseList(fromDate, toDate);
        }

        return getTableByDateUtil.fillResponseList(sessionsListBetweenDate, fromDate, toDate);
    }

    public SessionServiceChainByDateResponse getBarChainByDate(User user, LocalDate fromDate, LocalDate toDate) {
        userService.findByEmile(user.getEmail());

        List<Session> sessionsBetweenDateForUser = sessionDao.findAllByTask_UserAndDateBetween(user, Date.valueOf(fromDate), Date.valueOf(toDate));

        if (sessionsBetweenDateForUser.isEmpty()) {
            return SessionServiceChainByDateResponse.builder()
                    .dataList(new ArrayList<>())
                    .dateLabel(getBarChainByDateUtil.createDateLabel(fromDate, toDate)).build();
        }
        return getBarChainByDateUtil.fillBarChainByDate(sessionsBetweenDateForUser, fromDate, toDate);
    }

    public List<GetByTaskSessionResponse> getAllByTask(User user, String taskName) {
        Task task = taskService.getTaskByUserAndName(user, taskName);
        return sessionDao.findAllByTask(task).stream()
                .map(this::mapToGetByTaskResponse)
                .collect(Collectors.toList());
    }

    private GetByTaskSessionResponse mapToGetByTaskResponse(Session session) {
        return new GetByTaskSessionResponse(session.getDate(), session.getTime());
    }

    private Session saveSession(Session session) {
        try {
            Session dbSession = sessionDao.save(session);
            if (sessionDao.findById(dbSession.getId()).isPresent()) {
                log.info("Session has been saved");
                return dbSession;
            } else {
                throw new SaveException("Session", session.getTime().toString());
            }
        } catch (Exception e) {
            log.error("Session save error", e);
            throw new SaveException("Session", session.getTime().toString());
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
