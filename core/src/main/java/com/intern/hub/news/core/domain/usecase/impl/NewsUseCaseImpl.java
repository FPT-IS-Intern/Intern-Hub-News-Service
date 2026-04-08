package com.intern.hub.news.core.domain.usecase.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.text.Normalizer;
import java.util.stream.Collectors;

import com.intern.hub.library.common.dto.PaginatedData;
import com.intern.hub.library.common.exception.BadRequestException;
import com.intern.hub.library.common.exception.ExceptionConstant;
import com.intern.hub.news.core.domain.command.CreateNewsCommand;
import com.intern.hub.news.core.domain.command.CreateTicketCommand;
import com.intern.hub.news.core.domain.command.UpdateNewsCommand;
import com.intern.hub.news.core.domain.model.NewsModel;
import com.intern.hub.news.core.domain.model.NewsStatusModel;
import com.intern.hub.news.core.domain.model.NewsTopicModel;
import com.intern.hub.news.core.domain.port.NewsRepository;
import com.intern.hub.news.core.domain.port.NewsStatusRepository;
import com.intern.hub.news.core.domain.port.TicketService;
import com.intern.hub.news.core.domain.usecase.NewsUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class NewsUseCaseImpl implements NewsUseCase {

  private static final String STATUS_PENDING = "PENDING";
  private static final String STATUS_APPROVED = "APPROVED";
  private static final String STATUS_REJECT = "REJECT";
  private static final String STATUS_DRAFT = "DRAFT";
  private static final int TICKET_APPROVAL_MAX_RETRY = 5;
  private static final long TICKET_APPROVAL_RETRY_DELAY_MS = 600L;
  private static final int RECONCILE_PAGE_SIZE = 200;
  private static final int RECONCILE_MAX_PAGES = 10;

  private final NewsRepository newsRepository;
  private final NewsStatusRepository newsStatusRepository;
  private final TicketService ticketService;
  private final Long newsTicketTypeId;

  @Override
  public NewsModel create(CreateNewsCommand command) {
    try {
      validateInput(command.getTitle(), command.getBody(), command.getShortDescription());
      long now = System.currentTimeMillis();
      NewsModel newsModel = new NewsModel();
      newsModel.setTitle(command.getTitle());
      newsModel.setBody(command.getBody());
      newsModel.setShortDescription(command.getShortDescription());
      newsModel.setThumbnail(normalizeThumbnail(command.getThumbnail()));
      newsModel.setTopics(command.getTopicIds() != null
          ? command.getTopicIds().stream().map(tId -> {
            NewsTopicModel tm = new NewsTopicModel();
            tm.setId(tId);
            return tm;
          }).toList()
          : new java.util.ArrayList<>());

      Long pendingStatusId = getStatusIdByName(STATUS_PENDING);
      Long statusId = command.getStatusId();
      if (statusId == null) {
        statusId = pendingStatusId;
      }
      newsModel.setStatusId(statusId);
      newsModel.setFeatured(command.getFeatured() != null && command.getFeatured());
      newsModel.setCreatedAt(now);
      newsModel.setUpdatedAt(now);
      newsModel.setCreatedBy(command.getUserId());

      NewsModel saved = newsRepository.create(newsModel);
      if (pendingStatusId.equals(statusId)) {
        Long ticketId = createApprovalTicketOrRollback(saved, command.getUserId());
        newsRepository.updateApprovalTicketId(saved.getId(), ticketId, System.currentTimeMillis());
        saved.setApprovalTicketId(ticketId);
      }
      log.info("[News] Create New Successfully: {}", saved.getId());
      return saved;
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (Exception e) {
      log.error("[News] TÃ¡ÂºÂ¡o News thÃ¡ÂºÂ¥t bÃ¡ÂºÂ¡i: {}", e.getMessage(), e);
      throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE,
          "Failed to create news: " + e.getMessage());
    }
  }

  @Override
  public NewsModel update(Long id, UpdateNewsCommand command) {
    try {
      validateInput(command.getTitle(), command.getBody(), command.getShortDescription());
      NewsModel existing = getById(id);

      existing.setTitle(command.getTitle());
      existing.setBody(command.getBody());
      existing.setShortDescription(command.getShortDescription());
      existing.setThumbnail(normalizeThumbnail(command.getThumbnail()));
      existing.setTopics(command.getTopicIds() != null
          ? command.getTopicIds().stream().map(tId -> {
            NewsTopicModel tm = new NewsTopicModel();
            tm.setId(tId);
            return tm;
          }).toList()
          : new ArrayList<>());
      existing.setFeatured(command.getFeatured() != null && command.getFeatured());

      // Use the provided status if available, else force pending
      if (command.getStatusId() != null) {
        existing.setStatusId(command.getStatusId());
      } else {
        existing.setStatusId(getStatusIdByName(STATUS_PENDING));
      }
      existing.setUpdatedAt(System.currentTimeMillis());
      return newsRepository.update(existing);
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (Exception e) {
      log.error("[News] CÃ¡ÂºÂ­p nhÃ¡ÂºÂ­t News thÃ¡ÂºÂ¥t bÃ¡ÂºÂ¡i: {}", e.getMessage(), e);
      throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE,
          "Failed to update news: " + e.getMessage());
    }

  }

  @Override
  public NewsModel approve(Long id) {
    try {
      NewsModel existing = getById(id);
      existing.setStatusId(getStatusIdByName(STATUS_APPROVED));
      existing.setUpdatedAt(System.currentTimeMillis());
      return newsRepository.update(existing);
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (Exception e) {
      log.error("[News] DuyÃ¡Â»â€¡t News thÃ¡ÂºÂ¥t bÃ¡ÂºÂ¡i: {}", e.getMessage(), e);
      throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE, "Failed to approve news");
    }
  }

  @Override
  public void approveByTicketId(Long ticketId) {
    if (ticketId == null) {
      log.warn("[News][ApproveByTicket] Skip because ticketId is null");
      return;
    }
    log.info("[News][ApproveByTicket] Start processing ticketId={}", ticketId);
    markNewsByTicketId(ticketId, STATUS_APPROVED);
  }

  @Override
  public void rejectByTicketId(Long ticketId) {
    if (ticketId == null) {
      log.warn("[News][RejectByTicket] Skip because ticketId is null");
      return;
    }
    log.info("[News][RejectByTicket] Start processing ticketId={}", ticketId);
    markNewsByTicketId(ticketId, STATUS_REJECT);
  }

  @Override
  public void reconcilePendingNewsApprovals() {
    List<String> pendingStatusNames = getStatusNamesByMode(STATUS_PENDING);
    if (pendingStatusNames.isEmpty()) {
      return;
    }
    int candidateCount = 0;
    int pageProcessed = 0;

    for (int page = 0; page < RECONCILE_MAX_PAGES; page++) {
      List<NewsModel> pendingNews = newsRepository.findPageByStatusNames(
          pendingStatusNames,
          page,
          RECONCILE_PAGE_SIZE,
          "updated_at",
          "desc");

      if (pendingNews.isEmpty()) {
        break;
      }

      pageProcessed++;
      for (NewsModel pendingItem : pendingNews) {
        if (pendingItem == null || pendingItem.getApprovalTicketId() == null) {
          continue;
        }
        candidateCount++;
        Long ticketId = pendingItem.getApprovalTicketId();
        if (waitForApprovedTicket(ticketId)) {
          markNewsByTicketId(ticketId, STATUS_APPROVED);
        }
      }
    }
    log.info("[News][ReconcilePending] processed {} candidate tickets across {} page(s)", candidateCount,
        pageProcessed);
  }

  private void markNewsByTicketId(Long ticketId, String targetStatus) {
    NewsModel existing = newsRepository.findByApprovalTicketId(ticketId).orElse(null);
    if (existing == null) {
      log.warn("[News][TicketStatusSync] No news found with approvalTicketId={}", ticketId);
      return;
    }

    if (existing.getStatus() != null && targetStatus.equalsIgnoreCase(toStatusKey(existing.getStatus()))) {
      log.info("[News][TicketStatusSync] News id={} already in status {}. Skip update.", existing.getId(), targetStatus);
      return;
    }

    existing.setStatusId(getStatusIdByName(targetStatus));
    existing.setUpdatedAt(System.currentTimeMillis());
    newsRepository.update(existing);
    log.info("[News][TicketStatusSync] Updated news id={} to {} via ticketId={}", existing.getId(), targetStatus, ticketId);
  }

  private boolean waitForApprovedTicket(Long ticketId) {
    for (int attempt = 1; attempt <= TICKET_APPROVAL_MAX_RETRY; attempt++) {
      try {
        if (ticketService.isTicketApproved(ticketId)) {
          return true;
        }
      } catch (Exception ex) {
        log.warn("[News][ApproveByTicket] Failed to check ticket status, ticketId={}, attempt={}/{}: {}",
            ticketId, attempt, TICKET_APPROVAL_MAX_RETRY, ex.getMessage());
      }

      if (attempt < TICKET_APPROVAL_MAX_RETRY) {
        try {
          Thread.sleep(TICKET_APPROVAL_RETRY_DELAY_MS);
        } catch (InterruptedException interruptedException) {
          Thread.currentThread().interrupt();
          return false;
        }
      }
    }
    return false;
  }

  @Override
  public NewsModel getById(Long id) {
    try {
      return newsRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("News not found with id: " + id));
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (Exception _) {
      throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE, "Failed to get news by id");
    }
  }

  @Override
  public List<NewsModel> getAll() {
    return newsRepository.findAll();
  }

  @Override
  public PaginatedData<NewsModel> findPage(int page, int size, String sortColumn, String sortDirection) {
    List<NewsModel> items = newsRepository.findPage(page, size, sortColumn, sortDirection);
    long total = newsRepository.count();
    return new PaginatedData<>(items, (int) total, size);
  }

  @Override
  public PaginatedData<NewsModel> findPageByDateRange(long start, long end, int page, int size, String sortColumn,
      String sortDirection) {
    List<NewsModel> items = newsRepository.findPageByDateRange(start, end, page, size, sortColumn, sortDirection);
    long total = newsRepository.countByDateRange(start, end);
    return new PaginatedData<>(items, (int) total, size);
  }

  @Override
  public PaginatedData<NewsModel> getApprovedNews(int page, int size, String sortColumn, String sortDirection) {
    List<String> approvedStatusNames = getStatusNamesByMode(STATUS_APPROVED);
    List<NewsModel> items = newsRepository.findPageByStatusNames(approvedStatusNames, page, size, sortColumn, sortDirection);
    long total = newsRepository.countByStatusNames(approvedStatusNames);
    return new PaginatedData<>(items, (int) total, size);
  }

  @Override
  public PaginatedData<NewsModel> searchApprovedNewsByTitle(String title, int page, int size, String sortColumn,
      String sortDirection) {
    List<String> approvedStatusNames = getStatusNamesByMode(STATUS_APPROVED);
    List<NewsModel> items = newsRepository.findPageByStatusNamesAndTitle(approvedStatusNames, title, page, size, sortColumn,
        sortDirection);
    long total = newsRepository.countByStatusNamesAndTitle(approvedStatusNames, title);
    return new PaginatedData<>(items, (int) total, size);
  }

  @Override
  public PaginatedData<NewsModel> getApprovedNewsByTopic(Long topicId, int page, int size, String sortColumn,
      String sortDirection) {
    try {
      List<String> approvedStatusNames = getStatusNamesByMode(STATUS_APPROVED);
      List<NewsModel> items = newsRepository.findPageByTopicAndStatusNames(topicId, approvedStatusNames, page, size, sortColumn, sortDirection);
      long total = items.size(); // Simplified total for topic-specific view
      return new PaginatedData<>(items, (int) total, size);
    } catch (Exception e) {
      log.error("[News] LÃ¡Â»â€”i khi lÃ¡ÂºÂ¥y tin tÃ¡Â»Â©c theo topic {}: {}", topicId, e.getMessage());
      throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE, "Failed to get news by topic");
    }
  }

  @Override
  public PaginatedData<NewsModel> getPendingNews(int page, int size, String sortColumn, String sortDirection) {
    List<String> pendingStatusNames = getStatusNamesByMode(STATUS_PENDING);
    List<NewsModel> items = newsRepository.findPageByStatusNames(pendingStatusNames, page, size, sortColumn,
        sortDirection);
    long total = newsRepository.countByStatusNames(pendingStatusNames);
    return new PaginatedData<>(items, (int) total, size);
  }

  @Override
  public PaginatedData<NewsModel> getAllNewsIsFeatured(int page, int size, String sortColumn, String sortDirection) {
    List<String> approvedStatusNames = getStatusNamesByMode(STATUS_APPROVED);
    List<NewsModel> items = newsRepository.findPageByFeaturedAndStatusNames(true, approvedStatusNames, page, size,
        sortColumn, sortDirection);
    long total = newsRepository.countByFeaturedAndStatusNames(true, approvedStatusNames);
    return new PaginatedData<>(items, (int) total, size);
  }

  @Override
  public List<NewsModel> getLatestFeaturedNews(int total) {
    try {
      return newsRepository.findPageByFeatured(true, 0, total, "updatedAt", "desc");
    } catch (Exception e) {
      log.error("[News] LÃ¡Â»â€”i khi lÃ¡ÂºÂ¥y tin tÃ¡Â»Â©c nÃ¡Â»â€¢i bÃ¡ÂºÂ­t: {}", e.getMessage());
      throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE, "Failed to get latest featured news");
    }
  }

  @Override
  public List<NewsModel> getTop3LatestNews() {
    try {
      return newsRepository.findPageByStatusNames(getStatusNamesByMode(STATUS_APPROVED), 0, 3, "createdAt", "desc");
    } catch (Exception _) {
      throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE, "Failed to get top 3 latest news");
    }
  }

  private List<String> getStatusNamesByMode(String mode) {
    String normalizedMode = mode == null ? "" : mode.trim().toUpperCase(Locale.ROOT);
    List<String> statusNames = newsStatusRepository.findAll().stream()
        .filter(Objects::nonNull)
        .filter(status -> status.getName() != null && !status.getName().isBlank())
        .filter(status -> normalizedMode.equals(toStatusKey(status.getName())))
        .map(NewsStatusModel::getName)
        .distinct()
        .toList();

    if (statusNames.isEmpty() && !normalizedMode.isBlank()) {
      return List.of(normalizedMode);
    }
    return statusNames;
  }

  @Override
  public void delete(Long id) {
    try {
      if (!newsRepository.existsById(id)) {
        throw new IllegalArgumentException("News not found with id: " + id);
      }
      newsRepository.deleteById(id);
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (Exception _) {
      throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE, "Failed to delete news");
    }
  }

  private Long getStatusIdByName(String name) {
    List<NewsStatusModel> allStatuses = newsStatusRepository.findAll();
    String targetKey = toStatusKey(name);
    if (targetKey == null) {
      targetKey = normalizeStatusName(name);
    }

    for (NewsStatusModel status : allStatuses) {
      if (status == null || status.getName() == null) {
        continue;
      }
      String statusKey = toStatusKey(status.getName());
      if (statusKey == null) {
        statusKey = normalizeStatusName(status.getName());
      }
      if (targetKey.equals(statusKey)) {
        return status.getId();
      }
    }

    String availableStatuses = allStatuses.stream()
        .map(NewsStatusModel::getName)
        .filter(Objects::nonNull)
        .collect(Collectors.joining(", "));

    throw new BadRequestException(
        ExceptionConstant.BAD_REQUEST_DEFAULT_CODE,
        "Status not found: " + name + ". Available statuses: " + availableStatuses);
  }

  private String toStatusKey(String input) {
    String normalized = normalizeStatusName(input);
    if (normalized.isEmpty()) {
      return null;
    }

    if ("PENDING".equals(normalized) || "CHO DUYET".equals(normalized)) {
      return STATUS_PENDING;
    }

    if ("APPROVED".equals(normalized)
        || "APPROVE".equals(normalized)
        || "DA DUYET".equals(normalized)
        || "QUYET DINH DANG".equals(normalized)) {
      return STATUS_APPROVED;
    }

    if ("REJECT".equals(normalized)
        || "REJECTED".equals(normalized)
        || "TU CHOI".equals(normalized)) {
      return STATUS_REJECT;
    }

    if ("DRAFT".equals(normalized)
        || "LUU NHAP".equals(normalized)
        || "BAN NHAP".equals(normalized)
        || "NHAP".equals(normalized)) {
      return STATUS_DRAFT;
    }

    return null;
  }

  private String normalizeStatusName(String input) {
    if (input == null) {
      return "";
    }
    String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
         .replace("\u0111", "d")
         .replace("\u0110", "D")
        .replaceAll("\\p{M}", "")
        .replaceAll("\\s+", " ")
        .trim()
        .toUpperCase(Locale.ROOT);
    return normalized;
  }
  private String normalizeThumbnail(String thumbnail) {
    return thumbnail == null ? "" : thumbnail;
  }

  private void validateInput(String title, String body, String shortDescription) {
    if (title == null || title.isBlank()) {
      throw new IllegalArgumentException("Title is required");
    }
    if (body == null || body.isBlank()) {
      throw new IllegalArgumentException("Body is required");
    }
    if (shortDescription == null || shortDescription.isBlank()) {
      throw new IllegalArgumentException("Short description is required");
    }
  }

  private Map<String, Object> buildNewsTicketPayload(NewsModel news) {
    Map<String, Object> payload = new HashMap<>();
    payload.put("news_id", String.valueOf(news.getId()));
    payload.put("title", news.getTitle());
    payload.put("summary", news.getShortDescription());
    payload.put("content", news.getBody());
    payload.put("reason", news.getShortDescription());
    payload.put("thumbnail_url", news.getThumbnail());
    payload.put("preview_url", "/news/" + news.getId());

    String category = "";
    if (news.getTopics() != null && !news.getTopics().isEmpty()) {
      category = news.getTopics().stream()
          .map(NewsTopicModel::getId)
          .filter(Objects::nonNull)
          .map(String::valueOf)
          .collect(Collectors.joining(","));
    }
    payload.put("category", category);

    List<String> imageUrls = new ArrayList<>();
    if (news.getThumbnail() != null && !news.getThumbnail().isBlank()) {
      imageUrls.add(news.getThumbnail());
    }
    payload.put("image_urls", imageUrls);

    return payload;
  }

  private Long createApprovalTicketOrRollback(NewsModel saved, Long userId) {
    Long ticketId;
    try {
      ticketId = ticketService.createTicket(new CreateTicketCommand(
          userId,
          newsTicketTypeId,
          buildNewsTicketPayload(saved)));
    } catch (Exception ex) {
      newsRepository.deleteById(saved.getId());
      throw ex;
    }

    if (ticketId == null) {
      newsRepository.deleteById(saved.getId());
      throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE,
          "Failed to create approval ticket for news");
    }

    return ticketId;
  }
}

