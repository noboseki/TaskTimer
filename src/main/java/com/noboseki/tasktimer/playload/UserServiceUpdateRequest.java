package com.noboseki.tasktimer.playload;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserServiceUpdateRequest {

    @Min(6)
    @Max(14)
    private String username;

    @Email
    private String email;

    @NotNull
    private String profileImgName;
}
