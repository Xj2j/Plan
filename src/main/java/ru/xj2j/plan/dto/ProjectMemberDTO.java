package ru.xj2j.plan.dto;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.xj2j.plan.model.Project;
import ru.xj2j.plan.model.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberDTO {

    private Long id;
    private UserDTO member;
    private String role;
    private ProjectDTO project;
}
