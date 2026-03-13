package com.intern.hub.news.core.domain.usecase.impl;

import com.intern.hub.news.core.domain.model.NewsStatusModel;
import com.intern.hub.news.core.domain.port.NewsStatusRepository;
import com.intern.hub.news.core.domain.usecase.NewsStatusUsecase;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class NewsStatusUsecaseImpl implements NewsStatusUsecase {

    private final NewsStatusRepository newsStatusRepository;

    @Override
    public NewsStatusModel create(NewsStatusModel entity) {
        return newsStatusRepository.create(entity);
    }

    @Override
    public NewsStatusModel update(NewsStatusModel entity) {
        if (!newsStatusRepository.existsById(entity.getId())) {
            throw new IllegalArgumentException("NewsStatus not found with id: " + entity.getId());
        }
        return newsStatusRepository.create(entity);
    }

    @Override
    public NewsStatusModel getById(Long id) {
        return newsStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("NewsStatus not found with id: " + id));
    }

    @Override
    public List<NewsStatusModel> getAll() {
        return newsStatusRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        if (!newsStatusRepository.existsById(id)) {
            throw new IllegalArgumentException("NewsStatus not found with id: " + id);
        }
        newsStatusRepository.deleteById(id);
    }
}