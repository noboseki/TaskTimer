package com.noboseki.tasktimer.service.util.session_service;

import com.noboseki.tasktimer.domain.Session;
import com.noboseki.tasktimer.playload.SessionServiceTableByDateResponse;
import com.noboseki.tasktimer.service.UnitTestUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SessionServiceGetTableByDateUtilTest {
    SessionServiceGetTableByDateUtil util = new SessionServiceGetTableByDateUtil();
    UnitTestUtil unitTestUtil = new UnitTestUtil();

    @Test
    void fillEmptyResponseList() {
        List<SessionServiceTableByDateResponse> responseList =
                util.fillEmptyResponseList(LocalDate.parse("2020-10-01"), LocalDate.parse("2020-10-10"));

        assertEquals(responseList.size(), 10);
        assertEquals(responseList.get(0).getDate().toString(), "2020-10-01");
        assertEquals(responseList.get(0).getTime(), "00:00");
        assertEquals(responseList.get(0).getSessions(), 0);
    }

    @Test
    void fillResponseList() {
        List<Session> sessions = unitTestUtil.getDefaultSessionList();

        List<SessionServiceTableByDateResponse> response =
                util.fillResponseList(sessions,
                        LocalDate.parse("2020-10-20"),
                        LocalDate.parse("2020-11-01"));

        assertEquals(response.size(), 13);
        assertEquals(response.get(0).getDate(), LocalDate.parse("2020-10-20"));
        assertEquals(response.get(0).getTime(), "07:50");
        assertEquals(response.get(0).getSessions(), 3);
    }

    @Test
    void extractSessionsByDateAndRemove() {
        List<Session> requestSessions = unitTestUtil.getDefaultSessionList();
        List<Session> sessions = util.extractSessionsByDateAndRemove(
                requestSessions,
                LocalDate.parse("2020-10-20"));

        assertEquals(sessions.size(), 3);
        assertEquals(requestSessions.size(), 2);
    }

    @Test
    void fillingResponseListByDate() {
        List<SessionServiceTableByDateResponse> responseList = new ArrayList<>();
        List<SessionServiceTableByDateResponse> responseListEmpty = new ArrayList<>();
        util.fillingResponseListByDate(unitTestUtil.getDefaultSessionList(), responseList, LocalDate.parse("2020-10-20"));
        util.fillingResponseListByDate(new ArrayList<>(), responseListEmpty, LocalDate.parse("2020-10-20"));

        assertEquals(responseList.size(), 1);
        assertEquals(responseList.get(0).getSessions(), 5);
        assertEquals(responseList.get(0).getDate(), LocalDate.parse("2020-10-20"));
        assertEquals(responseList.get(0).getTime(), "11:15");

        assertEquals(responseListEmpty.size(), 1);
        assertEquals(responseListEmpty.get(0).getSessions(), 0);
        assertEquals(responseListEmpty.get(0).getDate(), LocalDate.parse("2020-10-20"));
        assertEquals(responseListEmpty.get(0).getTime(), "00:00");
    }
}