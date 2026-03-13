package com.intern.hub.news.core.domain.port;

import com.intern.hub.news.core.domain.model.NewsStatusModel;

import java.util.List;
import java.util.Optional;

public interface NewsStatusRepository {
    NewsStatusModel create(NewsStatusModel model);

    Optional<NewsStatusModel> findById(Long id);

    List<NewsStatusModel> findAll();

    void deleteById(Long id);

    boolean existsById(Long id);
}