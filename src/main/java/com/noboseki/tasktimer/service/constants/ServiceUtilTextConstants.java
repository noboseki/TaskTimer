package com.noboseki.tasktimer.service.constants;

public enum ServiceUtilTextConstants {

    INSTANCE;

    private static final String COMPLETE_REGISTRATION = "Complete Registration!";
    private static final String CHANGE_PASSWORD = "Change Password";
    private static final String ZERO_TIME = "00:00";

    public static String getCompleteRegistration() {
        return COMPLETE_REGISTRATION;
    }

    public static String getChangePassword() {
        return CHANGE_PASSWORD;
    }

    public static String getZeroTime() {
        return ZERO_TIME;
    }

    public static String activationEmailMessage(String url, String token) {
        return "To confirm your account, please click here : " + url + token;
    }

    public static String changePasswordEmailMessage(String url, String token) {
        return "To change password, please click here : " + url + token;
    }
}
