package ru.xj2j.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateInviteRequestDTO {
    @NotNull
    private String email;
    @NotNull
    private String workspaceSlug;
    @NotNull
    private String role;
    private String message;
    /*@NotNull
    private String invitorEmail;*/
}
