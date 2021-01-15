package com.noboseki.tasktimer.playload;

import com.noboseki.tasktimer.domain.ProfileImg;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserServiceGetResponse {

    private Long publicId;
    private String email;
    private String username;
    private ProfileImg profileImg;
}
