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
@EqualsAndHashCode(callSuper = false)
public class IssueComment extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comment", nullable = false)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;

    /*@ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "actor_id", nullable = true)
    private User actor;*/

    @Override
    public String toString() {
        return "IssueComment{" +
                "id=" + id +
                ", comment='" + comment + '\'' +
                ", issue=" + issue +
                ", createdBy=" + getCreatedBy() +
                '}';
    }

}
