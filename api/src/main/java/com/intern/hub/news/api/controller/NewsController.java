package com.intern.hub.news.api.controller;

import com.intern.hub.library.common.dto.PaginatedData;
import com.intern.hub.library.common.dto.ResponseApi;
import com.intern.hub.news.api.dto.request.CreateNewsRequest;
import com.intern.hub.news.api.dto.request.UpdateNewsRequest;
import com.intern.hub.news.api.dto.response.NewsResponse;
import com.intern.hub.news.api.mapper.NewsMapper;
import com.intern.hub.news.core.domain.usecase.NewsUsecase;
import jakarta.validation.Valid;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/news")
public class NewsController {

  private final NewsUsecase newsUsecase;
  private final NewsMapper newsMapper;

  @PostMapping
  public ResponseApi<NewsResponse> create(@RequestBody @Valid CreateNewsRequest request) {
    return ResponseApi.ok(newsMapper.toResponse(newsUsecase.create(
        request.getTitle(),
        request.getBody(),
        request.getThumbnail(),
        request.getTopicId(),
        request.getStatusId(),
        request.getFeatured()
    )));
  }

  @GetMapping("/{id}")
  public ResponseApi<NewsResponse> getById(@PathVariable Long id) {
    return ResponseApi.ok(newsMapper.toResponse(newsUsecase.getById(id)));
  }

  @GetMapping
  public ResponseApi<PaginatedData<NewsResponse>> getAll(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    List<NewsResponse> all = newsUsecase.getAll().stream().map(newsMapper::toSummaryResponse).toList();
    int total = all.size();
    int fromIndex = Math.min(page * size, total);
    int toIndex = Math.min(fromIndex + size, total);
    List<NewsResponse> paged = all.subList(fromIndex, toIndex);
    PaginatedData<NewsResponse> paginatedData = new PaginatedData<>(paged, total, size);
    return ResponseApi.ok(paginatedData);
  }

  @GetMapping("/isFeatured")
  public ResponseApi<List<NewsResponse>> getAllNewsIsFeatured() {
    List<NewsResponse> response = newsUsecase.getAllNewsIsFeatured().stream().map(newsMapper::toSummaryResponse).toList();
    return ResponseApi.ok(response);
  }

  @PutMapping("/{id}")
  public ResponseApi<NewsResponse> update(@PathVariable Long id, @RequestBody @Valid UpdateNewsRequest request) {
    return ResponseApi.ok(newsMapper.toResponse(newsUsecase.update(
        id,
        request.getTitle(),
        request.getBody(),
        request.getTopicId(),
        request.getFeatured()
    )));
  }

  @PostMapping("/{id}/approve")
  public ResponseApi<NewsResponse> approve(@PathVariable Long id) {
    return ResponseApi.ok(newsMapper.toResponse(newsUsecase.approve(id)));
  }

  @DeleteMapping("/{id}")
  public ResponseApi<String> delete(@PathVariable Long id) {
    newsUsecase.delete(id);
    return ResponseApi.ok("Delete Successfully");
  }
}
