package ru.xj2j.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceDTO {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private UserDTO createdBy;
    @Builder.Default
    private List<WorkspaceMemberDTO> members = new ArrayList<>();
}
