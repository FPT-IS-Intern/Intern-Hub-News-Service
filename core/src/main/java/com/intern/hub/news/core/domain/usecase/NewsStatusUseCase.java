package com.intern.hub.news.core.domain.usecase;

import com.intern.hub.news.core.domain.model.NewsStatusModel;

import java.util.List;

public interface NewsStatusUseCase {
    NewsStatusModel create(NewsStatusModel entity);
    NewsStatusModel create(NewsStatusModel entity, Long userId);

    NewsStatusModel update(NewsStatusModel entity);

    NewsStatusModel getById(Long id);

    List<NewsStatusModel> getAll();

    void delete(Long id);
}