package com.intern.hub.news.infra.mapper;

import com.intern.hub.news.core.domain.model.NewsModel;
import com.intern.hub.news.infra.persistence.entity.News;
import com.intern.hub.news.infra.persistence.entity.NewsStatuses;
import com.intern.hub.news.infra.persistence.entity.NewsTopics;
import org.springframework.stereotype.Component;

@Component
public class NewsEntityMapper {

  public NewsModel toModel(News entity) {
    NewsModel newsModel = new NewsModel();
    newsModel.setId(entity.getId());
    newsModel.setTitle(entity.getTitle());
    newsModel.setThumbnail(entity.getThumbnail() != null ? entity.getThumbnail() : "");
    newsModel.setBody(entity.getBody());
    newsModel.setCreatedAt(entity.getCreatedAt());
    newsModel.setUpdatedAt(entity.getUpdatedAt());
    newsModel.setFeatured(entity.isFeatured());
    newsModel.setTopicId(entity.getTopic() != null ? entity.getTopic().getId() : null);
    newsModel.setTopicName(entity.getTopic() != null ? entity.getTopic().getName() : null);
    newsModel.setStatus(entity.getStatus() != null ? entity.getStatus().getName() : null);

    return newsModel;
  }

  public News toEntity(NewsModel model) {
    NewsStatuses status = null;
    if (model.getStatusId() != null) {
      status = new NewsStatuses();
      status.setId(model.getStatusId());
    }

    NewsTopics topic = null;
    if (model.getTopicId() != null) {
      topic = new NewsTopics();
      topic.setId(model.getTopicId());
      topic.setName(model.getTopicName());
    }

    News entity = new News();
    entity.setId(model.getId());
    entity.setTitle(model.getTitle());
    entity.setThumbnail(model.getThumbnail());
    entity.setBody(model.getBody());
    entity.setCreatedAt(model.getCreatedAt());
    entity.setUpdatedAt(model.getUpdatedAt());
    entity.setStatus(status);
    entity.setTopic(topic);
    entity.setFeatured(model.isFeatured());
    return entity;
  }
}
