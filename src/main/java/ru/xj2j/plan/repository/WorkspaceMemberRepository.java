package ru.xj2j.plan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.xj2j.plan.model.User;
import ru.xj2j.plan.model.WorkspaceMember;
import ru.xj2j.plan.model.WorkspaceRoleType;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, Long> {

    List<WorkspaceMember> findByWorkspaceId(Long workspaceId);

    /*@Query("SELECT wm FROM WorkspaceMember wm JOIN FETCH wm.member u WHERE wm.workspace.id = :workspaceId")
    List<WorkspaceMember> findWithUserEmailByWorkspaceId(@Param("workspaceId") Long workspaceId);*/

    /*@Query("SELECT wm FROM WorkspaceMember wm JOIN FETCH wm.member WHERE wm.id = :id")
    WorkspaceMember findWorkspaceMemberWithUserById(@Param("id") Long id);*/

    @Query("SELECT wm FROM WorkspaceMember wm JOIN FETCH wm.member WHERE wm.workspace.id = :workspaceId")
    List<WorkspaceMember> findWorkspaceMembersByWorkspaceId(@Param("workspaceId") Long workspaceId);

    @Query("SELECT wm FROM WorkspaceMember wm JOIN FETCH wm.member WHERE wm.workspace.slug = :workspaceSlug")
    List<WorkspaceMember> findWorkspaceMembersByWorkspaceSlug(@Param("workspaceSlug") String workspaceSlug);

    Optional<WorkspaceMember> findByWorkspaceIdAndId(Long workspaceId, Long id);

    Optional<WorkspaceMember> findByWorkspace_SlugAndId(String workspaceSlug, Long id);

    Optional<WorkspaceMember> findByWorkspaceIdAndMemberId(Long workspaceId, Long id);

    Optional<WorkspaceMember> findByWorkspace_SlugAndMember_Id(String workspaceSlug, Long id);

    Optional<WorkspaceMember> findByWorkspaceIdAndMember(Long workspaceId, User member);

    //TODO @Query
    Optional<WorkspaceMember> findByWorkspaceIdAndMemberEmail(Long workspaceId, String email);

    //TODO @Query
    List<WorkspaceMember> findByWorkspaceIdAndMemberEmailIn(Long workspaceId, List<String> emails);

    /*@Query("""
        SELECT wm.role FROM WorkspaceMember wm
        WHERE wm.member.id = :userId AND wm.workspace.slug = :slug
        """)
    Optional<WorkspaceRoleType> findRoleByUserIdAndWorkspaceSlug(@Param("userId") Long userId, @Param("workspaceId") String slug);*/

    Optional<WorkspaceRoleType> findByMember_IdAndWorkspace_Slug(Long memberId, String workspaceSlug);



}
