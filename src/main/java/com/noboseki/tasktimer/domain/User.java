package com.noboseki.tasktimer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.noboseki.tasktimer.generator.UserIdGenerator;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "user")
@NoArgsConstructor
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
    private Boolean emailVerified = false;

    @JsonIgnore
    private String password;

    @Data
    @Builder
    public static class UserDto {
        private UUID privateID;
        private Long publicId;
        private String email;
        private String imageUrl;
        private Boolean emailVerified;
        private String password;
    }
}
