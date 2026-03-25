package com.intern.hub.news.api.dto.response;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewsResponse {

  private Long id;
  private String title;
  private String body;
  private String shortDescription;
  private List<NewsTopicResponse> topics;
  private String thumbNail;
  private String status;
  private boolean featured;
  private Long createdAt;
  private Long updatedAt;
  private Long createdBy;
}
