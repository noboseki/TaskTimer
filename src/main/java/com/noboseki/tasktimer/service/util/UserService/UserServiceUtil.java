package com.noboseki.tasktimer.service.util.UserService;

import com.noboseki.tasktimer.config.PropertiesConstants;
import com.noboseki.tasktimer.domain.Authority;
import com.noboseki.tasktimer.domain.ProfileImg;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.playload.UserServiceCreateRequest;
import com.noboseki.tasktimer.playload.UserServiceGetResponse;
import com.noboseki.tasktimer.service.EmailSenderService;
import com.noboseki.tasktimer.service.constants.ServiceUtilTextConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserServiceUtil {
    private static final String PASSWORD_URL = "http://localhost:8080/confirm/change-password?token=";
    private static final String ACTIVATION_URL = "http://localhost:8080/confirm/confirm-account?token=";

    private final PasswordEncoder passwordEncoder;
    private final EmailSenderService emailSenderService;
    private final PropertiesConstants constants;

    public UserServiceGetResponse mapToResponse(User user) {
        return UserServiceGetResponse.builder()
                .publicId(user.getPublicId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profileImg(user.getProfileImg()).build();
    }

    public User mapToUser(UserServiceCreateRequest request, Authority authority, ProfileImg profileImg) {
        return User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .authority(authority)
                .profileImg(profileImg).build();
    }

    public boolean activationEmileSender(String token, String emile) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(emile);
        mailMessage.setSubject(ServiceUtilTextConstants.getCompleteRegistration());
        mailMessage.setFrom(constants.getEmail());
        mailMessage.setText(ServiceUtilTextConstants
                .activationEmailMessage(ACTIVATION_URL, token));

        emailSenderService.sendEmail(mailMessage);

        return true;
    }

    public boolean changePasswordEmileSender(String token, String emile) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(emile);
        mailMessage.setSubject(ServiceUtilTextConstants.getChangePassword());
        mailMessage.setFrom(constants.getEmail());
        mailMessage.setText(ServiceUtilTextConstants
                .changePasswordEmailMessage(PASSWORD_URL, token));

        emailSenderService.sendEmail(mailMessage);

        return true;
    }
}
