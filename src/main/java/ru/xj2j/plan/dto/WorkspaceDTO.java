package ru.xj2j.plan.dto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.xj2j.plan.model.WorkspaceMember;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceDTO {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private UserDTO createdBy;
    private List<WorkspaceMemberDTO> members = new ArrayList<>();
}
