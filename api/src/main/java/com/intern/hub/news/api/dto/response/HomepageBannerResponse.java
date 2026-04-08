package com.intern.hub.news.api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomepageBannerResponse {
  private Long id;
  private String title;
  private String description;
  private Integer displayOrder;
  private Boolean isActive;
  private String desktopImageUrl;
  private String mobileImageUrl;
  private String imageAltText;
  private String actionType;
  private String actionTarget;
  private Boolean openInNewTab;
}
