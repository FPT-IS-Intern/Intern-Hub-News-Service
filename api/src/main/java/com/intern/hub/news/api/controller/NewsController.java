package com.intern.hub.news.api.controller;

import com.intern.hub.library.common.dto.ResponseApi;
import com.intern.hub.news.api.dto.request.CreateNewsRequest;
import com.intern.hub.news.api.dto.request.UpdateNewsRequest;
import com.intern.hub.news.api.dto.response.NewsResponse;
import com.intern.hub.news.core.domain.model.NewsModel;
import com.intern.hub.news.core.domain.usecase.NewsUsecase;
import jakarta.validation.Valid;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/news")
public class NewsController {

  private final NewsUsecase newsUsecase;

  @PostMapping
  public ResponseApi<NewsResponse> create(@RequestBody @Valid CreateNewsRequest request) {
    return ResponseApi.ok(toResponse(newsUsecase.create(
        request.getTitle(),
        request.getBody(),
        request.getThumbnail(),
        request.getTopicId(),
        request.getFeatured()
    )));
  }

  @GetMapping("/{id}")
  public ResponseApi<NewsResponse> getById(@PathVariable Long id) {
    return ResponseApi.ok(toResponse(newsUsecase.getById(id)));
  }

  @GetMapping
  public ResponseApi<List<NewsResponse>> getAll() {
    List<NewsResponse> response = newsUsecase.getAll().stream().map(this::toResponse).toList();
    return ResponseApi.ok(response);
  }

  @PutMapping("/{id}")
  public ResponseApi<NewsResponse> update(@PathVariable Long id, @RequestBody @Valid UpdateNewsRequest request) {
    return ResponseApi.ok(toResponse(newsUsecase.update(
        id,
        request.getTitle(),
        request.getBody(),
        request.getTopicId(),
        request.getFeatured()
    )));
  }

  @PostMapping("/{id}/approve")
  public ResponseApi<NewsResponse> approve(@PathVariable Long id) {
    return ResponseApi.ok(toResponse(newsUsecase.approve(id)));
  }

  @DeleteMapping("/{id}")
  public ResponseApi<String> delete(@PathVariable Long id) {
    newsUsecase.delete(id);
    return ResponseApi.ok("Delete Successfully");
  }

  private NewsResponse toResponse(NewsModel model) {
    var response = new NewsResponse();
    response.setId(model.getId());
    response.setTitle(model.getTitle());
    response.setBody(model.getBody());
    response.setTopicName(model.getTopicName());
    response.setFeatured(model.isFeatured());
    response.setCreatedAt(model.getCreatedAt());
    response.setUpdatedAt(model.getUpdatedAt());

    return response;
  }
}

