package com.noboseki.tasktimer.playload;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GetSessionChainByDateResponse {

    private List<String> dateLabel;
    private List<GetSessionChainDataForTask> dataList;
}
