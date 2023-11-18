package ru.xj2j.plan.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.xj2j.plan.model.Project;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    public enum Network {
        SECRET, PUBLIC
    }

    public enum Role {
        ADMIN, MEMBER, VIEWER, GUEST
    }

    private Long id;
    private String identifier;
    private Project.Network network;
    private String name;
    private String description;
    private WorkspaceDTO workspace;
    private UserDTO projectLead;

}
