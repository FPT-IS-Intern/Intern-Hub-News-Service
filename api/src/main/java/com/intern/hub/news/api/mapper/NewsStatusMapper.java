package com.intern.hub.news.api.mapper;

import com.intern.hub.news.api.dto.response.NewsStatusResponse;
import com.intern.hub.news.core.domain.model.NewsStatusModel;

public class NewsStatusMapper {
    private NewsStatusMapper() {}

    public static NewsStatusResponse toResponse(NewsStatusModel model) {
        return new NewsStatusResponse(
                model.getId() != null ? model.getId().toString() : null,
                model.getName(),
                model.getDescription()
        );
    }

    public static NewsStatusModel toModel(NewsStatusResponse response) {
        NewsStatusModel model = new NewsStatusModel();
        if (response.getId() != null) {
            model.setId(Long.valueOf(response.getId()));
        }
        model.setName(response.getName());
        model.setDescription(response.getDescription());
        return model;
    }
}

