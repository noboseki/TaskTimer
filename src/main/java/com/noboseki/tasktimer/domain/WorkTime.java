package com.noboseki.tasktimer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@Entity
@Table(name = "work_time")
@NoArgsConstructor
public class WorkTime {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private UUID privateID;

    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private Time time;

    public WorkTime mapToEntity(WorkTimeDto dto) {
        return WorkTime.builder()
                .privateID(dto.privateID)
                .date(dto.date)
                .time(dto.time).build();
    }

    @Data
    @Builder
    public static class WorkTimeDto {
        private UUID privateID;
        private Date date;
        private Time time;

        public WorkTimeDto mapToDto(WorkTime workTime) {
            return WorkTimeDto.builder()
                    .privateID(workTime.privateID)
                    .date(workTime.date)
                    .time(workTime.time).build();
        }

        public List<WorkTimeDto> mapToDtos(List<WorkTime> tasks) {
            return tasks.stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());
        }
    }
}
