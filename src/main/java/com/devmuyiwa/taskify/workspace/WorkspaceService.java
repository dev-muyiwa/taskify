package com.devmuyiwa.taskify.workspace;

import com.devmuyiwa.taskify.common.events.UserRegisteredEvent;
import com.devmuyiwa.taskify.common.events.WorkspaceMemberCreatedEvent;
import com.devmuyiwa.taskify.workspace.domain.external.Workspace;
import com.devmuyiwa.taskify.workspace.domain.external.WorkspaceMember;
import com.devmuyiwa.taskify.workspace.domain.internal.WorkspaceMemberRole;
import com.devmuyiwa.taskify.workspace.repository.WorkspaceMemberRepository;
import com.devmuyiwa.taskify.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepo;
    private final WorkspaceMemberRepository workspaceMemberRepo;
    private final ApplicationEventPublisher eventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void handleWorkspaceCreation(UserRegisteredEvent event) {
        try {
            log.info("Creating workspace for user: {}", event.user().getEmail());
            
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

            log.info("Workspace created successfully for user: {}", event.user().getEmail());

            // Publish workspace member created event asynchronously
            publishWorkspaceMemberCreatedEventAsync(member, event.user().getEmail());
            
        } catch (Exception e) {
            log.error("Failed to create workspace for user: {}", event.user().getEmail(), e);
            // Don't rethrow to avoid affecting the main registration flow
        }
    }

    private void publishWorkspaceMemberCreatedEventAsync(WorkspaceMember member, String email) {
        try {
            eventPublisher.publishEvent(new WorkspaceMemberCreatedEvent(member.getFirstName(), email));
            log.info("WorkspaceMemberCreatedEvent published successfully for user: {}", email);
        } catch (Exception e) {
            log.error("Failed to publish WorkspaceMemberCreatedEvent for user: {}", email, e);
        }
    }
}
