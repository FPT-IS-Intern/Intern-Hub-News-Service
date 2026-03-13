package com.intern.hub.news.infra.persistence.repository.impl;

import com.intern.hub.news.core.domain.model.NewsStatusModel;
import com.intern.hub.news.core.domain.port.NewsStatusRepository;
import com.intern.hub.news.infra.mapper.NewsStatusMapper;
import com.intern.hub.news.infra.persistence.repository.jpa.NewsStatusJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NewsStatusRepositoryImpl implements NewsStatusRepository {

    private final NewsStatusJpaRepository newsStatusJpaRepository;
    private final NewsStatusMapper newsStatusMapper;

    @Override
    public NewsStatusModel create(NewsStatusModel model) {
        var entity = newsStatusJpaRepository.save(newsStatusMapper.toEntity(model));
        return newsStatusMapper.toModel(entity);
    }

    @Override
    public Optional<NewsStatusModel> findById(Long id) {
        return newsStatusJpaRepository.findById(id).map(newsStatusMapper::toModel);
    }

    @Override
    public List<NewsStatusModel> findAll() {
        return newsStatusJpaRepository.findAll().stream().map(newsStatusMapper::toModel).toList();
    }

    @Override
    public void deleteById(Long id) {
        if (!newsStatusJpaRepository.existsById(id)) {
            throw new EntityNotFoundException("News Status with id " + id + " not found");
        }
        newsStatusJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return newsStatusJpaRepository.existsById(id);
    }
}