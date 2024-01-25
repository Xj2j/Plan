package ru.xj2j.plan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.xj2j.plan.model.WorkspaceMemberInvite;

@Repository
public interface WorkspaceMemberInviteRepository extends JpaRepository<WorkspaceMemberInvite, Long> {
}