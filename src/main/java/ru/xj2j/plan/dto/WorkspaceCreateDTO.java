package ru.xj2j.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceCreateDTO {
    @NotNull
    @Size(
            min = 5,
            max = 40,
            message = "Name length must be between {min} and {max}."
    )
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-_]*$")
    private String name;
    @NotNull
    @Size(
            min = 5,
            max = 40,
            message = "Slug length must be between {min} and {max}."
    )
    @Pattern(regexp = "^[a-z0-9\\-_]*$")
    private String slug;
    private String description;
}
