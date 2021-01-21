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
import com.noboseki.tasktimer.service.util.ServiceUtil;
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
    private final ServiceUtil util;

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

    public List<SessionServiceTableByDateResponse> getTableByDate(User user, String fromDate, String toDate) {
        userService.findByEmile(user.getEmail());
        checkDateFromString(fromDate, toDate);

        LocalDate from = LocalDate.parse(fromDate);
        LocalDate to = LocalDate.parse(toDate);

        List<Session> sessionsListBetweenDate = sessionDao.findAllByTask_UserAndDateBetween(user, Date.valueOf(fromDate), Date.valueOf(toDate));

        if (sessionsListBetweenDate.isEmpty()) {
            return getTableByDateUtil.fillEmptyResponseList(from, to);
        }

        return getTableByDateUtil.fillResponseList(sessionsListBetweenDate, from, to);
    }

    public SessionServiceChainByDateResponse getBarChainByDate(User user, String fromDate, String toDate) {
        userService.findByEmile(user.getEmail());
        checkDateFromString(fromDate, toDate);
        LocalDate from = LocalDate.parse(fromDate);
        LocalDate to = LocalDate.parse(toDate);

        List<Session> sessionsBetweenDateForUser = sessionDao.findAllByTask_UserAndDateBetween(user, Date.valueOf(from), Date.valueOf(to));

        if (sessionsBetweenDateForUser.isEmpty()) {
            return SessionServiceChainByDateResponse.builder()
                    .dataList(new ArrayList<>())
                    .dateLabel(getBarChainByDateUtil.createDateLabel(from, to)).build();
        }
        return getBarChainByDateUtil.fillBarChainByDate(sessionsBetweenDateForUser, from, to);
    }

    public List<GetByTaskSessionResponse> getAllByTask(User user, String taskName) {
        Task task = taskService.findByNameAndUser(user, taskName);

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

    private boolean checkDateFromString(String from, String to) {
        final String format = "yyyy-MM-dd";
        boolean fromCorrect = util.isValidFormat(format, from);
        boolean toCorrect = util.isValidFormat(format, to);

        if (fromCorrect && toCorrect) {
            return true;
        } else {
            throw new DateTimeException("Date", from + " or " + to);
        }
    }
}
