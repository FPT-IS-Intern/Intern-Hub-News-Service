package com.intern.hub.news.infra.persistence.repository.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.intern.hub.news.core.domain.model.NewsModel;
import com.intern.hub.news.core.domain.port.NewsRepository;
import com.intern.hub.news.infra.mapper.NewsEntityMapper;
import com.intern.hub.news.infra.persistence.entity.News;
import com.intern.hub.news.infra.persistence.repository.jpa.NewsJpaRepository;
import com.intern.hub.news.infra.persistence.repository.jpa.NewsStatusJpaRepository;
import com.intern.hub.news.infra.persistence.repository.jpa.NewsTopicJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NewsRepositoryImpl implements NewsRepository {

  private final NewsJpaRepository newsJpaRepository;
  private final NewsEntityMapper newsMapper;
  private final NewsTopicJpaRepository newsTopicJpaRepository;
  private final NewsStatusJpaRepository newsStatusJpaRepository;

  @Override
  public NewsModel create(NewsModel model) {
    News entity = newsMapper.toEntity(model);

    if (entity.getTopic() != null && entity.getTopic().getId() != null) {
      entity.setTopic(newsTopicJpaRepository.findById(entity.getTopic().getId()).orElse(null));
    }

    if (entity.getStatus() != null && entity.getStatus().getId() != null) {
      entity.setStatus(newsStatusJpaRepository.findById(entity.getStatus().getId()).orElse(null));
    } else if (model.getStatus() != null && !model.getStatus().isBlank()) {
      entity.setStatus(newsStatusJpaRepository.findByName(model.getStatus()).orElse(null));
    }

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
  public void deleteById(Long id) {
    newsJpaRepository.deleteById(id);
  }

  @Override
  public boolean existsById(Long id) {
    return newsJpaRepository.existsById(id);
  }
}
