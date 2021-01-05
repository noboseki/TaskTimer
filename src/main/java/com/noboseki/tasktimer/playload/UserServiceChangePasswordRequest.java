package com.noboseki.tasktimer.playload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserServiceChangePasswordRequest {

    @NotNull
    private String token;
    @NotNull
    private String oldPassword;
    @NotNull
    @Min(6)
    @Max(30)
    private String newPassword;
}
