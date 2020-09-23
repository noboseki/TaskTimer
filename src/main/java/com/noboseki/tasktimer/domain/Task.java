package com.noboseki.tasktimer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@Entity
@Table(name = "task")
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private UUID privateID;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean complete = false;

    public Task mapToEntity(TaskDto dto) {
        return Task.builder()
                .privateID(dto.privateID)
                .name(dto.name)
                .complete(dto.complete).build();
    }

    @Data
    @Builder
    public static class TaskDto {
        private UUID privateID;
        private String name;
        private Boolean complete;

        public TaskDto mapToDto(Task task) {
            return TaskDto.builder()
                    .privateID(task.privateID)
                    .name(task.name)
                    .complete(task.complete).build();
        }

        public List<TaskDto> mapToDtos(List<Task> tasks) {
            return tasks.stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());
        }
    }
}
