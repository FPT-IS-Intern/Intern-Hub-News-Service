package com.intern.hub.news.api.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNewsRequest {

  @NotBlank
  @Size(max = 150)
  private String title;

  @NotBlank
  private String body;

  @NotNull
  @Positive
  private Long topicId;

  @NotNull
  private Boolean featured;
}
