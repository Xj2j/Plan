package ru.xj2j.plan.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.xj2j.plan.model.Issue;
import ru.xj2j.plan.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    @EntityGraph(attributePaths = {"comments", "assignees"})
    Optional<Issue> findByIdAndWorkspaceId(Long id, Long workspaceId);

    List<Issue> findAllByWorkspaceId(Long workspaceId);

    List<Issue> findAllByWorkspaceSlug(String workspaceSlug);

    List<Issue> findAllByWorkspaceIdAndCreatedBy_Id(Long workspaceId, Long createdById);

    List<Issue> findAllByWorkspaceSlugAndCreatedBy_Id(String workspaceSlug, Long id);

    List<Issue> findAllByWorkspaceIdAndAssignees_Id(Long workspaceId, Long userId);

    List<Issue> findAllByWorkspaceSlugAndAssignees_Id(String workspaceSlug, Long id);

    boolean existsByCreatedBy_IdAndId(Long userId, Long issueId);

    boolean existsByAssignees_IdAndId(Long userId, Long issueId);

    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM Issue i WHERE (i.createdBy.id = :userId OR :userId MEMBER OF i.assignees) AND i.id = :issueId")
    boolean existsByCreatedByOrAssignee(@Param("userId") Long userId, @Param("issueId") Long issueId);

    Optional<Issue> findByIdAndWorkspaceSlug(Long issueId, String workspaceSlug);

    @Query("SELECT u FROM Issue i JOIN i.assignees u WHERE i.id = :issueId AND u.email IN :emails")
    Set<User> findAssigneesByIssueIdAndEmails(@Param("issueId") Long issueId, @Param("emails") Set<String> emails);

    @Query("SELECT u FROM Issue i JOIN i.assignees u WHERE i.id = :issueId AND u.id IN :ids")
    Set<User> findAssigneesByIssueIdAndIds(@Param("issueId") Long issueId, @Param("ids") Set<Long> ids);

}
