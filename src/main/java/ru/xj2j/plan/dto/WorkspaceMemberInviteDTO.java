package ru.xj2j.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceMemberInviteDTO {
    private Long id;
    private String email;
    private String workspaceSlug;
    private String role;
    private String invitorEmail;
    private String message;
    private boolean accepted;
    private LocalDateTime respondedAt;
    private LocalDateTime createdAt;
}
