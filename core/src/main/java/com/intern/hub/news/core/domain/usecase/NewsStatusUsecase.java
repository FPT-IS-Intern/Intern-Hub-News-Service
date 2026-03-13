package com.intern.hub.news.core.domain.usecase;

import com.intern.hub.news.core.domain.model.NewsStatusModel;

import java.util.List;

public interface NewsStatusUsecase {
    NewsStatusModel create(NewsStatusModel entity);

    NewsStatusModel update(NewsStatusModel entity);

    NewsStatusModel getById(Long id);

    List<NewsStatusModel> getAll();

    void delete(Long id);
}