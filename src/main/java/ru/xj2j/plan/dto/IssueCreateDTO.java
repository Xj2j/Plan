package ru.xj2j.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueCreateDTO {
    @Pattern(regexp = "BACKLOG|UNSTARTED|STARTED|COMPLETED|CANCELLED", message = "Invalid state")
    private String state;
    @Pattern(regexp = "URGENT|HIGH|MEDIUM|LOW", message = "Invalid priority")
    private String priority;
    @NotNull
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime targetDate;
    @Builder.Default
    private Set<UserDTO> assignees = new HashSet<>();
}
