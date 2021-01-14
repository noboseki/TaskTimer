package com.noboseki.tasktimer.playload;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserServiceCreateRequest {

    @Email(regexp = ".+@.+\\..+")
    private String email;

    @Size(min = 6, max = 30)
    private String password;

    @Size(min = 6, max = 14)
    private String username;

}
