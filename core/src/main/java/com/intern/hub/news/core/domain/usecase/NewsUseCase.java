package com.intern.hub.news.core.domain.usecase;

import java.util.List;
import com.intern.hub.library.common.dto.PaginatedData;

import com.intern.hub.news.core.domain.command.CreateNewsCommand;
import com.intern.hub.news.core.domain.command.UpdateNewsCommand;
import com.intern.hub.news.core.domain.model.NewsModel;

public interface NewsUseCase {

  NewsModel create(CreateNewsCommand command);

  NewsModel update(Long id, UpdateNewsCommand command);

  NewsModel approve(Long id);

  NewsModel getById(Long id);

  List<NewsModel> getAll();

  PaginatedData<NewsModel> findPage(int page, int size);

  PaginatedData<NewsModel> findPageByDateRange(long start, long end, int page, int size, String sortColumn, String sortDirection);

  PaginatedData<NewsModel> getApprovedNews(int page, int size, String sortColumn, String sortDirection);
  PaginatedData<NewsModel> searchApprovedNewsByTitle(String title, int page, int size, String sortColumn, String sortDirection);
  PaginatedData<NewsModel> getApprovedNewsByTopic(Long topicId, int page, int size, String sortColumn, String sortDirection);

  PaginatedData<NewsModel> getPendingNews(int page, int size, String sortColumn, String sortDirection);

  PaginatedData<NewsModel> getAllNewsIsFeatured(int page, int size, String sortColumn, String sortDirection);
  List<NewsModel> getLatestFeaturedNews(int total);
  List<NewsModel> getTop3LatestNews();

  void delete(Long id);
}
