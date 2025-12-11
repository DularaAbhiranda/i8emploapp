package com.innov8.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_action", columnList = "action"),
    @Index(name = "idx_timestamp", columnList = "timestamp"),
    @Index(name = "idx_user", columnList = "username")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action; // CREATE, READ, UPDATE, DELETE, LOGIN, LOGOUT

    @Column(nullable = false)
    private String entityType; // Personnel, Admin, etc.

    @Column
    private Long entityId; // ID of the affected entity

    @Column(nullable = false)
    private String username;

    @Column(length = 50)
    private String ipAddress;

    @Column
    private String details; // Additional details about the action

    @Column
    private Integer responseStatus; // HTTP status code

    @Column
    private Long executionTime; // Time taken in milliseconds

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column
    private String userAgent;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
