package com.noboseki.tasktimer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.sql.Time;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "session")
@NoArgsConstructor
@AllArgsConstructor
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private UUID id;

    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private Time time;

    @NotNull
    @ManyToOne(
            fetch = FetchType.EAGER,
            cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH },
            targetEntity = Task.class)
    @JoinColumn(name = "task_Id")
    private Task task;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SessionDto {
        @NotNull
        private UUID privateID;
        @NotNull
        private Date date;
        @NotNull
        private Time time;

        @Override
        public String toString() {
            return "WorkTimeDto{" +
                    "privateID=" + privateID +
                    ", date=" + date.toString() +
                    ", time=" + time.toString() +
                    '}';
        }
    }
}
