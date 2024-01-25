package ru.xj2j.plan.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "workspace_member_invites")
@EntityListeners(AuditingEntityListener.class)
public class WorkspaceMemberInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", referencedColumnName = "id", nullable = false)
    private Workspace workspace;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "role", nullable = false)
    @Enumerated(STRING)
    private WorkspaceRoleType role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User invitor;

    @Column(name = "message")
    private String message;

    @Column(name = "accepted")
    private boolean isAssepted;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
