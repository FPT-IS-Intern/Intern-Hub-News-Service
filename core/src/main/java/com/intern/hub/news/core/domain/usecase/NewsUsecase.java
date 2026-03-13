package com.intern.hub.news.core.domain.usecase;

import com.intern.hub.news.core.domain.model.NewsModel;
import java.util.List;

public interface NewsUsecase {

  NewsModel create(String title, String body, String thumbnail, Long topicId, boolean featured);

  NewsModel update(Long id, String title, String body, Long topicId, boolean featured);

  NewsModel approve(Long id);

  NewsModel getById(Long id);

  List<NewsModel> getAll();

  void delete(Long id);
}

