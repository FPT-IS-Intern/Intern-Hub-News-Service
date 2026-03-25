package com.intern.hub.news.api.controller;

import org.springframework.web.bind.annotation.*;
import com.intern.hub.library.common.dto.ResponseApi;
import com.intern.hub.news.api.dto.response.NewsTopicResponse;
import com.intern.hub.news.core.domain.model.NewsTopicModel;
import com.intern.hub.news.core.domain.usecase.NewsTopicUsecase;
import com.intern.hub.news.api.mapper.NewsTopicMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/news/topic")
public class NewsTopicController {

    private final NewsTopicUsecase newsTopicUsecase;

    @PostMapping
    public ResponseApi<NewsTopicResponse> create(@RequestBody @Valid NewsTopicResponse request) {
        NewsTopicModel model = NewsTopicMapper.toModel(request);
        return ResponseApi.ok(NewsTopicMapper.toResponse(newsTopicUsecase.create(model)));
    }

    @GetMapping("/{id}")
    public ResponseApi<NewsTopicResponse> getById(@PathVariable Long id) {
        return ResponseApi.ok(NewsTopicMapper.toResponse(newsTopicUsecase.getById(id)));
    }

    @GetMapping
    public ResponseApi<List<NewsTopicResponse>> getAll() {
        List<NewsTopicResponse> response = newsTopicUsecase.getAll().stream().map(NewsTopicMapper::toResponse).toList();
        return ResponseApi.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseApi<NewsTopicResponse> update(@PathVariable Long id, @RequestBody @Valid NewsTopicResponse request) {
        NewsTopicModel model = NewsTopicMapper.toModel(request);
        model.setId(id);
        return ResponseApi.ok(NewsTopicMapper.toResponse(newsTopicUsecase.update(model)));
    }

    @DeleteMapping("/{id}")
    public ResponseApi<String> delete(@PathVariable Long id) {
        newsTopicUsecase.delete(id);
        return ResponseApi.ok("Delete Successfully");
    }
}
