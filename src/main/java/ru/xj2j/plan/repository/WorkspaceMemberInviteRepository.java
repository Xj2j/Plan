package ru.xj2j.plan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.xj2j.plan.model.WorkspaceMemberInvite;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceMemberInviteRepository extends JpaRepository<WorkspaceMemberInvite, Long> {

    boolean existsByIdAndEmailAndWorkspace_Slug(Long id, String email, String workspaceSlug);

    boolean existsByIdAndInvitor_EmailAndWorkspace_Slug(Long id, String email, String workspaceSlug);

    Optional<WorkspaceMemberInvite> findByIdAndWorkspaceSlug(Long id, String WorkspaceSlug);

    List<WorkspaceMemberInvite> getByEmail(String email);

    List<WorkspaceMemberInvite> getByWorkspace_Slug(String slug);
}
