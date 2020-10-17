package com.noboseki.tasktimer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.noboseki.tasktimer.generator.UserIdGenerator;
import lombok.*;
import org.hibernate.annotations.NaturalId;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@Entity
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails, CredentialsContainer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private UUID id;

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

    @Singular
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "user_authority",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID")})
    private Set<Authority> authorities;

    @Singular
    @OneToMany(
            fetch =  FetchType.LAZY,
            cascade = CascadeType.ALL,
            targetEntity = Task.class,
            mappedBy = "user")
    private Set<Task> tasks;

    @Transient
    public Set<GrantedAuthority> getAuthorities() {
        return authorities.stream()
                .map(authority -> {
                    return new SimpleGrantedAuthority(authority.getRole());
                }).collect(Collectors.toSet());
    }

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

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
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
