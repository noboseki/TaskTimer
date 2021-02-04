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
public class User implements CredentialsContainer {

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

    @ManyToOne(
            fetch = FetchType.EAGER,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH},
            targetEntity = ProfileImg.class)
    @JoinColumn(name = "profileImg_Id")
    private ProfileImg profileImg;

    @Singular
    @OneToMany(
            orphanRemoval = true,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            targetEntity = Task.class,
            mappedBy = "user")
    private Set<Task> tasks;

    @Builder.Default
    private Boolean enabled = false;

    @Override
    public void eraseCredentials() {
        this.password = null;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", publicId=" + publicId +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
