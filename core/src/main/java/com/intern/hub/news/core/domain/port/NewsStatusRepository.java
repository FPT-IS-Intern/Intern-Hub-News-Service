package com.intern.hub.news.core.domain.port;

import java.util.List;
import java.util.Optional;

import com.intern.hub.news.core.domain.model.NewsStatusModel;

public interface NewsStatusRepository {
    NewsStatusModel create(NewsStatusModel model);

    Optional<NewsStatusModel> findById(Long id);

    List<NewsStatusModel> findAll();

    void deleteById(Long id);

    Optional<NewsStatusModel> findByName(String name);

    boolean existsById(Long id);
}