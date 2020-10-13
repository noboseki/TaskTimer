package com.noboseki.tasktimer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.noboseki.tasktimer.generator.UserIdGenerator;
import lombok.*;
import org.hibernate.annotations.NaturalId;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
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
    @Builder.Default
    private Long publicId = UserIdGenerator.generateId();

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    private String imageUrl;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @NotEmpty
    @Column(nullable = false)
    private String username;

    @Builder.Default
    private Boolean emailVerified = false;

    @Builder.Default
    private Boolean accountNonExpired = true;

    @Builder.Default
    private Boolean accountNonLocked = true;

    @Builder.Default
    private Boolean credentialsNonExpired = true;

    @Builder.Default
    private Boolean enabled = true;

    @Singular
    @ManyToMany
    @JoinTable(name = "user_authority",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id"))
    private Set<Authority> authorities;

    @Singular
    @OneToMany(
            fetch =  FetchType.LAZY,
            cascade = CascadeType.ALL,
            targetEntity = Task.class,
            mappedBy = "user")
    private Set<Task> tasks;

    @Override
    public String toString() {
        return "User{" +
                "privateID=" + privateID +
                ", publicId=" + publicId +
                ", email='" + email + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", emailVerified=" + emailVerified +
                ", accountNonExpired=" + accountNonExpired +
                ", accountNonLocked=" + accountNonLocked +
                ", credentialsNonExpired=" + credentialsNonExpired +
                ", enabled=" + enabled +
                '}';
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserDto {
        @NotNull
        @JsonIgnore
        private UUID privateID;
        @NotNull
        private Long publicId;
        @Email
        @NotNull
        private String email;
        private String imageUrl;
        @NotNull
        @JsonIgnore
        private Boolean emailVerified;
        @NotNull
        private String username;
        @NotNull
        @JsonIgnore
        private String password;
        @Builder.Default
        @JsonIgnore
        private Boolean accountNonExpired = true;
        @Builder.Default
        @JsonIgnore
        private Boolean accountNonLocked = true;
        @Builder.Default
        @JsonIgnore
        private Boolean credentialsNonExpired = true;
        @Builder.Default
        @JsonIgnore
        private Boolean enabled = true;
    }
}
