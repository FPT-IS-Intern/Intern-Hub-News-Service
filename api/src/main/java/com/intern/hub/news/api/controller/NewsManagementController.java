package com.intern.hub.news.api.controller;

import com.intern.hub.library.common.dto.PaginatedData;
import com.intern.hub.library.common.dto.ResponseApi;
import com.intern.hub.news.api.dto.response.NewsResponse;
import com.intern.hub.news.api.mapper.NewsMapper;
import com.intern.hub.news.core.domain.model.NewsModel;
import com.intern.hub.news.core.domain.usecase.NewsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/news/management")
public class NewsManagementController {

    private final NewsUseCase newsUsecase;
    private final NewsMapper newsMapper;

    @GetMapping
    public ResponseApi<PaginatedData<NewsResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long startDate,
            @RequestParam(required = false) Long endDate,
            @RequestParam(defaultValue = "created_at") String sortColumn,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        long start = startDate != null ? startDate : 0L;
        long end = endDate != null ? endDate : Long.MAX_VALUE;

        PaginatedData<NewsModel> pageData = newsUsecase.findPageByDateRange(start, end, page, size, sortColumn,
                sortDirection);
        List<NewsResponse> items = pageData.getItems().stream()
                .map(newsMapper::toSummaryResponse).toList();
        PaginatedData<NewsResponse> paginatedData = new PaginatedData<>(items, pageData.getTotalItems(), size);
        return ResponseApi.ok(paginatedData);
    }

    @GetMapping("/pending")
    public ResponseApi<PaginatedData<NewsResponse>> getPendingNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "created_at") String sortColumn,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        PaginatedData<NewsModel> pageData = newsUsecase.getPendingNews(page, size, sortColumn, sortDirection);
        List<NewsResponse> items = pageData.getItems().stream()
                .map(newsMapper::toSummaryResponse).toList();
        PaginatedData<NewsResponse> paginatedData = new PaginatedData<>(items, pageData.getTotalItems(), size);
        return ResponseApi.ok(paginatedData);
    }

    @PostMapping("/{id}/approve")
    public ResponseApi<NewsResponse> approve(@PathVariable Long id) {
        NewsModel approvedModel = newsUsecase.approve(id);
        return ResponseApi.ok(newsMapper.toResponse(approvedModel));
    }

    @DeleteMapping("/{id}")
    public ResponseApi<String> delete(@PathVariable Long id) {
        newsUsecase.delete(id);
        return ResponseApi.ok("Delete Successfully");
    }
}
