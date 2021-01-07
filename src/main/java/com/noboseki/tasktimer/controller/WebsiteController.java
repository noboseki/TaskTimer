package com.noboseki.tasktimer.controller;

import com.noboseki.tasktimer.security.perms.UserPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class WebsiteController {

    @GetMapping("create")
    public String createPage() {
        return "create";
    }

    @GetMapping("login")
    public String loginPage() {
        return "login";
    }

    @UserPermission
    @GetMapping("")
    public String mainPage() {
        return "main";
    }

    @UserPermission
    @GetMapping("profile")
    public String profilePage() {
        return "profile";
    }

    @UserPermission
    @GetMapping("statistic")
    public String statisticPage() {
        return "statistic";
    }

    @GetMapping("confirm/change-password")
    public String changePassword(@RequestParam("token") String confirmationToken) {
        return "changePassword";
    }

    @GetMapping("forgot-password")
    public String forgotPassword() {
        return "forgotPassword";
    }
}
