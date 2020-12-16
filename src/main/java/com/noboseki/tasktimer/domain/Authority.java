package com.noboseki.tasktimer.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@Entity
@Table(name = "authority")
@NoArgsConstructor
@AllArgsConstructor
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String role;

    @Singular
    @ManyToMany(mappedBy = "authorities")
    private Set<User> users;
}
