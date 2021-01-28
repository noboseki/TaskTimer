package com.noboseki.tasktimer.exeption;

public enum ExceptionTextConstants {
    INSTANCE;

    public static String delete(String objectName, String fieldValue) {
        return "Delete error of " + objectName + " : " + fieldValue;
    }

    public static String dateTime(String typeName, String convertedString) {
        return "DateTime create error of " + typeName + " from " + convertedString;
    }

    public static String duplicate(String objectName, String filedValue) {
        return "Duplicate error of" + objectName + " : " + filedValue;
    }

    public static String invalid(String fieldName, String filedValue) {
        return "Invalid value of " + fieldName + " : " + filedValue;
    }

    public static String resourceNotFound(String objectName, String fieldValue) {
        return objectName + " not found by : " + fieldValue;
    }

    public static String save(String fieldName, String fieldValue) {
        return "Save error of " + fieldName + " : " + fieldValue;
    }
}
