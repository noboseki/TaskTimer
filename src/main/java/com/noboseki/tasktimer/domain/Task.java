package com.noboseki.tasktimer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "task")
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private UUID privateID;

    @Column(nullable = false)
    @Builder.Default
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private Boolean complete = false;

    @NotNull
    @ManyToOne(
            fetch = FetchType.EAGER,
            cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH },
            targetEntity = User.class)
    @JoinColumn(name = "user_Id")
    private User user;

    @Singular
    @OneToMany(
            fetch =  FetchType.LAZY,
            cascade = CascadeType.ALL,
            targetEntity = WorkTime.class,
            mappedBy = "task")
    private Set<WorkTime> workTimes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskDto {
        @NotNull
        private UUID privateID;
        @NotNull
        private String name;
        @NotNull
        private Boolean complete;
    }
}
