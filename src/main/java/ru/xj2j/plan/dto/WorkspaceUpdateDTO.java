package ru.xj2j.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceUpdateDTO {
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-_]*$")
    private String name;
    @Pattern(regexp = "^[a-z0-9\\-_]*$")
    private String slug;
    private String description;
}
