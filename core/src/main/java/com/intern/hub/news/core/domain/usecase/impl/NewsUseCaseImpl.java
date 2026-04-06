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
  private static final String STATUS_DRAFT = "DRAFT";

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
    NewsModel existing = newsRepository.findByApprovalTicketId(ticketId).orElse(null);
    if (existing == null) {
      log.warn("[News][ApproveByTicket] No news found with approvalTicketId={}", ticketId);
      return;
    }
    if (existing.getStatus() != null && STATUS_APPROVED.equalsIgnoreCase(existing.getStatus())) {
      log.info("[News][ApproveByTicket] News id={} already approved. Skip update.", existing.getId());
      return;
    }
    if (!ticketService.isTicketApproved(ticketId)) {
      log.warn("[News][ApproveByTicket] Ticket id={} is not APPROVED yet. Skip update.", ticketId);
      return;
    }
    existing.setStatusId(getStatusIdByName(STATUS_APPROVED));
    existing.setUpdatedAt(System.currentTimeMillis());
    newsRepository.update(existing);
    log.info("[News][ApproveByTicket] Updated news id={} to APPROVED via ticketId={}", existing.getId(), ticketId);
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
    List<NewsModel> items = newsRepository.findPageByStatus(STATUS_APPROVED, page, size, sortColumn, sortDirection);
    long total = newsRepository.countByStatus(STATUS_APPROVED);
    return new PaginatedData<>(items, (int) total, size);
  }

  @Override
  public PaginatedData<NewsModel> searchApprovedNewsByTitle(String title, int page, int size, String sortColumn,
      String sortDirection) {
    List<NewsModel> items = newsRepository.findPageByStatusAndTitle(STATUS_APPROVED, title, page, size, sortColumn,
        sortDirection);
    long total = newsRepository.countByStatusAndTitle(STATUS_APPROVED, title);
    return new PaginatedData<>(items, (int) total, size);
  }

  @Override
  public PaginatedData<NewsModel> getApprovedNewsByTopic(Long topicId, int page, int size, String sortColumn,
      String sortDirection) {
    try {
      List<NewsModel> items = newsRepository.findPageByTopic(topicId, page, size, sortColumn, sortDirection);
      long total = items.size(); // Simplified total for topic-specific view
      return new PaginatedData<>(items, (int) total, size);
    } catch (Exception e) {
      log.error("[News] LÃ¡Â»â€”i khi lÃ¡ÂºÂ¥y tin tÃ¡Â»Â©c theo topic {}: {}", topicId, e.getMessage());
      throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE, "Failed to get news by topic");
    }
  }

  @Override
  public PaginatedData<NewsModel> getPendingNews(int page, int size, String sortColumn, String sortDirection) {
    List<NewsModel> items = newsRepository.findPageByStatus(STATUS_PENDING, page, size, sortColumn,
        sortDirection);
    long total = newsRepository.countByStatus(STATUS_PENDING);
    return new PaginatedData<>(items, (int) total, size);
  }

  @Override
  public PaginatedData<NewsModel> getAllNewsIsFeatured(int page, int size, String sortColumn, String sortDirection) {
    Long approvedStatusId = getStatusIdByName(STATUS_APPROVED);
    List<NewsModel> items = newsRepository.findPageByFeaturedAndStatusId(true, approvedStatusId, page, size,
        sortColumn, sortDirection);
    long total = newsRepository.countByFeaturedAndStatusId(true, approvedStatusId);
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
      return newsRepository.findPageByStatus(STATUS_APPROVED, 0, 3, "createdAt", "desc");
    } catch (Exception _) {
      throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE, "Failed to get top 3 latest news");
    }
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

