package com.intern.hub.news.infra.mapper;

import com.intern.hub.news.core.domain.model.NewsStatusModel;
import com.intern.hub.news.infra.persistence.entity.NewsStatuses;
import org.springframework.stereotype.Component;

@Component
public class NewsStatusMapper {

  public NewsStatusModel toModel(NewsStatuses entity) {
    if (entity == null) {
      return null;
    }

    var newsStatusModel = new NewsStatusModel();
    newsStatusModel.setId(entity.getId());
    newsStatusModel.setName(entity.getName());
    newsStatusModel.setDescription(entity.getDescription());
    return newsStatusModel;
  }

  public NewsStatuses toEntity(NewsStatusModel model) {
    if (model == null) {
      return null;
    }
    NewsStatuses entity = new NewsStatuses();
    entity.setId(model.getId());
    entity.setName(model.getName());
    entity.setDescription(model.getDescription());
    return entity;
  }
}
