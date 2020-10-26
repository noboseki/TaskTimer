/*
package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.Session;
import com.noboseki.tasktimer.exeption.DeleteException;
import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.playload.ApiResponse;
import com.noboseki.tasktimer.repository.SessionDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@Service
public class SessionService {
    private final String WORK_TIME_HAS_BEEN = "WorkTime has been ";

    private SessionDao dao;

    public SessionService(SessionDao dao) {
        this.dao = dao;
    }

    public ResponseEntity<ApiResponse> create(@Valid Session.SessionDto dto) {
        checkSaveWorkTime(dto);
        return getApiResponse(true, "created");
    }

    public ResponseEntity<Session.SessionDto> get(UUID workTimeID) {
        Session session = checkGetWorkTime(workTimeID);
        log.info(WORK_TIME_HAS_BEEN + "taken");
        return ResponseEntity.ok(EntityMapper.mapToDto(session));
    }

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
        return dao.findById(workTimeID).orElseThrow(() -> new ResourceNotFoundException("WorkTime: ", "id", workTimeID));
    }

    private boolean checkDeleteWorkTime(UUID WorkTImeID) {
        try {
            dao.deleteById(WorkTImeID);
            log.info(WORK_TIME_HAS_BEEN + "deleted");
            return true;
        } catch (Exception e) {
            log.error("Delete error", e);
            throw new DeleteException("WorkTime", WorkTImeID.toString());
        }
    }

    private boolean checkSaveWorkTime(Session.SessionDto dto){
        try {
            dao.save(EntityMapper.mapToEntity(dto));
            log.info(WORK_TIME_HAS_BEEN + "saved");
            return true;
        } catch (Exception e) {
            log.error("WorkTime save error", e);
            throw new SaveException("WorkTime", dto);
        }
    }
}*/
