package com.intern.hub.news.infra.persistence.repository.jpa;

import com.intern.hub.news.infra.persistence.entity.NewsStatuses;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NewsStatusJpaRepository extends JpaRepository<NewsStatuses, Long> {
    Optional<NewsStatuses> findByName(String name);
}