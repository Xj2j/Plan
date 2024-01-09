package ru.xj2j.plan.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.xj2j.plan.dto.UserDTO;
import ru.xj2j.plan.dto.WorkspaceDTO;
import ru.xj2j.plan.dto.WorkspaceMemberDTO;
import ru.xj2j.plan.dto.WorkspaceUpdateDTO;
import ru.xj2j.plan.model.User;
import ru.xj2j.plan.model.Workspace;
import ru.xj2j.plan.model.WorkspaceMember;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

class WorkspaceMapperTest {

    private final WorkspaceMapper workspaceMapper = new WorkspaceMapperImpl(new WorkspaceMemberMapperImpl(new UserMapperImpl()), new UserMapperImpl());
    @Test
    void toDto() {
        User user = new User();
        user.setEmail("test@mail.com");

        WorkspaceMember workspaceMember = new WorkspaceMember();
        workspaceMember.setMember(user);

        Workspace workspace = new Workspace();
        workspace.setSlug("test-slug");
        workspace.setCreatedBy(user);
        workspace.getMembers().add(workspaceMember);

        WorkspaceDTO result = workspaceMapper.toDto(workspace);

        assertNotNull(result);
        assertThat(result.getSlug()).isEqualTo("test-slug");
        assertThat(result.getCreatedBy().getEmail()).isEqualTo("test@mail.com");

        WorkspaceMemberDTO expectedMember = result.getMembers().get(0);
        assertThat(expectedMember.getMember().getEmail()).isEqualTo("test@mail.com");
    }

    @Test
    void updateWorkspaceFromDto() {
        WorkspaceUpdateDTO workspaceDTO = new WorkspaceUpdateDTO();
        workspaceDTO.setSlug("new-name");
        workspaceDTO.setName("new name");

        Workspace workspace = new Workspace();
        workspace.setId(1L);
        workspace.setSlug("old-name");
        workspace.setName("old name");
        workspace.setDescription("old description");

        Workspace result = workspaceMapper.updateWorkspaceFromDto(workspaceDTO, workspace);

        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSlug()).isEqualTo("new-name");
        assertThat(result.getName()).isEqualTo("new name");
        assertThat(result.getDescription()).isEqualTo("old description");
    }
}