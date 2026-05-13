package com.intern.hub.news.core.domain.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsStatusModel {

  private Long id;
  private String name;
  private String description;
  private Long ticketTypeId;
  private Map<String, Object> payload;

}
