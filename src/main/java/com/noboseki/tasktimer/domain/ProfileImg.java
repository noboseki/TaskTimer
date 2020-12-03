package com.noboseki.tasktimer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Builder
@Entity
@Table(name = "profileImg")
@NoArgsConstructor
@AllArgsConstructor
public class ProfileImg {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String urlAddress;

    @JsonIgnore
    @OneToMany(
            fetch =  FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH},
            targetEntity = User.class,
            mappedBy = "profileImg")
    private Set<User> users;

}
