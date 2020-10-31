package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.Session;
import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.DateTimeException;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.playload.ApiResponse;
import com.noboseki.tasktimer.playload.CreateSessionRequest;
import com.noboseki.tasktimer.playload.GetByDateSessionResponse;
import com.noboseki.tasktimer.playload.GetByTaskSessionResponse;
import com.noboseki.tasktimer.repository.SessionDao;
import com.noboseki.tasktimer.repository.TaskDao;
import com.noboseki.tasktimer.repository.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SessionService extends MainService{
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

        public ResponseEntity<List<GetByTaskSessionResponse>> getAllByTask(User user, String taskName) {
        Task task = getTaskByUserAndName(user, taskName);
        List<GetByTaskSessionResponse> session = sessionDao.findAllByTask(task).stream()
                .map(this::mapToGetByTaskResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(session);
    }

    public ResponseEntity<List<GetByDateSessionResponse>> getByDate(User user, String date){
        Date dateFromString = checkDateFromString(date);
        User dbUser = checkGetUser(user.getEmail());
        List<GetByDateSessionResponse> responses = sessionDao.findAllByTask_User_EmailAndDate(dbUser.getEmail(),dateFromString).stream()
                .map(this::mapToGetByDateResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    private GetByDateSessionResponse mapToGetByDateResponse(Session session) {
        return new GetByDateSessionResponse(session.getDate(), session.getTime(), session.getTask().getName());
    }

    private GetByTaskSessionResponse mapToGetByTaskResponse(Session session){
        return new GetByTaskSessionResponse(session.getDate(), session.getTime());
    }

    private boolean checkSaveSession(Session session){
        try {
            sessionDao.save(session);
            log.info(SESSION_TIME_HAS_BEEN + "saved");
            return true;
        } catch (Exception e) {
            log.error("Session save error", e);
            throw new SaveException("Session", session);
        }
    }

    private Date checkDateFromString(String date){
        try {
            log.debug("Success format to Date");
            return Date.valueOf(date);
        } catch (Exception e) {
            log.error("Date error" , e);
            throw new DateTimeException("Date", date);
        }
    }

    private Time checkTimeFromString(String time){
        try {
            log.debug("Success format to Time");
            return Time.valueOf(time);
        } catch (Exception e) {
            log.error("Time error" , e);
            throw new DateTimeException("Time", time);
        }
    }
}
