package com.intern.hub.news.core.domain.port;

import com.intern.hub.news.core.domain.model.NewsModel;
import java.util.List;
import java.util.Optional;

public interface NewsRepository {

  NewsModel create(NewsModel model);

  Optional<NewsModel> findById(Long id);

  List<NewsModel> findAll();

  void deleteById(Long id);

  boolean existsById(Long id);
}

