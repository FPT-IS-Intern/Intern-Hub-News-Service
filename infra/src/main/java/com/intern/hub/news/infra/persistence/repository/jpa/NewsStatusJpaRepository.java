package com.intern.hub.news.infra.persistence.repository.jpa;

import com.intern.hub.news.infra.persistence.entity.NewsStatuses;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsStatusJpaRepository extends JpaRepository<NewsStatuses, Long> {
}