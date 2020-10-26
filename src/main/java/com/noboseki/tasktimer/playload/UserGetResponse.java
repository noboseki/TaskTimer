package com.noboseki.tasktimer.playload;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserGetResponse {

    private Long publicId;
    private String email;
    private String username;
    private String imageUrl;
}
