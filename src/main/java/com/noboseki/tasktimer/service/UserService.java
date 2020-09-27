package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import com.noboseki.tasktimer.playload.ApiResponse;
import com.noboseki.tasktimer.repository.UserDao;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private UserDao dao;

    public UserService(UserDao dao) {
        this.dao = dao;
    }

    public ResponseEntity<ApiResponse> create(User.UserDto dto) {
        dao.save(new User().mapToEntity(dto));
        return ResponseEntity.ok().body(new ApiResponse(true,"User has been saved"));
    }

    public ResponseEntity<User.UserDto> get(UUID userID) {
        User user = dao.findById(userID).orElseThrow(() -> new ResourceNotFoundException("User: ", "id", userID));
        return ResponseEntity.ok(new User().mapToDto(user));
    }

    public ResponseEntity<ApiResponse> update(User.UserDto dto) {
        dao.findById(dto.getPrivateID()).orElseThrow(() -> new ResourceNotFoundException("User :", "id", dto.getPrivateID()));
        User user = dao.save(new User().mapToEntity(dto));
        return ResponseEntity.ok().body(new ApiResponse(true,"User has been saved"));
    }

    public ResponseEntity<ApiResponse> delete(UUID userID) {
        dao.findById(userID).orElseThrow(() -> new ResourceNotFoundException("User :", "id", userID));
        dao.deleteById(userID);
        return ResponseEntity.ok(new ApiResponse(true,"User has been deleted"));
    }
}
