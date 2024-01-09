package ru.xj2j.plan.model;

import lombok.Data;

import java.util.Set;


public interface Role {
    boolean includes(Role role);

    static Set<Role> roots() {
        return Set.of(WorkspaceRoleType.OWNER);
    }
}