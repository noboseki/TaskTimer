package com.noboseki.tasktimer.controller;

import com.noboseki.tasktimer.service.ConfirmationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("confirm")
@RequiredArgsConstructor
public class ConfirmationTokenController {

    private final ConfirmationTokenService tokenService;

    @GetMapping("confirm-account")
    public ModelAndView confirmUserAccount(@RequestParam("token") String confirmationToken) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("activationResponse");
        modelAndView.addObject("message", tokenService.activateAccount(confirmationToken));

        return modelAndView;
    }
}
