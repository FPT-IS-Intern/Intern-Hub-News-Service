package com.intern.hub.news.core.domain.port;

import com.intern.hub.news.core.domain.model.NewsTopicModel;

import java.util.List;
import java.util.Optional;

public interface NewsTopicRepository {

    NewsTopicModel create(NewsTopicModel model);

    Optional<NewsTopicModel> findById(Long id);

    List<NewsTopicModel> findAll();

    void deleteById(Long id);

    boolean existsById(Long id);
}
