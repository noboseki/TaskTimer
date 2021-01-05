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
    @Column(name = "token_id")
    private UUID id;

    @Column(nullable = false)
    private String confirmationToken;

    @Column(nullable = false)
    private TokenType type;

    @Column(nullable = false)
    private Timestamp timestamp;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public ConfirmationToken(User user, TokenType type) {
        this.type = type;
        this.user = user;
        timestamp = new Timestamp(System.currentTimeMillis());
        confirmationToken = UUID.randomUUID().toString();
    }
}
