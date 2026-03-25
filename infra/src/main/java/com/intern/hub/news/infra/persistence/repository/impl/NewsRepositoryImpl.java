package com.intern.hub.news.infra.persistence.repository.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.intern.hub.news.core.domain.model.NewsModel;
import com.intern.hub.news.core.domain.port.NewsRepository;
import com.intern.hub.news.infra.mapper.NewsEntityMapper;
import com.intern.hub.news.infra.persistence.entity.News;
import com.intern.hub.news.infra.persistence.repository.jpa.NewsJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NewsRepositoryImpl implements NewsRepository {

  private final NewsJpaRepository newsJpaRepository;
  private final NewsEntityMapper newsMapper;

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
  public List<NewsModel> findAll() {
    return newsJpaRepository.findAll().stream().map(newsMapper::toModel).toList();
  }

  @Override
  public List<NewsModel> findPage(int page, int size) {
    return newsJpaRepository
        .findAllProjectedBy(org.springframework.data.domain.PageRequest.of(page, size,
            org.springframework.data.domain.Sort.by("createdAt").descending()))
        .stream().map(newsMapper::toSummaryModel).toList();
  }

  @Override
  public List<NewsModel> findPageByStatus(String status, int page, int size) {
    return newsJpaRepository
        .findProjectedByStatus_Name(status,
            org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by("createdAt").descending()))
        .stream().map(newsMapper::toSummaryModel).toList();
  }

  @Override
  public List<NewsModel> findPageByFeatured(boolean featured, int page, int size) {
    return newsJpaRepository
        .findProjectedByIsFeatured(featured,
            org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by("updatedAt").descending()))
        .stream().map(newsMapper::toSummaryModel).toList();
  }

  @Override
  public List<NewsModel> findPageByTopic(Long topicId, int page, int size) {
    return newsJpaRepository
        .findProjectedByTopics_IdAndStatus_Name(topicId, "PUBLIC",
            org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by("createdAt").descending()))
        .stream().map(newsMapper::toSummaryModel).toList();
  }

  @Override
  public List<NewsModel> findPageByDateRange(long start, long end, int page, int size, String sortColumn,
      String sortDirection) {
    org.springframework.data.domain.Sort sort = sortDirection.equalsIgnoreCase("asc")
        ? org.springframework.data.domain.Sort.by(sortColumn).ascending()
        : org.springframework.data.domain.Sort.by(sortColumn).descending();

    return newsJpaRepository
        .findAllByCreatedAtBetween(start, end, org.springframework.data.domain.PageRequest.of(page, size, sort))
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
  public long countByFeatured(boolean featured) {
    return newsJpaRepository.countByIsFeatured(featured);
  }

  @Override
  public void deleteById(Long id) {
    newsJpaRepository.deleteById(id);
  }

  @Override
  public boolean existsById(Long id) {
    return newsJpaRepository.existsById(id);
  }
}
