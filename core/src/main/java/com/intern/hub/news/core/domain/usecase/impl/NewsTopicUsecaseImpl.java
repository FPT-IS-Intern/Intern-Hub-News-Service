package com.intern.hub.news.core.domain.usecase.impl;

import com.intern.hub.news.core.domain.model.NewsTopicModel;
import com.intern.hub.news.core.domain.port.NewsTopicRepository;
import com.intern.hub.news.core.domain.usecase.NewsTopicUsecase;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class NewsTopicUsecaseImpl implements NewsTopicUsecase {

    private final NewsTopicRepository newsTopicRepository;

    @Override
    public NewsTopicModel create(NewsTopicModel entity) {
        return newsTopicRepository.create(entity);
    }

    @Override
    public NewsTopicModel update(NewsTopicModel entity) {
        if (!newsTopicRepository.existsById(entity.getId())) {
            throw new IllegalArgumentException("NewsTopic not found with id: " + entity.getId());
        }
        return newsTopicRepository.create(entity);
    }

    @Override
    public NewsTopicModel getById(Long id) {
        return newsTopicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("NewsTopic not found with id: " + id));
    }

    @Override
    public List<NewsTopicModel> getAll() {
        return newsTopicRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        if (!newsTopicRepository.existsById(id)) {
            throw new IllegalArgumentException("NewsTopic not found with id: " + id);
        }
        newsTopicRepository.deleteById(id);
    }
}