package ru.xj2j.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueUpdateDTO {
    private String state;
    private String priority;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime targetDate;
    //private LocalDateTime completedAt;
    //private UserDTO completedBy;
}
