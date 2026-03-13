package com.intern.hub.news.infra.mapper;

import com.intern.hub.news.core.domain.model.NewsTopicModel;
import com.intern.hub.news.infra.persistence.entity.NewsTopics;
import org.springframework.stereotype.Component;

@Component
public class NewsTopicMapper {

  public NewsTopicModel toModel(NewsTopics entity) {
    if (entity == null) {
      return null;
    }
    return new NewsTopicModel(
        entity.getId(),
        entity.getName(),
        entity.getDescription());
  }

  public NewsTopics toEntity(NewsTopicModel model) {
    if (model == null) {
      return null;
    }
    NewsTopics entity = new NewsTopics();
    entity.setId(model.getId());
    entity.setName(model.getName());
    entity.setDescription(model.getDescription());
    return entity;
  }
}
