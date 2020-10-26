package com.noboseki.tasktimer.bootstrap;

import com.noboseki.tasktimer.domain.Authority;
import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.domain.Session;
import com.noboseki.tasktimer.repository.AuthorityDao;
import com.noboseki.tasktimer.repository.TaskDao;
import com.noboseki.tasktimer.repository.UserDao;
import com.noboseki.tasktimer.repository.SessionDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultTaskTimerLoader implements CommandLineRunner {

    private final SessionDao sessionDao;
    private final TaskDao taskDao;
    private final UserDao userDao;
    private final AuthorityDao authorityDao;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        runBootstrap();
    }

    public void runBootstrap() {
        log.info("Starting bootstrap loader");
        boolean checkDb = checkBootstrap();
        loadTaskTimerLoader(checkDb);
        log.info("Bootstrap has been loaded");
    }

    private boolean checkBootstrap() {
        boolean isUser = userDao.findAll().isEmpty();
        boolean isTask = taskDao.findAll().isEmpty();
        boolean isWorkTime = sessionDao.findAll().isEmpty();

        return isUser || isTask || isWorkTime;
    }

    private void loadTaskTimerLoader(boolean isTrue) {
        if (isTrue){

            Authority adminA = authorityDao.save(Authority.builder().role("ROLE_ADMIN").build());
            Authority userA = authorityDao.save(Authority.builder().role("ROLE_USER").build());

            User admin = userDao.save(User.builder()
                    .username("admin")
                    .email("admin@test.com")
                    .imageUrl("testURL")
                    .password(passwordEncoder.encode("spring"))
                    .authority(adminA)
                    .authority(userA).build()) ;

            User user = userDao.save(User.builder()
                    .username("user")
                    .email("user@test.com")
                    .imageUrl("testURL")
                    .password(passwordEncoder.encode("password"))
                    .authority(userA).build()) ;

            Task taskAdmin = taskDao.save(Task.builder()
                    .name("Task Admin")
                    .user(admin).build());

            Task taskUser = taskDao.save(Task.builder()
                    .name("Task User")
                    .user(user).build());

            sessionDao.save(Session.builder()
                    .date(Date.valueOf(LocalDate.of(2020, 3,20)))
                    .time(Time.valueOf(LocalTime.of(4, 6)))
                    .task(taskAdmin).build());

            sessionDao.save(Session.builder()
                    .date(Date.valueOf(LocalDate.of(2020, 3,21)))
                    .time(Time.valueOf(LocalTime.of(5, 24)))
                    .task(taskAdmin).build());

            sessionDao.save(Session.builder()
                    .date(Date.valueOf(LocalDate.of(2020, 3,15)))
                    .time(Time.valueOf(LocalTime.of(2, 55)))
                    .task(taskUser).build());

            sessionDao.save(Session.builder()
                    .date(Date.valueOf(LocalDate.of(2020, 3,16)))
                    .time(Time.valueOf(LocalTime.of(3, 41)))
                    .task(taskUser).build());
        }
    }
}
