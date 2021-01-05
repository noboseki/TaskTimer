package com.noboseki.tasktimer.service.util.UserService;

import com.noboseki.tasktimer.domain.Authority;
import com.noboseki.tasktimer.domain.ProfileImg;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.playload.UserServiceCreateRequest;
import com.noboseki.tasktimer.playload.UserServiceGetResponse;
import com.noboseki.tasktimer.service.EmailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserServiceUtil {

    private final PasswordEncoder passwordEncoder;
    private final EmailSenderService emailSenderService;

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
                .username(request.getUserName())
                .password(passwordEncoder.encode(request.getPassword()))
                .authority(authority)
                .profileImg(profileImg).build();
    }

    public boolean activationEmileSender(String token, String emile) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(emile);
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom("nobosekiemiletest@gmail.com");
        mailMessage.setText("To confirm your account, please click here : "
                +"http://localhost:8080/confirm/confirm-account?token=" + token);

        emailSenderService.sendEmail(mailMessage);

        return true;
    }

    public boolean changePasswordEmileSender(String token, String emile) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(emile);
        mailMessage.setSubject("Change Password");
        mailMessage.setFrom("nobosekiemiletest@gmail.com");
        mailMessage.setText("To change password, please click here : "
                +"http://localhost:8080/confirm/change-password?token=" + token);

        emailSenderService.sendEmail(mailMessage);

        return true;
    }
}
