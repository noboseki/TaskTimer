package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.Session;
import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.DateTimeException;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.playload.*;
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

    public SessionService(TaskDao taskDao, UserDao userDao, SessionDao sessionDao,
                          SessionServiceGetTableByDateUtil getTableByDateUtil,
                          SessionServiceGetBarChainByDateUtil getBarChainByDateUtil) {
        super(taskDao, userDao, sessionDao);
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

    public List<GetTableByDateResponse> getTableByDate(User user, String fromDate, LocalDate toDate) {
        checkUserPresenceInDb(user.getEmail());
        List<GetTableByDateResponse> responseList = new ArrayList<>();
        LocalDate loopDate = LocalDate.parse(fromDate);

        List<Session> sessionsListBetweenDate = sessionDao.findAllByTask_UserAndDateBetween(user, Date.valueOf(fromDate), Date.valueOf(toDate));

        if (sessionsListBetweenDate.isEmpty()) {
            while (loopDate.isBefore(toDate) || loopDate.equals(toDate)) {
                responseList.add(new GetTableByDateResponse(
                        loopDate,
                        "00:00:00",
                        0));
                loopDate = loopDate.plusDays(1);
            }
            return responseList;
        }

        while (loopDate.isBefore(toDate) || loopDate.equals(toDate)) {
            List<Session> tempSessionsByDateList = getTableByDateUtil.extractSessionsByDateAndRemove(sessionsListBetweenDate, loopDate);
            getTableByDateUtil.fillingResponseListByDate(tempSessionsByDateList, responseList, loopDate);
            loopDate = loopDate.plusDays(1);
        }

        return responseList;
    }

    public GetSessionChainByDateResponse getBarChainByDate(User user, String fromDate, String toDate) {
        checkUserPresenceInDb(user.getEmail());

        GetSessionChainByDateResponse response = new GetSessionChainByDateResponse();
        response.setDataList(new ArrayList<>());
        response.setDateLabel(getBarChainByDateUtil.createDateLabel(fromDate, toDate));

        List<Session> sessionsBetweenDateForUser = sessionDao.findAllByTask_UserAndDateBetween(user, Date.valueOf(fromDate), Date.valueOf(toDate));

        if (sessionsBetweenDateForUser.isEmpty()) {
            return response;
        }

        for (String taskName : getBarChainByDateUtil.getTaskNamesFromList(sessionsBetweenDateForUser)) {

            GetSessionChainDataForTask data = new GetSessionChainDataForTask(new ArrayList<>(), taskName);
            LocalDate loopDate = LocalDate.parse(fromDate);

            while (loopDate.isBefore(LocalDate.parse(toDate).plusDays(1))) {
                List<Time> sessions = getBarChainByDateUtil.extractSessionsTimeByDateAndTaskName(sessionsBetweenDateForUser, taskName, loopDate);
                data.getData().add(getBarChainByDateUtil.listToLongTime(sessions));
                loopDate = loopDate.plusDays(1);
            }

            response.getDataList().add(data);
        }
        return response;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
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
