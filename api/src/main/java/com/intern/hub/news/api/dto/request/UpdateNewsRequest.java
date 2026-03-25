package com.intern.hub.news.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
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

  @NotBlank
  @Size(max = 255)
  private String shortDescription;

  @NotEmpty
  private List<Long> topicIds;

  @NotNull
  private Long statusId;

  @NotNull
  private Boolean featured;

  private String thumbnail;
}
