package ru.xj2j.plan.model;

import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.FetchType.LAZY;

@Data
@Builder
@Entity(name = "Issue")
@Table(name = "issues")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Issue extends AuditModel {
    public enum Priority {
        URGENT("Urgent"),
        HIGH("High"),
        MEDIUM("Medium"),
        LOW("Low");

        private final String label;

        Priority(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public enum State {
        BACKLOG("Backlog"),
        UNSTARTED("Unstarted"),
        STARTED("Started"),
        COMPLETED("Completed"),
        CANCELLED("Cancelled");

        private final String label;

        State(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", referencedColumnName = "id", nullable = false)
    private Workspace workspace;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private State state = State.BACKLOG;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private Priority priority = Priority.MEDIUM;

    @NotBlank(message = "Name is required.")
    @Column(name = "name")
    private String name;

    @Lob @Basic(fetch = FetchType.LAZY)
    @Column(name = "description", columnDefinition = "TEXT") //, columnDefinition = "json" //columnDefinition="LONGTEXT" //, length = 1024
    private String description;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "target_date")
    private LocalDateTime targetDate;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "completed_by_id", referencedColumnName = "id")
    private User completedBy;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "issue_assignees",
            joinColumns = {@JoinColumn(name = "issue_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    private Set<User> assignees = new HashSet<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IssueComment> comments = new ArrayList<>();

    public void addComment(IssueComment comment) {
        comments.add(comment);
        comment.setIssue(this);
    }
    public void removeComment(IssueComment comment) {
        comments.remove(comment);
        comment.setIssue(null);
    }

}
