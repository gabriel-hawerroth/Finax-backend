package br.finax.finax.models;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "access_log")
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "login_dt")
    private LocalDateTime loginDt;

    public AccessLog(Long userId, LocalDateTime loginDt) {
        this.userId = userId;
        this.loginDt = loginDt;
    }
}
