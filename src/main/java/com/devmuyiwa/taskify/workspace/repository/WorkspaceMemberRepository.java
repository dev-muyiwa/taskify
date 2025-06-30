package com.devmuyiwa.taskify.workspace.repository;

import com.devmuyiwa.taskify.workspace.domain.external.WorkspaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, UUID> {
}
