package ru.xj2j.plan.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.xj2j.plan.model.User;
import ru.xj2j.plan.model.Workspace;
import ru.xj2j.plan.model.WorkspaceMember;
import ru.xj2j.plan.model.WorkspaceRoleType;

import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class WorkspaceRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    WorkspaceRepository workspaceRepository;

    @Test
    @DisplayName("findAllByMembers_Member_Id should return empty list when user does not exist")
    void findAllByMembers_Member_Id_ShouldReturnEmptyListWhenUserDoesNotExist() {
        List<Workspace> actualWorkspaces  = workspaceRepository.findAllByMembers_Member_Id(1L);

        assertThat(actualWorkspaces).isEmpty();
    }

    @Test
    @DisplayName("findAllByMembers_Member_Id should return empty list when user has no workspaces")
    void findAllByMembers_Member_Id_ShouldReturnEmptyListWhenUserHasNoWorkspaces() {
        User user = new User();
        user.setEmail("test@email.com");
        user.setPassword("testpass");
        testEntityManager.persistAndFlush(user);

        List<Workspace> actualWorkspaces = workspaceRepository.findAllByMembers_Member_Id(1L);

        assertThat(actualWorkspaces).isEmpty();
    }

    @Test
    @DisplayName("findAllByMembers_Member_Id should return workspaces for user")
    void findAllByMembers_Member_Id_ShouldReturnWorkspacesForUser() {
        User user = new User();
        user.setEmail("test@email.com");
        user.setPassword("testpass");
        testEntityManager.persistAndFlush(user);

        Workspace workspace1 = new Workspace();
        workspace1.setName("test workspace 1");
        workspace1.setSlug("test-workspace-1");
        workspace1.setCreatedAt(LocalDateTime.now());
        testEntityManager.persistAndFlush(workspace1);

        Workspace workspace2 = new Workspace();
        workspace2.setName("test workspace 2");
        workspace2.setSlug("test-workspace-2");
        workspace2.setCreatedAt(LocalDateTime.now());
        testEntityManager.persistAndFlush(workspace2);

        WorkspaceMember workspaceMember1 = new WorkspaceMember();
        workspaceMember1.setWorkspace(workspace1);
        workspaceMember1.setMember(user);
        workspaceMember1.setRole(WorkspaceRoleType.OWNER);
        workspaceMember1.setCreatedAt(LocalDateTime.now());
        testEntityManager.persistAndFlush(workspaceMember1);

        WorkspaceMember workspaceMember2 = new WorkspaceMember();
        workspaceMember2.setWorkspace(workspace2);
        workspaceMember2.setMember(user);
        workspaceMember2.setRole(WorkspaceRoleType.MEMBER);
        workspaceMember2.setCreatedAt(LocalDateTime.now());
        testEntityManager.persistAndFlush(workspaceMember2);

        List<Workspace> expectedWorkspaces = Arrays.asList(workspace1, workspace2);

        List<Workspace> actualWorkspaces  = workspaceRepository.findAllByMembers_Member_Id(1L);
        actualWorkspaces.forEach(workspace -> workspace.setId(null));

        assertThat(actualWorkspaces).hasSize(2);
        assertThat(expectedWorkspaces).containsExactlyInAnyOrderElementsOf(actualWorkspaces);
    }

    @Test
    @DisplayName("findAllByMembers_Member_Id should return correct list when user has multiple workspaces")
    void findAllByMembers_Member_Id_ShouldReturnCorrectListWhenUserHasMultipleWorkspaces() {
        User user1 = new User();
        user1.setEmail("test1@email.com");
        user1.setPassword("testpass1");
        testEntityManager.persistAndFlush(user1);

        User user2 = new User();
        user2.setEmail("test2@email.com");
        user2.setPassword("testpass2");
        testEntityManager.persistAndFlush(user2);

        Workspace workspace1 = new Workspace();
        workspace1.setName("test workspace 1");
        workspace1.setSlug("test-workspace-1");
        workspace1.setCreatedAt(LocalDateTime.now());
        testEntityManager.persistAndFlush(workspace1);

        Workspace workspace2 = new Workspace();
        workspace2.setName("test workspace 2");
        workspace2.setSlug("test-workspace-2");
        workspace2.setCreatedAt(LocalDateTime.now());
        testEntityManager.persistAndFlush(workspace2);

        WorkspaceMember workspaceMember1 = new WorkspaceMember();
        workspaceMember1.setWorkspace(workspace1);
        workspaceMember1.setMember(user1);
        workspaceMember1.setRole(WorkspaceRoleType.OWNER);
        workspaceMember1.setCreatedAt(LocalDateTime.now());
        testEntityManager.persistAndFlush(workspaceMember1);

        WorkspaceMember workspaceMember2 = new WorkspaceMember();
        workspaceMember2.setWorkspace(workspace2);
        workspaceMember2.setMember(user2);
        workspaceMember2.setRole(WorkspaceRoleType.OWNER);
        workspaceMember2.setCreatedAt(LocalDateTime.now());
        testEntityManager.persistAndFlush(workspaceMember2);

        List<Workspace> expectedWorkspaces = List.of(workspace1);

        List<Workspace> actualWorkspaces  = workspaceRepository.findAllByMembers_Member_Id(1L);
        actualWorkspaces.forEach(workspace -> workspace.setId(null));

        assertThat(expectedWorkspaces).containsExactlyInAnyOrderElementsOf(actualWorkspaces);
    }
}