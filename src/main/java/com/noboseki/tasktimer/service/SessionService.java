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
import com.noboseki.tasktimer.service.constants.ServiceTextConstants;
import com.noboseki.tasktimer.service.util.ServiceUtil;
import com.noboseki.tasktimer.service.util.session_service.SessionServiceGetBarChainByDateUtil;
import com.noboseki.tasktimer.service.util.session_service.SessionServiceGetTableByDateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {
    private static final String SESSION = "Session";
    private static final String DATE = "Date";
    private static final String TIME = "Time";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME24HOURS_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]";

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

        return ServiceTextConstants.hasBeenCreate(SESSION);
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
                return dbSession;
            } else {
                throw new SaveException(SESSION, session.getTime().toString());
            }
        } catch (Exception e) {
            throw new SaveException(SESSION, session.getTime().toString());
        }
    }

    private Time checkTimeFromString(String time) {
        Pattern pattern = Pattern.compile(TIME24HOURS_PATTERN);
        Matcher matcher = pattern.matcher(time);
        if (matcher.matches()) {
            return Time.valueOf(time);
        } else {
            throw new DateTimeException(TIME, time);
        }
    }

    private Date checkDateFromString(String date) {

        boolean isCorrect = util.isValidFormat(DATE_FORMAT, date);

        if (isCorrect) {
            return Date.valueOf(date);
        } else {
            throw new DateTimeException(DATE, date);
        }
    }

    private boolean checkDateFromString(String from, String to) {
        boolean fromCorrect = util.isValidFormat(DATE_FORMAT, from);
        boolean toCorrect = util.isValidFormat(DATE_FORMAT, to);

        if (fromCorrect && toCorrect) {
            return true;
        } else {
            final String or = " or ";
            throw new DateTimeException(DATE, from + or + to);
        }
    }
}
