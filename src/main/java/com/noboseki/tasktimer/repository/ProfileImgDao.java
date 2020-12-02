package com.noboseki.tasktimer.repository;

import com.noboseki.tasktimer.domain.ProfileImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileImgDao extends JpaRepository<ProfileImg, Integer> {

    Optional<ProfileImg> findByName(String name);
}
