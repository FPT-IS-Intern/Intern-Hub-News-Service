package com.intern.hub.news.infra.persistence.repository.impl;

import com.intern.hub.news.core.domain.model.NewsModel;
import com.intern.hub.news.core.domain.port.NewsRepository;
import com.intern.hub.news.infra.mapper.NewsMapper;
import com.intern.hub.news.infra.persistence.repository.jpa.NewsJpaRepository;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NewsRepositoryImpl implements NewsRepository {

  private final NewsJpaRepository newsJpaRepository;
  private final NewsMapper newsMapper;

  @Override
  public NewsModel create(NewsModel model) {
    return newsMapper.toModel(newsJpaRepository.save(newsMapper.toEntity(model)));
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
