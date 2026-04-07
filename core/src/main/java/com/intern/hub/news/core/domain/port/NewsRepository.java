package com.intern.hub.news.core.domain.port;

import com.intern.hub.news.core.domain.model.NewsModel;
import java.util.List;
import java.util.Optional;

public interface NewsRepository {

  NewsModel create(NewsModel model);

  Optional<NewsModel> findById(Long id);

  Optional<NewsModel> findByApprovalTicketId(Long approvalTicketId);

  List<NewsModel> findAll();

  List<NewsModel> findPage(int page, int size, String sortColumn, String sortDirection);
  List<NewsModel> findPageByStatus(String status, int page, int size, String sortColumn, String sortDirection);
  List<NewsModel> findPageByStatusNames(List<String> statusNames, int page, int size, String sortColumn, String sortDirection);
  List<NewsModel> findPageByFeatured(boolean featured, int page, int size, String sortColumn, String sortDirection);
  List<NewsModel> findPageByFeaturedAndStatus(boolean featured, String status, int page, int size, String sortColumn, String sortDirection);
  List<NewsModel> findPageByFeaturedAndStatusNames(boolean featured, List<String> statusNames, int page, int size, String sortColumn, String sortDirection);
  List<NewsModel> findPageByFeaturedAndStatusId(boolean featured, Long statusId, int page, int size,
      String sortColumn, String sortDirection);
  List<NewsModel> findPageByTopic(Long topicId, int page, int size, String sortColumn, String sortDirection);
  List<NewsModel> findPageByTopicAndStatusNames(Long topicId, List<String> statusNames, int page, int size, String sortColumn, String sortDirection);
  List<NewsModel> findPageByStatusAndTitle(String status, String title, int page, int size, String sortColumn, String sortDirection);
  List<NewsModel> findPageByStatusNamesAndTitle(List<String> statusNames, String title, int page, int size, String sortColumn, String sortDirection);
  List<NewsModel> findPageByDateRange(long start, long end, int page, int size, String sortColumn, String sortDirection);

  long countByDateRange(long start, long end);

  long count();

  long countByStatus(String status);
  long countByStatusNames(List<String> statusNames);
  long countByStatusAndTitle(String status, String title);
  long countByStatusNamesAndTitle(List<String> statusNames, String title);

  long countByFeatured(boolean featured);
  long countByFeaturedAndStatus(boolean featured, String status);
  long countByFeaturedAndStatusNames(boolean featured, List<String> statusNames);
  long countByFeaturedAndStatusId(boolean featured, Long statusId);

  void deleteById(Long id);

  boolean existsById(Long id);

  NewsModel update(NewsModel model);

  void updateApprovalTicketId(Long newsId, Long approvalTicketId, Long updatedAt);

}
