package com.devmuyiwa.taskify.workspace.repository;

import com.devmuyiwa.taskify.workspace.domain.external.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {
}
