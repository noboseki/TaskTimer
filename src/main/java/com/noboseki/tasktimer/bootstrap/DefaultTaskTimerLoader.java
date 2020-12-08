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
    private final PasswordEncoder passwordEncoder;
    private final ProfileImgDao profileImgDao;

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
        if (isTrue) {

            Authority adminA = authorityDao.save(Authority.builder().role("ROLE_ADMIN").build());
            Authority userA = authorityDao.save(Authority.builder().role("ROLE_USER").build());

            setIcons();

            ProfileImg adminIcon = profileImgDao.findByName("Doom").get();
            ProfileImg userIcon = profileImgDao.findByName("StarLord").get();

            User admin = userDao.save(User.builder()
                    .username("admin")
                    .email("admin@test.com")
                    .password(passwordEncoder.encode("spring"))
                    .profileImg(adminIcon)
                    .authority(adminA)
                    .authority(userA).build());

            User user = userDao.save(User.builder()
                    .username("user")
                    .email("user@test.com")
                    .password(passwordEncoder.encode("password"))
                    .profileImg(userIcon)
                    .authority(userA).build());

            Task taskAdmin = taskDao.save(Task.builder()
                    .name("Task Admin")
                    .user(admin).build());

            Task taskUser = taskDao.save(Task.builder()
                    .name("Task User")
                    .user(user).build());

            Task taskUser1 = taskDao.save(Task.builder()
                    .name("Task User 2")
                    .complete(true)
                    .user(user).build());

            Task taskUser2 = taskDao.save(Task.builder()
                    .name("Task User 3")
                    .user(user).build());

            sessionDao.save(Session.builder()
                    .date(Date.valueOf(LocalDate.of(2020, 11, 20)))
                    .time(Time.valueOf(LocalTime.of(4, 6)))
                    .task(taskAdmin).build());

            sessionDao.save(Session.builder()
                    .date(Date.valueOf(LocalDate.of(2020, 11, 21)))
                    .time(Time.valueOf(LocalTime.of(5, 24)))
                    .task(taskAdmin).build());

            sessionDao.save(Session.builder()
                    .date(Date.valueOf(LocalDate.of(2020, 11, 15)))
                    .time(Time.valueOf(LocalTime.of(2, 55)))
                    .task(taskUser).build());

            sessionDao.save(Session.builder()
                    .date(Date.valueOf(LocalDate.of(2020, 11, 15)))
                    .time(Time.valueOf(LocalTime.of(3, 55)))
                    .task(taskUser).build());

            sessionDao.save(Session.builder()
                    .date(Date.valueOf(LocalDate.of(2020, 11, 15)))
                    .time(Time.valueOf(LocalTime.of(3, 55)))
                    .task(taskUser1).build());

            sessionDao.save(Session.builder()
                    .date(Date.valueOf(LocalDate.of(2020, 11, 16)))
                    .time(Time.valueOf(LocalTime.of(3, 41)))
                    .task(taskUser).build());

            sessionDao.save(Session.builder()
                    .date(Date.valueOf(LocalDate.of(2020, 11, 15)))
                    .time(Time.valueOf(LocalTime.of(3, 55)))
                    .task(taskUser2).build());

            sessionDao.save(Session.builder()
                    .date(Date.valueOf(LocalDate.of(2020, 11, 16)))
                    .time(Time.valueOf(LocalTime.of(3, 41)))
                    .task(taskUser2).build());
        }
    }

    private void setIcons() {
        ProfileImg profileImg1 = new ProfileImg();
        profileImg1.setName("CptAmerica");
        profileImg1.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492436.svg");

        ProfileImg profileImg2 = new ProfileImg();
        profileImg2.setName("IronMan");
        profileImg2.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492446.svg");

        ProfileImg profileImg3 = new ProfileImg();
        profileImg3.setName("SpiderMan");
        profileImg3.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492453.svg");

        ProfileImg profileImg4 = new ProfileImg();
        profileImg4.setName("Deadpool");
        profileImg4.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492437.svg");

        ProfileImg profileImg5 = new ProfileImg();
        profileImg5.setName("Hulk");
        profileImg5.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492445.svg");

        ProfileImg profileImg6 = new ProfileImg();
        profileImg6.setName("BlackPanther");
        profileImg6.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492434.svg");

        ProfileImg profileImg7 = new ProfileImg();
        profileImg7.setName("DrStrange");
        profileImg7.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492439.svg");

        ProfileImg profileImg8 = new ProfileImg();
        profileImg8.setName("Thor");
        profileImg8.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492457.svg");

        ProfileImg profileImg9 = new ProfileImg();
        profileImg9.setName("BlackWidow");
        profileImg9.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492435.svg");

        ProfileImg profileImg10 = new ProfileImg();
        profileImg10.setName("Thanos");
        profileImg10.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492455.svg");

        ProfileImg profileImg11 = new ProfileImg();
        profileImg11.setName("AntMan");
        profileImg11.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492433.svg");

        ProfileImg profileImg12 = new ProfileImg();
        profileImg12.setName("Groot");
        profileImg12.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492443.svg");

        ProfileImg profileImg13 = new ProfileImg();
        profileImg13.setName("Loki");
        profileImg13.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492447.svg");

        ProfileImg profileImg14 = new ProfileImg();
        profileImg14.setName("Falcon");
        profileImg14.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492441.svg");

        ProfileImg profileImg15 = new ProfileImg();
        profileImg15.setName("Hawkeye");
        profileImg15.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492444.svg");

        ProfileImg profileImg16 = new ProfileImg();
        profileImg16.setName("NickFury");
        profileImg16.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492450.svg");

        ProfileImg profileImg17 = new ProfileImg();
        profileImg17.setName("Rocket");
        profileImg17.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492452.svg");

        ProfileImg profileImg18 = new ProfileImg();
        profileImg18.setName("Wasp");
        profileImg18.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492459.svg");

        ProfileImg profileImg19 = new ProfileImg();
        profileImg19.setName("Gamora");
        profileImg19.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492442.svg");

        ProfileImg profileImg20 = new ProfileImg();
        profileImg20.setName("Mantis");
        profileImg20.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492448.svg");

        ProfileImg profileImg21 = new ProfileImg();
        profileImg21.setName("StarLord");
        profileImg21.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492454.svg");

        ProfileImg profileImg22 = new ProfileImg();
        profileImg22.setName("Nebula");
        profileImg22.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492449.svg");

        ProfileImg profileImg23 = new ProfileImg();
        profileImg23.setName("Vision");
        profileImg23.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492458.svg");

        ProfileImg profileImg24 = new ProfileImg();
        profileImg24.setName("Think");
        profileImg24.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492456.svg");

        ProfileImg profileImg25 = new ProfileImg();
        profileImg25.setName("WinterSoldier");
        profileImg25.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492460.svg");

        ProfileImg profileImg26 = new ProfileImg();
        profileImg26.setName("Yondu");
        profileImg26.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492461.svg");

        ProfileImg profileImg27 = new ProfileImg();
        profileImg27.setName("Doom");
        profileImg27.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492438.svg");

        ProfileImg profileImg28 = new ProfileImg();
        profileImg28.setName("Drax");
        profileImg28.setUrlAddress("https://www.flaticon.com/svg/static/icons/svg/1492/1492440.svg");

        profileImgDao.save(profileImg1);
        profileImgDao.save(profileImg2);
        profileImgDao.save(profileImg3);
        profileImgDao.save(profileImg4);
        profileImgDao.save(profileImg5);
        profileImgDao.save(profileImg6);
        profileImgDao.save(profileImg7);
        profileImgDao.save(profileImg8);
        profileImgDao.save(profileImg9);
        profileImgDao.save(profileImg10);
        profileImgDao.save(profileImg11);
        profileImgDao.save(profileImg12);
        profileImgDao.save(profileImg13);
        profileImgDao.save(profileImg14);
        profileImgDao.save(profileImg15);
        profileImgDao.save(profileImg16);
        profileImgDao.save(profileImg17);
        profileImgDao.save(profileImg18);
        profileImgDao.save(profileImg19);
        profileImgDao.save(profileImg20);
        profileImgDao.save(profileImg21);
        profileImgDao.save(profileImg22);
        profileImgDao.save(profileImg23);
        profileImgDao.save(profileImg24);
        profileImgDao.save(profileImg25);
        profileImgDao.save(profileImg26);
        profileImgDao.save(profileImg27);
        profileImgDao.save(profileImg28);
    }
}
