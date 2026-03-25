package com.intern.hub.news.core.domain.port;

import com.intern.hub.news.core.domain.model.NewsModel;
import java.util.List;
import java.util.Optional;

public interface NewsRepository {

  NewsModel create(NewsModel model);

  Optional<NewsModel> findById(Long id);

  List<NewsModel> findAll();

  List<NewsModel> findPage(int page, int size);

  List<NewsModel> findPageByStatus(String status, int page, int size);

  List<NewsModel> findPageByFeatured(boolean featured, int page, int size);
  List<NewsModel> findPageByTopic(Long topicId, int page, int size);

  List<NewsModel> findPageByDateRange(long start, long end, int page, int size, String sortColumn, String sortDirection);

  long countByDateRange(long start, long end);

  long count();

  long countByStatus(String status);

  long countByFeatured(boolean featured);

  void deleteById(Long id);

  boolean existsById(Long id);

  NewsModel update(NewsModel model);

}
