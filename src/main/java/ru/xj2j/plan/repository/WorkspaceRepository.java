package ru.xj2j.plan.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.xj2j.plan.model.Workspace;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {

    Optional<Workspace> findById(Long id);

    /*@Query("SELECT ws FROM Workspace ws " +
            "INNER JOIN WorkspaceMember wm ON ws.id=wm.workspaceId " +
            "INNER JOIN User u ON wm.userId=u.id " +
            "WHERE u.email=:userEmail")*/
    /*List<Workspace> findByUserEmail(String userEmail);*/

    boolean existsBySlug(String slug);

    /*List<Workspace> findByOwner(String owner);*/
}
