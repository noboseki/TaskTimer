//package com.noboseki.tasktimer.exeption;
//
//import lombok.Getter;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.ResponseStatus;
//
//@Getter
//@ResponseStatus(HttpStatus.FORBIDDEN)
//public class ForbiddenException extends RuntimeException {
//    private String objectName;
//    private String authorityName;
//    private String actionName;
//
//    public ForbiddenException(String objectName, String authorityName, String actionName) {
//        super(String.format("%s with %s authorities cannot be '%s'", objectName, authorityName, actionName));
//        this.objectName = objectName;
//        this.authorityName = authorityName;
//        this.actionName = actionName;
//    }
//}
