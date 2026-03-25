package com.intern.hub.news.core.domain.usecase;

import com.intern.hub.news.core.domain.model.NewsTopicModel;
import java.util.List;

public interface NewsTopicUseCase {
    NewsTopicModel create(NewsTopicModel entity);
    NewsTopicModel update(NewsTopicModel entity);
    NewsTopicModel getById(Long id);
    List<NewsTopicModel> getAll();
    void delete(Long id);
}

