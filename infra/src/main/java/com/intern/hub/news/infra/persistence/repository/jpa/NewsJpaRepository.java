package com.intern.hub.news.infra.persistence.repository.jpa;

import com.intern.hub.news.infra.persistence.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsJpaRepository extends JpaRepository<News, Long> {
}

