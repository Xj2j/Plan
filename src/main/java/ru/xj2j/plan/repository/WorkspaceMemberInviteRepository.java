package ru.xj2j.plan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.xj2j.plan.model.WorkspaceMemberInvite;

import java.util.Optional;

@Repository
public interface WorkspaceMemberInviteRepository extends JpaRepository<WorkspaceMemberInvite, Long> {

    boolean existsByIdAndEmailAndWorkspaceSlug(Long id, String email, String workspaceSlug);

    Optional<WorkspaceMemberInvite> findByIdAndWorkspaceSlug(Long id, String WorkspaceSlug);
}
