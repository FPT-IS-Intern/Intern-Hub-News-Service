package com.intern.hub.news.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsResponse {

  private Long id;
  private String title;
  private String body;
  private Long topicId;
  private String topicName;
  private String status;
  private boolean featured;
  private Long createdAt;
  private Long updatedAt;
}
