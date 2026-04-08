package com.intern.hub.news.infra.mapper;

import com.intern.hub.news.core.domain.model.NewsModel;
import com.intern.hub.news.infra.persistence.entity.News;
import com.intern.hub.news.infra.persistence.entity.NewsStatuses;
import com.intern.hub.news.infra.persistence.entity.NewsTopics;
import com.intern.hub.news.infra.persistence.projection.NewsSummaryProjection;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NewsEntityMapper {

  private final NewsTopicMapper topicMapper;

  public NewsModel toSummaryModel(NewsSummaryProjection projection) {
    NewsModel newsModel = new NewsModel();
    newsModel.setId(projection.getId());
    newsModel.setTitle(projection.getTitle());
    newsModel.setThumbnail(projection.getThumbnail() != null ? projection.getThumbnail() : "");
    newsModel.setShortDescription(projection.getShortDescription());
    newsModel.setCreatedAt(projection.getCreatedAt());
    newsModel.setUpdatedAt(projection.getUpdatedAt());
    newsModel.setFeatured(projection.isFeatured());
    newsModel.setTopics(projection.getTopics() != null
        ? projection.getTopics().stream().map(topicMapper::toModel).toList()
        : new java.util.ArrayList<>());
    newsModel.setStatus(projection.getStatus() != null ? projection.getStatus().getName() : null);
    newsModel.setCreatedBy(projection.getCreatedBy());

    return newsModel;
  }

  public NewsModel toModel(News entity) {
    NewsModel newsModel = new NewsModel();
    newsModel.setId(entity.getId());
    newsModel.setTitle(entity.getTitle());
    newsModel.setThumbnail(entity.getThumbnail() != null ? entity.getThumbnail() : "");
    newsModel.setBody(entity.getBody());
    newsModel.setShortDescription(entity.getShortDescription());
    newsModel.setCreatedAt(entity.getCreatedAt());
    newsModel.setUpdatedAt(entity.getUpdatedAt());
    newsModel.setFeatured(entity.isFeatured());
    newsModel.setTopics(entity.getTopics() != null
        ? entity.getTopics().stream().map(topicMapper::toModel).toList()
        : new java.util.ArrayList<>());
    newsModel.setStatus(entity.getStatus() != null ? entity.getStatus().getName() : null);
    newsModel.setCreatedBy(entity.getCreatedBy());
    newsModel.setUpdatedBy(entity.getUpdatedBy());
    newsModel.setApprovalTicketId(entity.getApprovalTicketId());

    return newsModel;
  }

  public News toEntity(NewsModel model) {
    NewsStatuses status = null;
    if (model.getStatusId() != null) {
      status = new NewsStatuses();
      status.setId(model.getStatusId());
    }

    java.util.Set<NewsTopics> topics = null;
    if (model.getTopics() != null) {
      topics = model.getTopics().stream().map(topicMapper::toEntity).collect(Collectors.toSet());
    }

    News entity = new News();
    entity.setId(model.getId());
    entity.setTitle(model.getTitle());
    entity.setThumbnail(model.getThumbnail());
    entity.setBody(model.getBody());
    entity.setShortDescription(model.getShortDescription());
    entity.setCreatedAt(model.getCreatedAt());
    entity.setUpdatedAt(model.getUpdatedAt());
    entity.setStatus(status);
    entity.setTopics(topics);
    entity.setFeatured(model.isFeatured());
    entity.setCreatedBy(model.getCreatedBy());
    entity.setUpdatedBy(model.getUpdatedBy());
    entity.setApprovalTicketId(model.getApprovalTicketId());
    return entity;
  }
}
