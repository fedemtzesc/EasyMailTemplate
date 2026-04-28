package com.fdxsoft.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "user_login_ip")
public class UserLoginIpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK hacia e_user (sin relación objeto para evitar referencias circulares)
    @Column(name = "fk_user_id", nullable = false)
    private Long userId;

    // Soporta IPv4 e IPv6
    @Column(length = 45, nullable = false)
    private String ip;

    // Primera vez detectada
    @Column(nullable = false)
    private LocalDateTime firstSeen;

    // Última vez usada
    @Column(nullable = false)
    private LocalDateTime lastSeen;

    // Cantidad de veces vista
    @Column(nullable = false)
    private Long timesUsed;
}