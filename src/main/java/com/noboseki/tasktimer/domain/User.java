package com.noboseki.tasktimer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.noboseki.tasktimer.generator.UserIdGenerator;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private UUID privateID;

    @NaturalId
    private Long publicId = UserIdGenerator.generateId();

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    private String imageUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean emailVerified = false;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Singular
    @OneToMany(
            fetch =  FetchType.LAZY,
            cascade = CascadeType.ALL,
            targetEntity = Task.class,
            mappedBy = "user")
    private Set<Task> tasks;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserDto {
        @NotNull
        private UUID privateID;
        @NotNull
        private Long publicId;
        @Email
        @NotNull
        private String email;
        private String imageUrl;
        @NotNull
        private Boolean emailVerified;
        @NotNull
        private String password;
    }
}
