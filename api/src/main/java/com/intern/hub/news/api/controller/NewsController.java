package com.intern.hub.news.api.controller;

import com.intern.hub.library.common.dto.PaginatedData;
import com.intern.hub.library.common.dto.ResponseApi;
import com.intern.hub.news.api.dto.response.NewsBriefResponse;
import com.intern.hub.news.api.dto.response.NewsResponse;
import com.intern.hub.news.api.mapper.NewsMapper;
import com.intern.hub.news.api.dto.request.SearchNewsRequest;
import com.intern.hub.news.core.domain.model.NewsModel;
import com.intern.hub.news.core.domain.usecase.NewsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/news")
public class NewsController {

  private final NewsUseCase newsUsecase;
  private final NewsMapper newsMapper;

  @GetMapping("/{id:[0-9]+}")
  public ResponseApi<NewsResponse> getById(@PathVariable Long id) {
    return ResponseApi.ok(newsMapper.toResponse(newsUsecase.getById(id)));
  }

  @GetMapping("/isFeatured")
  public ResponseApi<PaginatedData<NewsResponse>> getAllNewsIsFeatured(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "created_at") String sortColumn,
      @RequestParam(defaultValue = "desc") String sortDirection) {
    PaginatedData<NewsModel> pageData = newsUsecase.getAllNewsIsFeatured(page, size, sortColumn, sortDirection);
    List<NewsResponse> items = pageData.getItems().stream()
        .map(newsMapper::toSummaryResponse).toList();
    PaginatedData<NewsResponse> paginatedData = new PaginatedData<>(items, pageData.getTotalItems(), size);
    return ResponseApi.ok(paginatedData);
  }

  @GetMapping("/approved")
  public ResponseApi<PaginatedData<NewsResponse>> getApprovedNews(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "created_at") String sortColumn,
      @RequestParam(defaultValue = "desc") String sortDirection) {
    PaginatedData<NewsModel> pageData = newsUsecase.getApprovedNews(page, size, sortColumn, sortDirection);
    List<NewsResponse> items = pageData.getItems().stream()
        .map(newsMapper::toSummaryResponse).toList();
    PaginatedData<NewsResponse> paginatedData = new PaginatedData<>(items, pageData.getTotalItems(), size);
    return ResponseApi.ok(paginatedData);
  }

  @PostMapping("/search")
  public ResponseApi<PaginatedData<NewsResponse>> searchApprovedNewsByTitle(
      @RequestBody SearchNewsRequest request) {
    int page = request.getPage();
    int size = request.getSize() > 0 ? request.getSize() : 10;
    String sortColumn = request.getSortColumn() != null ? request.getSortColumn() : "createdAt";
    String sortDirection = request.getSortDirection() != null ? request.getSortDirection() : "desc";

    PaginatedData<NewsModel> pageData = newsUsecase.searchApprovedNewsByTitle(request.getTitle(), page, size,
        sortColumn, sortDirection);
    List<NewsResponse> items = pageData.getItems().stream()
        .map(newsMapper::toSummaryResponse).toList();
    PaginatedData<NewsResponse> paginatedData = new PaginatedData<>(items, pageData.getTotalItems(), size);
    return ResponseApi.ok(paginatedData);
  }

  @GetMapping("/by-topic/{topicId}")
  public ResponseApi<PaginatedData<NewsResponse>> getApprovedNewsByTopic(
      @PathVariable Long topicId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "created_at") String sortColumn,
      @RequestParam(defaultValue = "desc") String sortDirection) {
    PaginatedData<NewsModel> pageData = newsUsecase.getApprovedNewsByTopic(topicId, page, size, sortColumn,
        sortDirection);
    List<NewsResponse> items = pageData.getItems().stream()
        .map(newsMapper::toSummaryResponse).toList();
    PaginatedData<NewsResponse> paginatedData = new PaginatedData<>(items, pageData.getTotalItems(), size);
    return ResponseApi.ok(paginatedData);
  }

  @GetMapping("/latest")
  public ResponseApi<List<NewsResponse>> getTop3LatestNews() {
    List<NewsResponse> latest = newsUsecase.getTop3LatestNews().stream().map(newsMapper::toSummaryResponse).toList();
    return ResponseApi.ok(latest);
  }

  @GetMapping("/latest-featured")
  public ResponseApi<List<NewsBriefResponse>> getLatestFeaturedBrief(@RequestParam(defaultValue = "5") int total) {
    List<NewsBriefResponse> featured = newsUsecase.getLatestFeaturedNews(total).stream()
        .map(model -> {
          var brief = new NewsBriefResponse();
          brief.setId(model.getId());
          brief.setThumbNail(model.getThumbnail());
          return brief;
        }).toList();
    return ResponseApi.ok(featured);
  }
}
