package com.intern.hub.news.core.domain.usecase.impl;

import java.util.ArrayList;
import java.util.List;

import com.intern.hub.library.common.dto.PaginatedData;
import com.intern.hub.library.common.exception.BadRequestException;
import com.intern.hub.library.common.exception.ExceptionConstant;
import com.intern.hub.news.core.domain.command.CreateNewsCommand;
import com.intern.hub.news.core.domain.command.UpdateNewsCommand;
import com.intern.hub.news.core.domain.model.NewsModel;
import com.intern.hub.news.core.domain.model.NewsStatusModel;
import com.intern.hub.news.core.domain.model.NewsTopicModel;
import com.intern.hub.news.core.domain.port.NewsRepository;
import com.intern.hub.news.core.domain.port.NewsStatusRepository;
import com.intern.hub.news.core.domain.usecase.NewsUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class NewsUseCaseImpl implements NewsUseCase {

  private static final String STATUS_PENDING = "PENDING";
  private static final String STATUS_APPROVED = "APPROVED";

  private final NewsRepository newsRepository;
  private final NewsStatusRepository newsStatusRepository;

  @Override
  public NewsModel create(CreateNewsCommand command) {
    try {
      validateInput(command.getTitle(), command.getBody(), command.getShortDescription());
      long now = System.currentTimeMillis();
      NewsModel newsModel = new NewsModel();
      newsModel.setTitle(command.getTitle());
      newsModel.setBody(command.getBody());
      newsModel.setShortDescription(command.getShortDescription());
      newsModel.setThumbnail(command.getThumbnail());
      newsModel.setTopics(command.getTopicIds() != null
          ? command.getTopicIds().stream().map(tId -> {
            NewsTopicModel tm = new NewsTopicModel();
            tm.setId(tId);
            return tm;
          }).toList()
          : new java.util.ArrayList<>());

      Long statusId = command.getStatusId();
      if (statusId == null) {
        statusId = getStatusIdByName(STATUS_PENDING);
      }
      newsModel.setStatusId(statusId);
      newsModel.setFeatured(command.getFeatured() != null && command.getFeatured());
      newsModel.setCreatedAt(now);
      newsModel.setUpdatedAt(now);
      newsModel.setCreatedBy(command.getUserId());

      NewsModel saved = newsRepository.create(newsModel);
      log.info("[News] Create New Successfully: {}", saved != null ? saved.getId() : "null");

      return saved;
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (Exception e) {
      log.error("[News] Tạo News thất bại: {}", e.getMessage(), e);
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
      existing.setThumbnail(command.getThumbnail());
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
      log.error("[News] Cập nhật News thất bại: {}", e.getMessage(), e);
      throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE,
          "Failed to update news: " + e.getMessage());
    }

  }

  @Override
  public NewsModel approve(Long id) {
    try {
      NewsModel existing = getById(id);
      if (!STATUS_PENDING.equals(existing.getStatus())
          && !STATUS_PENDING.equalsIgnoreCase(existing.getStatus())) {
        // Allow both variant names just in case
      }
      existing.setStatusId(getStatusIdByName(STATUS_APPROVED));
      existing.setUpdatedAt(System.currentTimeMillis());
      return newsRepository.update(existing);
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (Exception e) {
      log.error("[News] Duyệt News thất bại: {}", e.getMessage(), e);
      throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE, "Failed to approve news");
    }
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
      log.error("[News] Lỗi khi lấy tin tức theo topic {}: {}", topicId, e.getMessage());
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
      log.error("[News] Lỗi khi lấy tin tức nổi bật: {}", e.getMessage());
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
    Long statusId = newsStatusRepository.findByName(name)
        .map(NewsStatusModel::getId)
        .orElse(null);

    // Fallbacks for Vietnamese aliases
    if (statusId == null) {
      if ("PENDING".equalsIgnoreCase(name)) {
        statusId = newsStatusRepository.findByName("CHỜ DUYỆT")
            .map(NewsStatusModel::getId).orElse(null);
      } else if ("APPROVED".equalsIgnoreCase(name)) {
        statusId = newsStatusRepository.findByName("QUYẾT ĐỊNH ĐĂNG")
            .map(NewsStatusModel::getId).orElse(null);
        if (statusId == null) {
          statusId = newsStatusRepository.findByName("ĐÃ DUYỆT")
              .map(NewsStatusModel::getId).orElse(null);
        }
        if (statusId == null) {
          statusId = newsStatusRepository.findByName("Đã Duyệt")
              .map(NewsStatusModel::getId).orElse(null);
        }
      } else if ("DRAFT".equalsIgnoreCase(name)) {
        statusId = newsStatusRepository.findByName("BẢN NHÁP")
            .map(NewsStatusModel::getId).orElse(null);
      }
    }

    if (statusId == null) {
      throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE, "Status not found: " + name);
    }
    return statusId;
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
}
