package ru.xj2j.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueDTO {
    private Long id;
    private WorkspaceDTO workspace;
    private String state;
    private String priority;
    private String name;
    private String description;
    private UserDTO createdBy;
    private LocalDateTime startDate;
    private LocalDateTime targetDate;
    private LocalDateTime completedAt;
    private UserDTO completedBy;
    private Set<UserDTO> assignees;
    private List<CommentDTO> comments;
}
