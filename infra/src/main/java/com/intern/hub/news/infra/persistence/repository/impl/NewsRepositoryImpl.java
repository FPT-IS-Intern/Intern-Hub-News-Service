package com.intern.hub.news.infra.persistence.repository.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.intern.hub.news.core.domain.model.NewsModel;
import com.intern.hub.news.core.domain.port.NewsRepository;
import com.intern.hub.news.infra.mapper.NewsEntityMapper;
import com.intern.hub.news.infra.persistence.entity.News;
import com.intern.hub.news.infra.persistence.repository.jpa.NewsJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NewsRepositoryImpl implements NewsRepository {

  private static final String STATUS_APPROVED = "APPROVED";
  private static final List<String> ALLOWED_SORT_COLUMNS = List.of("createdAt", "title", "updatedAt", "id");

  private final NewsJpaRepository newsJpaRepository;
  private final NewsEntityMapper newsMapper;

  private Sort getSort(String sortColumn, String sortDirection) {
    if (sortColumn == null || sortColumn.isEmpty()) {
      return Sort.by("createdAt").descending();
    }

    String original = sortColumn.trim();
    String sortCol = original;

    // Map snake_case or specific variants to camelCase
    if ("created_at".equalsIgnoreCase(original) || "createdAt".equalsIgnoreCase(original)) {
      sortCol = "createdAt";
    } else if ("updated_at".equalsIgnoreCase(original) || "updatedAt".equalsIgnoreCase(original)) {
      sortCol = "updatedAt";
    } else if ("title".equalsIgnoreCase(original)) {
      sortCol = "title";
    } else if ("id".equalsIgnoreCase(original)) {
      sortCol = "id";
    }

    if (!ALLOWED_SORT_COLUMNS.contains(sortCol)) {
      log.warn("[NewsRepositoryImpl] Invalid sort column '{}', defaulting to createdAt", original);
      sortCol = "createdAt";
    }

    return sortDirection != null && sortDirection.equalsIgnoreCase("asc")
        ? Sort.by(sortCol).ascending()
        : Sort.by(sortCol).descending();
  }

  @Override
  public NewsModel create(NewsModel model) {
    News entity = newsMapper.toEntity(model);
    return newsMapper.toModel(newsJpaRepository.save(entity));
  }

  @Override
  public Optional<NewsModel> findById(Long id) {
    return newsJpaRepository.findById(id).map(newsMapper::toModel);
  }

  @Override
  public Optional<NewsModel> findByApprovalTicketId(Long approvalTicketId) {
    return newsJpaRepository.findByApprovalTicketId(approvalTicketId).map(newsMapper::toModel);
  }

  @Override
  public List<NewsModel> findAll() {
    return newsJpaRepository.findAll().stream().map(newsMapper::toModel).toList();
  }

  @Override
  public List<NewsModel> findPage(int page, int size, String sortColumn, String sortDirection) {
    Sort sort = getSort(sortColumn, sortDirection);
    return newsJpaRepository
        .findAllProjectedBy(PageRequest.of(page, size, sort))
        .stream().map(newsMapper::toSummaryModel).toList();
  }

  @Override
  public List<NewsModel> findPageByStatus(String status, int page, int size, String sortColumn, String sortDirection) {
    Sort sort = getSort(sortColumn, sortDirection);
    return newsJpaRepository
        .findProjectedByStatus_Name(status, PageRequest.of(page, size, sort))
        .stream().map(newsMapper::toSummaryModel).toList();
  }
  @Override
  public List<NewsModel> findPageByStatusNames(List<String> statusNames, int page, int size, String sortColumn,
      String sortDirection) {
    if (statusNames == null || statusNames.isEmpty()) {
      return List.of();
    }
    Sort sort = getSort(sortColumn, sortDirection);
    return newsJpaRepository
        .findProjectedByStatus_NameIn(statusNames, PageRequest.of(page, size, sort))
        .stream().map(newsMapper::toSummaryModel).toList();
  }

  @Override
  public List<NewsModel> findPageByFeatured(boolean featured, int page, int size, String sortColumn,
      String sortDirection) {
    Sort sort = getSort(sortColumn, sortDirection);
    return newsJpaRepository
        .findProjectedByIsFeatured(featured, PageRequest.of(page, size, sort))
        .stream().map(newsMapper::toSummaryModel).toList();
  }

  @Override
  public List<NewsModel> findPageByFeaturedAndStatus(boolean featured, String status, int page, int size,
      String sortColumn, String sortDirection) {
    Sort sort = getSort(sortColumn, sortDirection);
    return newsJpaRepository
        .findProjectedByIsFeaturedAndStatus_Name(featured, status, PageRequest.of(page, size, sort))
        .stream().map(newsMapper::toSummaryModel).toList();
  }
  @Override
  public List<NewsModel> findPageByFeaturedAndStatusNames(boolean featured, List<String> statusNames, int page,
      int size, String sortColumn, String sortDirection) {
    if (statusNames == null || statusNames.isEmpty()) {
      return List.of();
    }
    Sort sort = getSort(sortColumn, sortDirection);
    return newsJpaRepository
        .findProjectedByIsFeaturedAndStatus_NameIn(featured, statusNames, PageRequest.of(page, size, sort))
        .stream().map(newsMapper::toSummaryModel).toList();
  }

  @Override
  public List<NewsModel> findPageByFeaturedAndStatusId(boolean featured, Long statusId, int page, int size,
      String sortColumn, String sortDirection) {
    Sort sort = getSort(sortColumn, sortDirection);
    return newsJpaRepository
        .findProjectedByIsFeaturedAndStatus_Id(featured, statusId, PageRequest.of(page, size, sort))
        .stream().map(newsMapper::toSummaryModel).toList();
  }

  @Override
  public List<NewsModel> findPageByTopic(Long topicId, int page, int size, String sortColumn, String sortDirection) {
    Sort sort = getSort(sortColumn, sortDirection);
    return newsJpaRepository
        .findProjectedByTopics_IdAndStatus_Name(topicId, STATUS_APPROVED, PageRequest.of(page, size, sort))
        .stream().map(newsMapper::toSummaryModel).toList();
  }
  @Override
  public List<NewsModel> findPageByTopicAndStatusNames(Long topicId, List<String> statusNames, int page, int size,
      String sortColumn, String sortDirection) {
    if (statusNames == null || statusNames.isEmpty()) {
      return List.of();
    }
    Sort sort = getSort(sortColumn, sortDirection);
    return newsJpaRepository
        .findProjectedByTopics_IdAndStatus_NameIn(topicId, statusNames, PageRequest.of(page, size, sort))
        .stream().map(newsMapper::toSummaryModel).toList();
  }

  @Override
  public List<NewsModel> findPageByStatusAndTitle(String status, String title, int page, int size, String sortColumn,
      String sortDirection) {
    String searchTitle = (title == null) ? "" : title.trim();
    String searchStatus = (status == null) ? "" : status;
    Sort sort = getSort(sortColumn, sortDirection);
    log.debug("[NewsRepositoryImpl] Search: status={}, title='{}', page={}, size={}, sortCol={}, sortDir={}",
        searchStatus, searchTitle, page, size, sort.toString(), sortDirection);
    return newsJpaRepository
        .findProjectedByStatus_NameAndTitleContainingIgnoreCase(searchStatus, searchTitle,
            PageRequest.of(page, size, sort))
        .stream().map(newsMapper::toSummaryModel).toList();
  }
  @Override
  public List<NewsModel> findPageByStatusNamesAndTitle(List<String> statusNames, String title, int page, int size,
      String sortColumn, String sortDirection) {
    if (statusNames == null || statusNames.isEmpty()) {
      return List.of();
    }
    String searchTitle = (title == null) ? "" : title.trim();
    Sort sort = getSort(sortColumn, sortDirection);
    return newsJpaRepository
        .findProjectedByStatus_NameInAndTitleContainingIgnoreCase(statusNames, searchTitle, PageRequest.of(page, size, sort))
        .stream().map(newsMapper::toSummaryModel).toList();
  }

  @Override
  public List<NewsModel> findPageByDateRange(long start, long end, int page, int size, String sortColumn,
      String sortDirection) {
    Sort sort = getSort(sortColumn, sortDirection);

    return newsJpaRepository
        .findAllByCreatedAtBetween(start, end, PageRequest.of(page, size, sort))
        .stream().map(newsMapper::toSummaryModel).toList();
  }

  @Override
  public long countByDateRange(long start, long end) {
    return newsJpaRepository.countByCreatedAtBetween(start, end);
  }

  @Override
  public long count() {
    return newsJpaRepository.count();
  }

  @Override
  public long countByStatus(String status) {
    return newsJpaRepository.countByStatus_Name(status);
  }
  @Override
  public long countByStatusNames(List<String> statusNames) {
    if (statusNames == null || statusNames.isEmpty()) {
      return 0;
    }
    return newsJpaRepository.countByStatus_NameIn(statusNames);
  }

  @Override
  public long countByStatusAndTitle(String status, String title) {
    return newsJpaRepository.countByStatus_NameAndTitleContainingIgnoreCase(status, title);
  }
  @Override
  public long countByStatusNamesAndTitle(List<String> statusNames, String title) {
    if (statusNames == null || statusNames.isEmpty()) {
      return 0;
    }
    String searchTitle = (title == null) ? "" : title.trim();
    return newsJpaRepository.countByStatus_NameInAndTitleContainingIgnoreCase(statusNames, searchTitle);
  }

  @Override
  public long countByFeatured(boolean featured) {
    return newsJpaRepository.countByIsFeatured(featured);
  }

  @Override
  public long countByFeaturedAndStatus(boolean featured, String status) {
    return newsJpaRepository.countByIsFeaturedAndStatus_Name(featured, status);
  }
  @Override
  public long countByFeaturedAndStatusNames(boolean featured, List<String> statusNames) {
    if (statusNames == null || statusNames.isEmpty()) {
      return 0;
    }
    return newsJpaRepository.countByIsFeaturedAndStatus_NameIn(featured, statusNames);
  }

  @Override
  public long countByFeaturedAndStatusId(boolean featured, Long statusId) {
    return newsJpaRepository.countByIsFeaturedAndStatus_Id(featured, statusId);
  }

  @Override
  public void deleteById(Long id) {
    newsJpaRepository.deleteById(id);
  }

  @Override
  public boolean existsById(Long id) {
    return newsJpaRepository.existsById(id);
  }

  @Override
  public NewsModel update(NewsModel model) {
    News entity = newsMapper.toEntity(model);
    return newsMapper.toModel(newsJpaRepository.save(entity));
  }

  @Override
  public void updateApprovalTicketId(Long newsId, Long approvalTicketId, Long updatedAt) {
    newsJpaRepository.updateApprovalTicketId(newsId, approvalTicketId, updatedAt);
  }
}
