package com.noboseki.tasktimer.playload;

import com.noboseki.tasktimer.domain.ProfileImg;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserServiceGetResponse {

    private Long publicId;
    private String email;
    private String username;
    private ProfileImg profileImg;
    private Sex sex;
    private List<UserServiceGetTaskList> taskList;
}
