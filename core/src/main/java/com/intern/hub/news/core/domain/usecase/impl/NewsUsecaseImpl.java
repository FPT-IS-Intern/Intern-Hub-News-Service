package com.intern.hub.news.core.domain.usecase.impl;

import java.util.List;
import com.intern.hub.news.core.domain.model.NewsTopicModel;

import org.springframework.stereotype.Service;

import com.intern.hub.library.common.dto.PaginatedData;
import com.intern.hub.library.common.exception.BadRequestException;
import com.intern.hub.library.common.exception.ExceptionConstant;
import com.intern.hub.news.core.domain.command.CreateNewsCommand;
import com.intern.hub.news.core.domain.command.UpdateNewsCommand;
import com.intern.hub.news.core.domain.model.NewsModel;
import com.intern.hub.news.core.domain.port.NewsRepository;
import com.intern.hub.news.core.domain.port.TicketService;
import com.intern.hub.news.core.domain.usecase.NewsUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsUseCaseImpl implements NewsUseCase {

  private static final String STATUS_PENDING_APPROVAL = "PENDING_APPROVAL";
  private static final String STATUS_PUBLIC = "PUBLIC";

  private final NewsRepository newsRepository;

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
      newsModel.setStatusId(command.getStatusId());
      newsModel.setFeatured(command.getFeatured());
      newsModel.setCreatedAt(now);
      newsModel.setUpdatedAt(now);
      newsModel.setCreatedBy(command.getUserId());
      NewsModel saved = newsRepository.create(newsModel);
      log.info("[News] Create New Successfully: {}", saved);

      return saved;
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (Exception e) {
      log.error("[News] Tạo News hoặc call internal ticket thất bại: {}", e.getMessage(), e);
      throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE,
          "Failed to create news: " + e.getMessage());
    }
  }

  @Override
  public NewsModel update(Long id, UpdateNewsCommand command) {
    try {
      validateInput(command.getTitle(), command.getBody(), command.getShortDescription());
      NewsModel existing = getById(id);
      NewsModel updateModel = new NewsModel();
      updateModel.setId(existing.getId());
      updateModel.setTitle(command.getTitle());
      updateModel.setBody(command.getBody());
      updateModel.setShortDescription(command.getShortDescription());
      updateModel.setTopics(command.getTopicIds() != null
          ? command.getTopicIds().stream().map(tId -> {
            NewsTopicModel tm = new NewsTopicModel();
            tm.setId(tId);
            return tm;
          }).toList()
          : new java.util.ArrayList<>());
      updateModel.setFeatured(command.getFeatured());
      updateModel.setThumbnail(existing.getThumbnail());
      updateModel.setStatus(STATUS_PENDING_APPROVAL);
      return newsRepository.create(updateModel);
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (Exception _) {
      throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE, "Failed to update news");
    }
  }

  @Override
  public NewsModel approve(Long id) {
    try {
      NewsModel existing = getById(id);
      if (!STATUS_PENDING_APPROVAL.equals(existing.getStatus())) {
        throw new IllegalArgumentException("Only pending news can be approved");
      }
      existing.setStatus(STATUS_PUBLIC);
      return newsRepository.create(existing);
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (Exception _) {
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
  public PaginatedData<NewsModel> findPage(int page, int size) {
    List<NewsModel> items = newsRepository.findPage(page, size);
    long total = newsRepository.count();
    return new PaginatedData<>(items, (int) total, size);
  }

  @Override
  public PaginatedData<NewsModel> findPageByDateRange(long start, long end, int page, int size, String sortColumn, String sortDirection) {
    List<NewsModel> items = newsRepository.findPageByDateRange(start, end, page, size, sortColumn, sortDirection);
    long total = newsRepository.countByDateRange(start, end);
    return new PaginatedData<>(items, (int) total, size);
  }

  @Override
  public PaginatedData<NewsModel> getApprovedNews(int page, int size) {
    List<NewsModel> items = newsRepository.findPageByStatus(STATUS_PUBLIC, page, size);
    long total = newsRepository.countByStatus(STATUS_PUBLIC);
    return new PaginatedData<>(items, (int) total, size);
  }

  @Override
  public PaginatedData<NewsModel> getApprovedNewsByTopic(Long topicId, int page, int size) {
    try {
      List<NewsModel> items = newsRepository.findPageByTopic(topicId, page, size);
      // For simplicity, we can reuse items count if total is not crucial for summary
      // Or we can add a countByTopic repository method.
      return new PaginatedData<>(items, items.size(), size); 
    } catch (Exception e) {
      log.error("[News] Lỗi khi lấy tin tức theo topic {}: {}", topicId, e.getMessage());
      throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE, "Failed to get news by topic");
    }
  }

  @Override
  public PaginatedData<NewsModel> getPendingNews(int page, int size) {
    List<NewsModel> items = newsRepository.findPageByStatus(STATUS_PENDING_APPROVAL, page, size);
    long total = newsRepository.countByStatus(STATUS_PENDING_APPROVAL);
    return new PaginatedData<>(items, (int) total, size);
  }

  @Override
  public PaginatedData<NewsModel> getAllNewsIsFeatured(int page, int size) {
    List<NewsModel> items = newsRepository.findPageByFeatured(true, page, size);
    long total = newsRepository.countByFeatured(true);
    return new PaginatedData<>(items, (int) total, size);
  }

  @Override
  public List<NewsModel> getLatestFeaturedNews(int total) {
    try {
      return newsRepository.findPageByFeatured(true, 0, total);
    } catch (Exception e) {
      log.error("[News] Lỗi khi lấy tin tức nổi bật: {}", e.getMessage());
      throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE, "Failed to get latest featured news");
    }
  }

  @Override
  public List<NewsModel> getTop3LatestNews() {
    try {
      return newsRepository.findPageByStatus(STATUS_PUBLIC, 0, 3);
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
