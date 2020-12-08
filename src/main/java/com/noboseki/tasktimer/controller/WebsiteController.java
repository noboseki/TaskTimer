package com.noboseki.tasktimer.controller;

import com.noboseki.tasktimer.security.perms.UserPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class WebsiteController {

    @UserPermission
    @GetMapping("")
    public String mainPage() {
        return "main";
    }

    @GetMapping("login")
    public String loginPage() {
        return "login";
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

    @UserPermission
    @GetMapping("contact")
    public String contactPage() {
        return "contact";
    }
}
