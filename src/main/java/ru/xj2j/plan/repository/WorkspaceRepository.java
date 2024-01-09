package ru.xj2j.plan.repository;


import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.xj2j.plan.model.Workspace;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    /*@Query("SELECT ws FROM Workspace ws " +
            "INNER JOIN WorkspaceMember wm ON ws.id=wm.workspaceId " +
            "INNER JOIN User u ON wm.userId=u.id " +
            "WHERE u.email=:userEmail")
    List<Workspace> findByUserEmail(String userEmail);*/

    //List<Workspace> findByOwnerEmail(String userEmail);

    @EntityGraph(attributePaths = {"createdBy"})
    List<Workspace> findAllByMembers_Member_Id(Long userId);

    @EntityGraph(attributePaths = {"createdBy"})
    Optional<Workspace> findBySlug(String slug);

    boolean existsBySlug(String slug);

    void deleteBySlug(String slug);

    /*List<Workspace> findByOwner(String owner);*/
}
