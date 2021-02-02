package com.noboseki.tasktimer.service.util.session_service;

import com.noboseki.tasktimer.domain.Session;
import com.noboseki.tasktimer.playload.SessionServiceChainByDateResponse;
import com.noboseki.tasktimer.service.UnitTestUtil;
import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SessionServiceGetBarChainByDateUtilTest {
    private final SessionServiceGetBarChainByDateUtil util = new SessionServiceGetBarChainByDateUtil();
    private final UnitTestUtil unitTestUtil = new UnitTestUtil();

    @Test
    void fillBarChainByDate() {
        List<Session> sessions = unitTestUtil.getDefaultSessionList();

        SessionServiceChainByDateResponse response = util.fillBarChainByDate(
                sessions,
                LocalDate.parse("2020-10-20"),
                LocalDate.parse("2020-11-01"));

        assertEquals(response.getDataList().size(), 3);
        assertEquals(response.getDateLabel().size(), 13);
        assertEquals(response.getDateLabel().get(0), "2020-10-20");
        assertEquals(response.getDateLabel().get(response.getDateLabel().size() - 1),
                "2020-11-01");
    }

    @Test
    void createDateLabel() {
        LocalDate from = LocalDate.parse("2020-01-10");
        LocalDate to = LocalDate.parse("2020-01-31");

        List<String> dateLabels = util.createDateLabel(from, to);

        assertEquals(dateLabels.size(), 22);
    }

    @Test
    void getTaskNamesFromList() {
        List<Session> sessions = unitTestUtil.getDefaultSessionList();
        Set<String> taskNames = util.getTaskNamesFromList(sessions);
        assertEquals(taskNames.size(), 3);
    }

    @Test
    void extractSessionsTimeByDateAndTaskName() {
        List<Session> sessions = unitTestUtil.getDefaultSessionList();

        List<Time> times = util.extractSessionsTimeByDateAndTaskName
                (sessions, "task name A", LocalDate.parse("2020-10-20"));

        assertEquals(times.size(), 2);
        assertEquals(times.get(0).toString(), "01:15:00");
        assertEquals(times.get(1).toString(), "04:04:00");
    }

    @Test
    void listToLongTime() {
        List<Time> empty = new ArrayList<>();
        List<Time> filed = new ArrayList<>();

        filed.add(Time.valueOf("01:48:00"));
        filed.add(Time.valueOf("10:14:00"));
        filed.add(Time.valueOf("04:32:00"));

        float emptyFloat = util.listToLongTime(empty);
        float filedFloat = util.listToLongTime(filed);

        assertEquals(emptyFloat, 0);
        assertEquals(Float.valueOf(16.57f), Float.valueOf(filedFloat));
    }
}