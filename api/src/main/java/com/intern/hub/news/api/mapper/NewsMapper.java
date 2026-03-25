package com.intern.hub.news.api.mapper;

import com.intern.hub.news.api.dto.response.NewsResponse;
import com.intern.hub.news.core.domain.model.NewsModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewsMapper {

    public NewsResponse toResponse(NewsModel model) {
        var response = new NewsResponse();
        response.setId(model.getId());
        response.setTitle(model.getTitle());
        response.setBody(model.getBody());
        response.setShortDescription(model.getShortDescription());
        response.setThumbNail(model.getThumbnail());
        response.setTopics(model.getTopics() != null 
            ? model.getTopics().stream().map(NewsTopicMapper::toResponse).toList() 
            : new java.util.ArrayList<>());
        response.setStatus(model.getStatus());
        response.setFeatured(model.isFeatured());
        response.setCreatedAt(model.getCreatedAt());
        response.setUpdatedAt(model.getUpdatedAt());
        response.setCreatedBy(model.getCreatedBy());
        return response;
    }

    public NewsResponse toSummaryResponse(NewsModel model) {
        var response = new NewsResponse();
        response.setId(model.getId());
        response.setTitle(model.getTitle());
        response.setShortDescription(model.getShortDescription());
        response.setThumbNail(model.getThumbnail());
        response.setTopics(model.getTopics() != null 
            ? model.getTopics().stream().map(NewsTopicMapper::toResponse).toList() 
            : new java.util.ArrayList<>());
        response.setStatus(model.getStatus());
        response.setFeatured(model.isFeatured());
        response.setCreatedAt(model.getCreatedAt());
        response.setUpdatedAt(model.getUpdatedAt());
        response.setCreatedBy(model.getCreatedBy());
        return response;
    }
}
