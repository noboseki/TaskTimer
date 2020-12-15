package com.noboseki.tasktimer.playload;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserServiceCreateRequest {

    @NotNull
    @Email
    private String email;

    @NotNull
    @Min(6)
    @Max(30)
    private String password;

    @NotNull
    @Min(6)
    @Max(14)
    private String userName;

}
