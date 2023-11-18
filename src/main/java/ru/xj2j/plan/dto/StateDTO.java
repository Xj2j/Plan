package ru.xj2j.plan.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StateDTO {
    public enum Group {
        BACKLOG,
        UNSTARTED,
        STARTED,
        COMPLETED,
        CANCELLED
    }
    private Long id;
    private String name;
    private String description;
    private String color;
    @Enumerated(EnumType.STRING)
    @Column(name = "group_name", length = 20)
    private StateDTO.Group group = StateDTO.Group.BACKLOG;
}
