package ru.xj2j.plan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.xj2j.plan.model.IssueComment;

@Repository
public interface IssueCommentRepository extends JpaRepository<IssueComment, Long> {

    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM IssueComment i WHERE i.createdBy.id = :userId AND i.id = :commentId")
    boolean existsByCreatedBy(@Param("userId") Long userId, @Param("commentId") Long commentId);
}
