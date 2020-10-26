package com.noboseki.tasktimer.playload;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {

    @NotNull
    @Email
    private String email;

    @NotNull
    @Min(10)
    private String password;

    @NotNull
    @Max(14) @Min(6)
    private String userName;

    @NotNull
    private String imageUrl;
}
