package ru.xj2j.plan.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "workspace_members")
@EqualsAndHashCode(callSuper = false)
public class WorkspaceMember extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Workspace workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private User member;

    @Enumerated(STRING)
    private WorkspaceRoleType role;

    @Override
    public String toString() {
        return member.getEmail() + " <" + workspace.getSlug() + ">";
    }

}
