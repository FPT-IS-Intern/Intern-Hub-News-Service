package com.intern.hub.news.core.domain.model;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsModel {

  private Long id;
  private String title;
  private String body;
  private String shortDescription;
  private String thumbnail;
  private List<NewsTopicModel> topics;
  private String status;
  private Long statusId;
  private boolean featured;
  private Long createdAt;
  private Long updatedAt;
  private Long createdBy;
  private Long updatedBy;
}
