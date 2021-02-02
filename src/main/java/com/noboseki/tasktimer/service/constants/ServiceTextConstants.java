package com.noboseki.tasktimer.service.constants;

public enum ServiceTextConstants {
    INSTANCE;

    private static final String TOKEN = "Token";
    private static final String USER = "User";
    private static final String TASK = "Task";
    private static final String PROFILE_IMG = "Profile img";
    private static final String AUTHORITY = "Authority";
    private static final String STANDARD_AVATAR_NAME = "Yondu";
    private static final String ACTIVATE_ACCOUNT = "Congratulations! Your account has been activated and email is verified!";

    public static String hasBeenCreate(String object) {
        return object + " has been created";
    }

    public static String hasBeenUpdated(String object) {
        return object + " has been updated";
    }

    public static String emailHasBeenSend(String email) {
        return "Email has been sent to : " + email;
    }

    public static String hasBeenDeleted(String object) {
        return object + " has been deleted";
    }

    public static String getToken() {
        return TOKEN;
    }

    public static String getUser() {
        return USER;
    }

    public static String getTask() {
        return TASK;
    }

    public static String getProfileImg() {
        return PROFILE_IMG;
    }

    public static String getAuthority() {
        return AUTHORITY;
    }

    public static String getStandardAvatarName() {
        return STANDARD_AVATAR_NAME;
    }

    public static String getActivateAccount() {
        return ACTIVATE_ACCOUNT;
    }
}