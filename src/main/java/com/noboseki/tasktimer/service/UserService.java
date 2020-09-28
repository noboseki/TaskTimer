package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.DeleteException;
import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import com.noboseki.tasktimer.exeption.SaveException;
import com.noboseki.tasktimer.playload.ApiResponse;
import com.noboseki.tasktimer.repository.UserDao;
import com.noboseki.tasktimer.util.EntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@Service
public class UserService {
    private final String USER_HAS_BEEN = "User has been ";

    private UserDao dao;

    public UserService(UserDao dao) {
        this.dao = dao;
    }

    public ResponseEntity<ApiResponse> create(@Valid User.UserDto dto) {
        checkSaveUser(dto);
        return getApiResponse(true,"created");
    }

    public ResponseEntity<User.UserDto> get(UUID userID) {
        User user = checkGetUser(userID);
        log.info(USER_HAS_BEEN + "taken");
        return ResponseEntity.ok(EntityMapper.mapToDto(user));
    }

    public ResponseEntity<ApiResponse> update(@Valid User.UserDto dto) {
        checkGetUser(dto.getPrivateID());
        checkSaveUser(dto);
        return getApiResponse(true,"updated");
    }

    public ResponseEntity<ApiResponse> delete(UUID userID) {
        checkGetUser(userID);
        boolean isDeleted = checkDeleteUser(userID);
        return getApiResponse(isDeleted,"deleted");
    }

    private ResponseEntity<ApiResponse> getApiResponse(boolean isCorrect, String methodName) {
        return ResponseEntity.ok().body(new ApiResponse(isCorrect, USER_HAS_BEEN + methodName));
    }

    private User checkGetUser(UUID userID) {
        return dao.findById(userID).orElseThrow(() -> new ResourceNotFoundException("User: ", "id", userID));
    }

    private boolean checkDeleteUser(UUID userId) {
        try {
            dao.deleteById(userId);
            log.info(USER_HAS_BEEN + "deleted");
            return true;
        } catch (Exception e) {
            log.error("Delete error", e);
            throw new DeleteException("User",userId.toString());
        }
    }

    private boolean checkSaveUser(User.UserDto dto){
        try {
            dao.save(EntityMapper.mapToEntity(dto));
            log.info(USER_HAS_BEEN + "saved");
            return true;
        } catch (Exception e) {
            log.error("User save error", e);
            throw new SaveException("User", dto);
        }
    }
}
