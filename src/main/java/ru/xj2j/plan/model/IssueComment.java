package ru.xj2j.plan.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "issue_comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class IssueComment extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comment_stripped", nullable = false)
    private String commentStripped = "";

    /*@Column(name = "comment_json", columnDefinition = "jsonb not null default '{}'")
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> commentJson = new HashMap<>();*/

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "issue_comment_attachments", joinColumns = @JoinColumn(name = "comment_id"))
    @Column(name = "attachment_url", nullable = false)
    private List<String> attachments = new ArrayList<>();

    /**@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Project project;*/

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;

    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "actor_id", nullable = true)
    private User actor;

    @Override
    public String toString() {
        return String.valueOf(issue);
    }

}
