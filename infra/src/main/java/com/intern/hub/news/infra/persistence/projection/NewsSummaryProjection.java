package com.intern.hub.news.infra.persistence.projection;

import com.intern.hub.news.infra.persistence.entity.NewsStatuses;
import com.intern.hub.news.infra.persistence.entity.NewsTopics;
import org.springframework.beans.factory.annotation.Value;

import java.util.Set;

public interface NewsSummaryProjection {
    Long getId();

    String getTitle();

    String getShortDescription();

    String getThumbnail();

    @Value("#{target.isFeatured}")
    boolean isFeatured();

    NewsStatuses getStatus();

    Set<NewsTopics> getTopics();

    Long getCreatedAt();

    Long getUpdatedAt();

    Long getCreatedBy();
}
