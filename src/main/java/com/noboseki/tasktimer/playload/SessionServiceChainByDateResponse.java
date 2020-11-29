package com.noboseki.tasktimer.playload;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionServiceChainByDateResponse {

    private List<String> dateLabel;
    private List<TaskDataForSessionChain> dataList;
}
