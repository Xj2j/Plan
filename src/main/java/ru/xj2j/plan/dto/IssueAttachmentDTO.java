package ru.xj2j.plan.dto;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.xj2j.plan.model.Issue;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueAttachmentDTO {

    private Long id;
    private IssueDTO issue;
    private String fileName;
    private String fileType;
    private byte[] data;
}
