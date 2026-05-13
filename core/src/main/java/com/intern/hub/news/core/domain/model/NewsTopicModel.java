package com.intern.hub.news.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsTopicModel {

  private Long id;
  private String name;
  private String description;

}
