package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.Session;
import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.playload.ApiResponse;
import com.noboseki.tasktimer.playload.CreateSessionRequest;
import com.noboseki.tasktimer.playload.GetByTaskSessionResponse;
import com.noboseki.tasktimer.repository.SessionDao;
import com.noboseki.tasktimer.repository.TaskDao;
import com.noboseki.tasktimer.repository.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SessionService extends MainService{
    private final String SESSION_TIME_HAS_BEEN = "Session has been ";

    private SessionDao sessionDao;

    public SessionService(TaskDao taskDao, UserDao userDao, SessionDao sessionDao) {
        super(taskDao, userDao);
        this.sessionDao = sessionDao;
    }

    public ResponseEntity<ApiResponse> create(User user, String taskName, CreateSessionRequest request) {
        Task task = getTaskByUserAndName(user, taskName);

        Session session = Session.builder()
                .date(request.getDate())
                .time(request.getTime())
                .task(task).build();

        return getApiResponse(checkSaveSession(session), SESSION_TIME_HAS_BEEN + "created");
    }

        public ResponseEntity<List<GetByTaskSessionResponse>> getByTask(User user, String taskName) {
        Task task = getTaskByUserAndName(user, taskName);
        List<GetByTaskSessionResponse> session = sessionDao.findAllByTask(task).stream()
                .map(this::mapToGetByTaskResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(session);
    }

    public ResponseEntity<List<GetByTaskSessionResponse>> getByDate(User user, Date date){
        User dbUser = checkGetUser(user.getEmail());
        return null;
    }

    /*
       public ResponseEntity<ApiResponse> update(@Valid Session.SessionDto dto) {
           checkGetWorkTime(dto.getPrivateID());
           checkSaveWorkTime(dto);
           return getApiResponse(true, "updated");
       }

 public ResponseEntity<ApiResponse> delete(UUID workTimeID) {
           checkGetWorkTime(workTimeID);
           boolean isDeleted = checkDeleteWorkTime(workTimeID);
           return getApiResponse(isDeleted, "deleted");
       }

       private ResponseEntity<ApiResponse> getApiResponse(boolean isCorrect, String methodName) {
           return ResponseEntity.ok().body(new ApiResponse(isCorrect, WORK_TIME_HAS_BEEN + methodName));
       }

       private Session checkGetWorkTime(UUID workTimeID) {
           return sessionDao.findById(workTimeID).orElseThrow(() -> new ResourceNotFoundException("WorkTime: ", "id", workTimeID));
       }

       private boolean checkDeleteWorkTime(UUID WorkTImeID) {
           try {
               sessionDao.deleteById(WorkTImeID);
               log.info(WORK_TIME_HAS_BEEN + "deleted");
               return true;
           } catch (Exception e) {
               log.error("Delete error", e);
               throw new DeleteException("WorkTime", WorkTImeID.toString());
           }
       }
   */
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

    private GetByTaskSessionResponse mapToGetByTaskResponse(Session session){
        return new GetByTaskSessionResponse(session.getDate(), session.getTime());
    }
}
