package com.intern.hub.news.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsTopicModel {

  private Long id;
  private String name;
  private String description;

}
