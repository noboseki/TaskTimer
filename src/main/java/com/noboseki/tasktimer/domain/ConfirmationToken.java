package com.noboseki.tasktimer.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@ToString
@Getter
@Entity
@NoArgsConstructor
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="token_id")
    private UUID  id;

    private String confirmationToken;

    private Timestamp timestamp;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public ConfirmationToken(User user) {
        this.user = user;
        timestamp = new Timestamp(System.currentTimeMillis());
        confirmationToken = UUID.randomUUID().toString();
    }
}
