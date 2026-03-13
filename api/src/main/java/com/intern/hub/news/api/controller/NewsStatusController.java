package com.intern.hub.news.api.controller;

import org.springframework.web.bind.annotation.*;
import com.intern.hub.library.common.dto.ResponseApi;
import com.intern.hub.news.api.dto.response.NewsStatusResponse;
import com.intern.hub.news.api.mapper.NewsStatusMapper;
import com.intern.hub.news.core.domain.model.NewsStatusModel;
import com.intern.hub.news.core.domain.usecase.NewsStatusUsecase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/news/statuses")
public class NewsStatusController {

    private final NewsStatusUsecase newsStatusUsecase;

    @PostMapping
    public ResponseApi<NewsStatusResponse> create(@RequestBody @Valid NewsStatusResponse request) {
        NewsStatusModel model = NewsStatusMapper.toModel(request);
        return ResponseApi.ok(NewsStatusMapper.toResponse(newsStatusUsecase.create(model)));
    }

    @GetMapping("/{id}")
    public ResponseApi<NewsStatusResponse> getById(@PathVariable Long id) {
        return ResponseApi.ok(NewsStatusMapper.toResponse(newsStatusUsecase.getById(id)));
    }

    @GetMapping
    public ResponseApi<List<NewsStatusResponse>> getAll() {
        List<NewsStatusResponse> response = newsStatusUsecase.getAll().stream().map(NewsStatusMapper::toResponse).toList();
        return ResponseApi.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseApi<NewsStatusResponse> update(@PathVariable Long id,
            @RequestBody @Valid NewsStatusResponse request) {
        NewsStatusModel model = NewsStatusMapper.toModel(request);
        model.setId(id);
        return ResponseApi.ok(NewsStatusMapper.toResponse(newsStatusUsecase.update(model)));
    }

    @DeleteMapping("/{id}")
    public ResponseApi<String> delete(@PathVariable Long id) {
        newsStatusUsecase.delete(id);
        return ResponseApi.ok("Delete Successfully");
    }
}