package com.intern.hub.news.infra.persistence.repository.jpa;

import com.intern.hub.news.infra.persistence.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.util.Optional;

public interface NewsJpaRepository extends JpaRepository<News, Long> {

    @EntityGraph(attributePaths = {"status", "topic"})
    List<News> findAll();

    @EntityGraph(attributePaths = {"status", "topic"})
    Optional<News> findById(Long id);
}
