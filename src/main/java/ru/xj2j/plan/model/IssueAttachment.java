package ru.xj2j.plan.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "issue_attachments")
@Data
@EqualsAndHashCode(callSuper = false)
public class IssueAttachment extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Issue issue;

    private String fileName;

    private String fileType;

    @Column(columnDefinition = "bytea")
    private byte[] data;
}
