package com.noboseki.tasktimer.exeption;

public enum ExceptionTextConstants {
    INSTANCE;

    public static String delete(String fieldName, String fieldValue) {
        return "Delete error of " + fieldName + " : " + fieldValue;
    }

    public static String dateTime(String typeName, String convertedString) {
        return "DateTime create error of " + typeName + " from " + convertedString;
    }

    public static String duplicate(String objectName, String fieldName, String filedValue) {
        return objectName + " with " + fieldName + " : " + filedValue + " exists in the database";
    }

    public static String invalid(String fieldName, String filedValue) {
        return "Invalid value of " + fieldName + " : " + filedValue;
    }

    public static String resourceNotFound(String objectName, String fieldName, String fieldValue) {
        return objectName + " not found by " + fieldName + " : " + fieldValue;
    }

    public static String save(String fieldName, String fieldValue) {
        return "Save error of " + fieldName + " : " + fieldValue;
    }
}
