package ru.xj2j.plan.model;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

public enum WorkspaceRoleType implements Role {
    OWNER, ADMIN, MEMBER;

    private final Set<Role> children = new HashSet<>();

    static {
        OWNER.children.add(ADMIN);
        ADMIN.children.add(MEMBER);
    }

    @Component("WorkspaceRole")
    @Getter
    static class SpringComponent {
        private final WorkspaceRoleType OWNER = WorkspaceRoleType.OWNER;
        private final WorkspaceRoleType ADMIN = WorkspaceRoleType.ADMIN;
        private final WorkspaceRoleType MEMBER = WorkspaceRoleType.MEMBER;
    }

    @Override
    public boolean includes(Role role) {
        return this.equals(role) || children.stream().anyMatch(r -> r.includes(role));
    }
}
