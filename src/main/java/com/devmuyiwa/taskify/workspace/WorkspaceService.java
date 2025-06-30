package com.devmuyiwa.taskify.workspace;

import com.devmuyiwa.taskify.common.events.UserRegisteredEvent;
import com.devmuyiwa.taskify.common.events.WorkspaceMemberCreatedEvent;
import com.devmuyiwa.taskify.workspace.domain.external.Workspace;
import com.devmuyiwa.taskify.workspace.domain.external.WorkspaceMember;
import com.devmuyiwa.taskify.workspace.domain.internal.WorkspaceMemberRole;
import com.devmuyiwa.taskify.workspace.repository.WorkspaceMemberRepository;
import com.devmuyiwa.taskify.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepo;
    private final WorkspaceMemberRepository workspaceMemberRepo;
    private final ApplicationEventPublisher eventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void handleWorkspaceCreation(UserRegisteredEvent event) {
        System.out.println("Received UserRegisteredEvent!");
        Workspace workspace = Workspace.builder()
                .name(event.firstName() + "'s Workspace")
                .build();
        workspaceRepo.save(workspace);

        WorkspaceMember member = WorkspaceMember.builder()
                .firstName(event.firstName())
                .lastName(event.lastName())
                .role(WorkspaceMemberRole.OWNER)
                .user(event.user())
                .workspace(workspace)
                .build();
        workspaceMemberRepo.save(member);

        System.out.println("Workspace created!");

        eventPublisher.publishEvent(new WorkspaceMemberCreatedEvent(member.getFirstName(), event.user().getEmail()));
    }
}
