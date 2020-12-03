package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.Session;
import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.DateTimeException;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.playload.*;
import com.noboseki.tasktimer.repository.ProfileImgDao;
import com.noboseki.tasktimer.repository.SessionDao;
import com.noboseki.tasktimer.repository.TaskDao;
import com.noboseki.tasktimer.repository.UserDao;
import com.noboseki.tasktimer.service.util.SessionServiceGetBarChainByDateUtil;
import com.noboseki.tasktimer.service.util.SessionServiceGetTableByDateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SessionService extends MainService {
    private final String SESSION_TIME_HAS_BEEN = "Session has been ";
    private final SessionServiceGetTableByDateUtil getTableByDateUtil;
    private final SessionServiceGetBarChainByDateUtil getBarChainByDateUtil;

    public SessionService(TaskDao taskDao, UserDao userDao,
                          SessionDao sessionDao, ProfileImgDao profileImgDao,
                          SessionServiceGetTableByDateUtil getTableByDateUtil,
                          SessionServiceGetBarChainByDateUtil getBarChainByDateUtil) {
        super(taskDao, userDao, sessionDao, profileImgDao);
        this.getTableByDateUtil = getTableByDateUtil;
        this.getBarChainByDateUtil = getBarChainByDateUtil;
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

    public List<SessionServiceTableByDateResponse> getTableByDate(User user, LocalDate fromDate, LocalDate toDate) {
        checkUserPresenceInDb(user.getEmail());

        List<Session> sessionsListBetweenDate = sessionDao.findAllByTask_UserAndDateBetween(user, Date.valueOf(fromDate), Date.valueOf(toDate));

        if (sessionsListBetweenDate.isEmpty()) {
            return getTableByDateUtil.fillEmptyResponseList(fromDate, toDate);
        }

        return getTableByDateUtil.fillResponseList(sessionsListBetweenDate, fromDate, toDate);
    }

    public SessionServiceChainByDateResponse getBarChainByDate(User user, LocalDate fromDate, LocalDate toDate) {
        checkUserPresenceInDb(user.getEmail());

        List<Session> sessionsBetweenDateForUser = sessionDao.findAllByTask_UserAndDateBetween(user, Date.valueOf(fromDate), Date.valueOf(toDate));

        if (sessionsBetweenDateForUser.isEmpty()) {
            return SessionServiceChainByDateResponse.builder()
                    .dataList(new ArrayList<>())
                    .dateLabel(getBarChainByDateUtil.createDateLabel(fromDate, toDate)).build();
        }
        return getBarChainByDateUtil.fillBarChainByDate(sessionsBetweenDateForUser, fromDate, toDate);
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
