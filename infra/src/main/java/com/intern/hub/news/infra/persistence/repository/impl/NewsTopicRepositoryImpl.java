package com.intern.hub.news.infra.persistence.repository.impl;

import com.intern.hub.news.core.domain.model.NewsTopicModel;
import com.intern.hub.news.core.domain.port.NewsTopicRepository;
import com.intern.hub.news.infra.mapper.NewsTopicMapper;
import com.intern.hub.news.infra.persistence.repository.jpa.NewsTopicJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class NewsTopicRepositoryImpl implements NewsTopicRepository {

    private final NewsTopicJpaRepository newsTopicJpaRepository;
    private final NewsTopicMapper newsTopicMapper;

    @Override
    public NewsTopicModel create(NewsTopicModel model) {
        var newsTopicModel = new NewsTopicModel();
        newsTopicModel.setId(model.getId());
        newsTopicModel.setName(model.getName());
        newsTopicModel.setDescription(model.getDescription());
        newsTopicJpaRepository.save(newsTopicMapper.toEntity(model));
        return newsTopicModel;
    }

    @Override
    public Optional<NewsTopicModel> findById(Long id) {
        return newsTopicJpaRepository.findById(id).map(newsTopicMapper::toModel);
    }

    @Override
    public List<NewsTopicModel> findAll() {
        return newsTopicJpaRepository.findAll().stream().map(newsTopicMapper::toModel).toList();
    }

    @Override
    public void deleteById(Long id) {
        if (!newsTopicJpaRepository.existsById(id)) {
            throw new EntityNotFoundException("News Topic with id " + id + " not found");
        }
        newsTopicJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return newsTopicJpaRepository.existsById(id);
    }
}
