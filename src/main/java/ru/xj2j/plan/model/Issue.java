package ru.xj2j.plan.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "Issue")
@Table(name = "issues")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Issue extends AuditModel {
    public enum Priority {
        URGENT,
        HIGH,
        MEDIUM,
        LOW;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    private Project project;

    /*@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private Issue parent;*/

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", referencedColumnName = "id")
    private State state;

    @NotBlank(message = "Name is required.")
    @Column(name = "name")
    private String name;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "description", columnDefinition = "json", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private Priority priority;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "start_date")
    private LocalDateTime startDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "target_date")
    private LocalDateTime targetDate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "issue_assignees",
            joinColumns = {@JoinColumn(name = "issue_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    private List<User> assignees;

    /*@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IssueBlocker> blockerIssues;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IssueLink> links;*/

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IssueAttachment> attachments;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "completed_by_id", referencedColumnName = "id")
    private User completedBy;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

}
