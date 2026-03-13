package com.intern.hub.news.infra.persistence.repository.jpa;

import com.intern.hub.news.infra.persistence.entity.NewsTopics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsTopicJpaRepository extends JpaRepository<NewsTopics, Long> {
}
