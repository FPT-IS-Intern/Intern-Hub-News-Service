package com.intern.hub.news.core.domain.usecase;

import java.util.List;

import com.intern.hub.news.core.domain.model.NewsModel;

public interface NewsUsecase {

  NewsModel create(String title, String body, String thumbnail, Long topicId, Long statusId, boolean featured);

  NewsModel update(Long id, String title, String body, Long topicId, boolean featured);

  NewsModel approve(Long id);

  NewsModel getById(Long id);

  List<NewsModel> getAll();

  List<NewsModel> getAllNewsIsFeatured();

  void delete(Long id);
}
