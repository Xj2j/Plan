package ru.xj2j.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static ru.xj2j.plan.util.RolePattern.ROLE_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateInviteRequest {
    @NotNull
    private String email;
    @NotNull
    @Pattern(regexp = ROLE_PATTERN, message = "Invalid Role")
    private String role;
    private String message;
}
