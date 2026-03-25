package com.intern.hub.news.api.controller;

import com.intern.hub.library.common.dto.ResponseApi;
import com.intern.hub.news.api.dto.request.UpdateNewsRequest;
import com.intern.hub.news.api.dto.response.NewsResponse;
import com.intern.hub.news.api.mapper.NewsMapper;
import com.intern.hub.news.core.domain.command.CreateNewsCommand;
import com.intern.hub.news.core.domain.command.UpdateNewsCommand;
import com.intern.hub.news.core.domain.model.NewsModel;
import com.intern.hub.news.core.domain.usecase.NewsUseCase;
import com.intern.hub.starter.security.context.AuthContextHolder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/news/author")
public class NewsAuthorController {

    private final NewsUseCase newsUsecase;
    private final NewsMapper newsMapper;

    @PostMapping
    public ResponseApi<NewsResponse> create(@RequestBody @Valid CreateNewsCommand request) {
        Long userId = AuthContextHolder.get().userId();
        request.setUserId(userId);
        NewsModel createdModel = newsUsecase.create(request);
        return ResponseApi.ok(newsMapper.toResponse(createdModel));
    }

    @PutMapping("/{id}")
    public ResponseApi<NewsResponse> update(@PathVariable Long id, @RequestBody @Valid UpdateNewsRequest request) {
        UpdateNewsCommand command = new UpdateNewsCommand();
        command.setTitle(request.getTitle());
        command.setBody(request.getBody());
        command.setShortDescription(request.getShortDescription());
        command.setTopicIds(request.getTopicIds());
        command.setStatusId(request.getStatusId());
        command.setFeatured(request.getFeatured());
        command.setThumbnail(request.getThumbnail());

        NewsModel updatedModel = newsUsecase.update(id, command);
        return ResponseApi.ok(newsMapper.toResponse(updatedModel));
    }
}
