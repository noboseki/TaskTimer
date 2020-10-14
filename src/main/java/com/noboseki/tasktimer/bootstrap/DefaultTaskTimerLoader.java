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

        return isUser && isTask && isWorkTime;
    }

    private void loadTaskTimerLoader(boolean isTrue) {
        if (isTrue){
            Authority authority = Authority.builder()
                    .role("ADMIN").build();

            authority = authorityDao.save(authority);

            User user = User.builder()
                    .username("user")
                    .email("test@test.com")
                    .imageUrl("testURL")
                    .password(passwordEncoder.encode("password"))
                    .authority(authority).build();

            User userFromDb = userDao.save(user);

            Task task = Task.builder()
                    .name("Test name")
                    .user(userFromDb).build();

            Task taskFromDb = taskDao.save(task);

            Session session = Session.builder()
                    .date(Date.valueOf(LocalDate.of(2020, 3,20)))
                    .time(Time.valueOf(LocalTime.of(4, 6)))
                    .task(taskFromDb).build();

            sessionDao.save(session);
        }
    }
}
