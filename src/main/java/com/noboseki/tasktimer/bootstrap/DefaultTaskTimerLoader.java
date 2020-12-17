package com.noboseki.tasktimer.bootstrap;

import com.noboseki.tasktimer.domain.*;
import com.noboseki.tasktimer.repository.*;
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
    private final ConfirmationTokenDao confirmationTokenDao;
    private final PasswordEncoder passwordEncoder;
    private final ProfileImgDao profileImgDao;

    @Override
    public void run(String... args) {
        runBootstrap();
    }

    public void runBootstrap() {
        log.info("Starting bootstrap loader");
        setIcons();
        setAuthority();
        setUsers();
        setTasksAndSessions();
        log.info("Bootstrap has been loaded");
    }

    private void setTasksAndSessions() {
        if (taskDao.findAll().isEmpty()) {
            User user = userDao.findByEmail("user@test.com").orElseThrow(RuntimeException::new);
            User admin = userDao.findByEmail("admin@test.com").orElseThrow(RuntimeException::new);

            Task taskAdmin = taskDao.save(Task.builder()
                    .name("Task Admin")
                    .user(admin).build());

            Task userA = taskDao.save(Task.builder()
                    .name("Task User A")
                    .user(user).build());

            Task userB = taskDao.save(Task.builder()
                    .name("Task User B")
                    .complete(true)
                    .user(user).build());

            Task userC = taskDao.save(Task.builder()
                    .name("Task User C")
                    .user(user).build());

            createSession("04:06:00", 0, taskAdmin);
            createSession("05:24:00", 2, taskAdmin);

            createSession("02:55:00", 1, userA);
            createSession("03:16:00", 0, userA);
            createSession("03:41:00", 2, userA);

            createSession("03:46:00", 4, userB);
            createSession("02:19:00", 1, userB);

            createSession("03:49:00", 3, userC);
            createSession("04:29:00", 1, userC);
        } else {
            throw new RuntimeException();
        }
    }

    private void setUsers() {
        if (userDao.findAll().isEmpty()) {
            Authority user = authorityDao.findByRole("ROLE_USER").orElseThrow(RuntimeException::new);
            Authority admin = authorityDao.findByRole("ROLE_ADMIN").orElseThrow(RuntimeException::new);
            ProfileImg profileImg = profileImgDao.findByName("SpiderMan").orElseThrow(RuntimeException::new);

            User Uadmin = userDao.save(User.builder()
                    .username("admin")
                    .email("admin@test.com")
                    .password(passwordEncoder.encode("spring"))
                    .profileImg(profileImg)
                    .enabled(true)
                    .authority(user)
                    .authority(admin).build());

            User Uuser = userDao.save(User.builder()
                    .username("user")
                    .email("user@test.com")
                    .password(passwordEncoder.encode("password"))
                    .enabled(true)
                    .profileImg(profileImg)
                    .authority(user).build());

            confirmationTokenDao.save(new ConfirmationToken(Uadmin));
            confirmationTokenDao.save(new ConfirmationToken(Uuser));

        } else {
            throw new RuntimeException();
        }
    }

    private void setAuthority() {
        if (authorityDao.findAll().isEmpty()) {
            authorityDao.save(Authority.builder().role("ROLE_ADMIN").build());
            authorityDao.save(Authority.builder().role("ROLE_USER").build());
        } else {
            throw new RuntimeException();
        }
    }

    private void setIcons() {
        if (profileImgDao.findAll().isEmpty()) {
            saveIcon("CptAmerica", "1492436");
            saveIcon("IronMan", "1492446");
            saveIcon("SpiderMan", "1492453");
            saveIcon("Deadpool", "1492437");
            saveIcon("Hulk", "1492445");
            saveIcon("BlackPanther", "1492439");
            saveIcon("Thor", "1492457");
            saveIcon("BlackWidow", "1492435");
            saveIcon("Thanos", "1492455");
            saveIcon("AntMan", "1492433");
            saveIcon("Groot", "1492443");
            saveIcon("Loki", "1492443");
            saveIcon("Falcon", "1492447");
            saveIcon("Hawkeye", "1492444");
            saveIcon("NickFury", "1492450");
            saveIcon("Rocket", "1492452");
            saveIcon("Wasp", "1492459");
            saveIcon("Gamora", "1492442");
            saveIcon("Mantis", "1492448");
            saveIcon("StarLord", "1492454");
            saveIcon("Nebula", "1492449");
            saveIcon("Vision", "1492458");
            saveIcon("Think", "1492456");
            saveIcon("WinterSoldier", "1492460");
            saveIcon("Yondu", "1492461");
            saveIcon("Doom", "1492438");
            saveIcon("Drax", "1492440");
        } else {
            throw new RuntimeException();
        }
    }

    private void saveIcon(String name, String urlNNumber) {
        ProfileImg profileImg = new ProfileImg();
        profileImg.setName(name);
        profileImg.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/" + urlNNumber + ".svg");

        profileImgDao.save(profileImg);
    }

    private void createSession(String time, int minusDayToady, Task task) {
        sessionDao.save(Session.builder()
                .date(Date.valueOf(LocalDate.now().minusDays(minusDayToady)))
                .time(Time.valueOf(LocalTime.parse(time)))
                .task(task).build());
    }
}