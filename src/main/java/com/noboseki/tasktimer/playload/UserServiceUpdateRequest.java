package com.noboseki.tasktimer.playload;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserServiceUpdateRequest {

    @Min(6) @Max(14)
    private String username;

    @Email
    private String email;

    private String profileImgName;
}
