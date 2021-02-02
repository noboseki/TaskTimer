package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.ProfileImg;
import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import com.noboseki.tasktimer.repository.ProfileImgDao;
import com.noboseki.tasktimer.service.constants.ServiceTextConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileImgService {

    private final ProfileImgDao profileImgDao;

    public List<ProfileImg> getAllIcons() {
        return profileImgDao.findAll();
    }

    public ProfileImg findByName(String name) {
        return profileImgDao.findByName(name).orElseThrow(() -> new ResourceNotFoundException(ServiceTextConstants.getProfileImg(), name));
    }
}
