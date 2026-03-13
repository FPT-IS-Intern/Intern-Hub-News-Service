package com.intern.hub.news.api.mapper;

import com.intern.hub.news.api.dto.response.NewsTopicResponse;
import com.intern.hub.news.core.domain.model.NewsTopicModel;

public class NewsTopicMapper {
    public static NewsTopicResponse toResponse(NewsTopicModel model) {
        if (model == null) return null;
        return new NewsTopicResponse(
                model.getId() != null ? model.getId().toString() : null,
                model.getName(),
                model.getDescription()
        );
    }

    public static NewsTopicModel toModel(NewsTopicResponse response) {
        if (response == null) return null;
        NewsTopicModel model = new NewsTopicModel();
        if (response.getId() != null) {
            model.setId(Long.valueOf(response.getId()));
        }
        model.setName(response.getName());
        model.setDescription(response.getDescription());
        return model;
    }
}
