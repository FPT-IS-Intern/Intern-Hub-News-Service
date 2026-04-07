package com.intern.hub.news.api.controller.internal;

import com.intern.hub.library.common.dto.ResponseApi;
import com.intern.hub.news.core.domain.usecase.NewsUseCase;
import com.intern.hub.starter.security.annotation.Internal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/news/internal/tickets")
public class InternalNewsApprovalController {

  private final NewsUseCase newsUseCase;

  @PostMapping("/{ticketId}/approved")
  @Internal
  public ResponseApi<String> approveByTicketId(@PathVariable Long ticketId) {
    newsUseCase.approveByTicketId(ticketId);
    return ResponseApi.ok("OK");
  }
}
