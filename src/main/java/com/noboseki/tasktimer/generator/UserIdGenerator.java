package com.noboseki.tasktimer.generator;

public abstract class UserIdGenerator {
    private static Long id = 10000000L;

    public static Long generateId() {
        return id++;
    }
}
