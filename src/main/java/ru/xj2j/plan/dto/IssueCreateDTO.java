package ru.xj2j.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueCreateDTO {
    private String state;
    @NotNull
    private String priority;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime targetDate;
    private Set<UserDTO> assignees;
}
