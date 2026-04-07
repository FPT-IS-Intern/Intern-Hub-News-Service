package com.intern.hub.news.infra.service.scheduler;

import com.intern.hub.news.core.domain.usecase.NewsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
    prefix = "news.reconcile.pending-approval",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
public class PendingNewsApprovalReconcileJob {

  private final NewsUseCase newsUseCase;

  @Scheduled(fixedDelayString = "${news.reconcile.pending-approval.fixed-delay-ms:60000}")
  public void reconcile() {
    try {
      newsUseCase.reconcilePendingNewsApprovals();
    } catch (Exception ex) {
      log.error("[News][ReconcilePending] Failed to reconcile pending news approvals", ex);
    }
  }
}
