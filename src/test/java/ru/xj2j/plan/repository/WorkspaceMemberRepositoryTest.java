package ru.xj2j.plan.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.xj2j.plan.model.User;
import ru.xj2j.plan.model.Workspace;
import ru.xj2j.plan.model.WorkspaceMember;
import ru.xj2j.plan.model.WorkspaceRoleType;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class WorkspaceMemberRepositoryTest {

    @Autowired
    TestEntityManager em;

    @Autowired
    WorkspaceMemberRepository repository;

    @Test
    @DisplayName("findByWorkspaceSlugAndMember_EmailIn should return empty list when workspace and user does not exist")
    void findByWorkspaceSlugAndMember_EmailIn_ShouldReturnEmptyListWhenWorkspaceAndUserDoesNotExist() {
        var actualMembers = repository.findByWorkspace_SlugAndMember_EmailIn("test-workspace", List.of("email"));

        assertThat(actualMembers).isEmpty();
    }

    @Test
    @DisplayName("findByWorkspaceSlugAndMember_EmailIn should return empty list when user not attached to workspace")
    void findByWorkspaceSlugAndMember_EmailIn_ShouldReturnEmptyListWhenUserNotAttachedToWorkspaces() {
        var user = new User();
        user.setEmail("test@email.com");
        user.setPassword("testpass");
        em.persistAndFlush(user);

        var actualMembers = repository.findByWorkspace_SlugAndMember_EmailIn("test-workspace", List.of(user.getEmail()));

        assertThat(actualMembers).isEmpty();
    }

    @DisplayName("findByWorkspaceSlugAndMember_EmailIn should return members for workspace slug and user emails")
    @Test
    void findByWorkspaceSlugAndMember_EmailIn_ShouldReturnCorrectList() {
        var user1 = new User();
        user1.setEmail("test@email.com");
        user1.setPassword("testpass");
        em.persistAndFlush(user1);

        var user2 = new User();
        user2.setEmail("test2@email.com");
        user2.setPassword("testpass2");
        em.persistAndFlush(user2);

        var workspace = new Workspace();
        workspace.setName("test workspace");
        workspace.setSlug("test-workspace");
        workspace.setCreatedAt(LocalDateTime.now());
        em.persistAndFlush(workspace);

        var workspaceMember1 = new WorkspaceMember();
        workspaceMember1.setWorkspace(workspace);
        workspaceMember1.setMember(user1);
        workspaceMember1.setRole(WorkspaceRoleType.OWNER);
        workspaceMember1.setCreatedAt(LocalDateTime.now());
        em.persistAndFlush(workspaceMember1);

        var workspaceMember2 = new WorkspaceMember();
        workspaceMember2.setWorkspace(workspace);
        workspaceMember2.setMember(user2);
        workspaceMember2.setRole(WorkspaceRoleType.MEMBER);
        workspaceMember2.setCreatedAt(LocalDateTime.now());
        em.persistAndFlush(workspaceMember2);

        var expectedMembers = Arrays.asList(workspaceMember1, workspaceMember2);

        var actualMembers = repository.findByWorkspace_SlugAndMember_EmailIn("test-workspace", Arrays.asList(user1.getEmail(), user2.getEmail()));
        actualMembers.forEach(member -> member.setId(null));

        assertThat(actualMembers).hasSize(2);
        assertThat(actualMembers).containsExactlyInAnyOrderElementsOf(expectedMembers);
    }
}