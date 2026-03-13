package com.intern.hub.news.core.domain.usecase.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.intern.hub.news.core.domain.model.NewsModel;
import com.intern.hub.news.core.domain.port.NewsRepository;
import com.intern.hub.news.core.domain.usecase.NewsUsecase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NewsUsecaseImpl implements NewsUsecase {

  private static final String STATUS_PENDING_APPROVAL = "PENDING_APPROVAL";
  private static final String STATUS_PUBLIC = "PUBLIC";

  private final NewsRepository newsRepository;

  @Override
  public NewsModel create(String title, String body, String thumbnail, Long topicId, Long statusId, boolean featured) {
    validateInput(title, body);
    long now = System.currentTimeMillis();
    NewsModel newsModel = new NewsModel();
    newsModel.setTitle(title);
    newsModel.setBody(body);
    newsModel.setThumbnail(thumbnail);
    newsModel.setTopicId(topicId); // topicId có thể null
    newsModel.setStatusId(statusId);
    newsModel.setFeatured(featured);
    newsModel.setCreatedAt(now);
    newsModel.setUpdatedAt(now);
    return newsRepository.create(newsModel);
  }

  @Override
  public NewsModel update(Long id, String title, String body, Long topicId, boolean featured) {
    validateInput(title, body);
    NewsModel existing = getById(id);
    NewsModel updateModel = new NewsModel();
    updateModel.setId(existing.getId());
    updateModel.setTitle(existing.getTitle());
    updateModel.setBody(existing.getBody());
    updateModel.setTopicId(existing.getTopicId());
    updateModel.setFeatured(featured);
    updateModel.setThumbnail(existing.getThumbnail());
    updateModel.setStatus(STATUS_PENDING_APPROVAL);
    return newsRepository.create(updateModel);
  }

  @Override
  public NewsModel approve(Long id) {
    NewsModel existing = getById(id);
    if (!STATUS_PENDING_APPROVAL.equals(existing.getStatus())) {
      throw new IllegalArgumentException("Only pending news can be approved");
    }
    existing.setStatus(STATUS_PUBLIC);
    return newsRepository.create(existing);
  }

  @Override
  public NewsModel getById(Long id) {
    return newsRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("News not found with id: " + id));
  }

  @Override
  public List<NewsModel> getAll() {
    return newsRepository.findAll();
  }

  @Override
  public List<NewsModel> getAllNewsIsFeatured() {
    return this.getAll().stream()
        .filter(NewsModel::isFeatured)
        .sorted((a, b) -> Long.compare(b.getUpdatedAt(), a.getUpdatedAt()))
        .toList();
  }

  @Override
  public void delete(Long id) {
    if (!newsRepository.existsById(id)) {
      throw new IllegalArgumentException("News not found with id: " + id);
    }
    newsRepository.deleteById(id);
  }

  private void validateInput(String title, String body) {
    if (title == null || title.isBlank()) {
      throw new IllegalArgumentException("Title is required");
    }
    if (body == null || body.isBlank()) {
      throw new IllegalArgumentException("Body is required");
    }
  }
}
