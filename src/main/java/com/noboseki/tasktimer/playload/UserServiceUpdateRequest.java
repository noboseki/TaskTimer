package com.noboseki.tasktimer.playload;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserServiceUpdateRequest {

    @Size(max = 16, min = 6)
    private String username;

    @Email(regexp = ".+@.+\\..+")
    private String email;

    @NotNull
    private String profileImgName;
}
