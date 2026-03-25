package com.intern.hub.news.core.domain.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsStatusModel {

  private Long id;
  private String name;
  private String description;
  private Long ticketTypeId;
  private Map<String, Object> payload;

}
