package com.intern.hub.news.infra.persistence.repository.jpa;

import com.intern.hub.news.infra.persistence.entity.News;
import com.intern.hub.news.infra.persistence.projection.NewsSummaryProjection;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.util.Optional;

public interface NewsJpaRepository extends JpaRepository<News, Long> {

    @EntityGraph(attributePaths = {"status", "topics"})
    @NullMarked
    List<News> findAll();

    @EntityGraph(attributePaths = {"status", "topics"})
    @NullMarked
    Page<NewsSummaryProjection> findAllProjectedBy(Pageable pageable);

    @EntityGraph(attributePaths = {"status", "topics"})
    @NullMarked
    Page<NewsSummaryProjection> findProjectedByStatus_Name(String statusName, Pageable pageable);

    @EntityGraph(attributePaths = {"status", "topics"})
    @NullMarked
    Page<NewsSummaryProjection> findProjectedByIsFeatured(boolean featured, Pageable pageable);

    long countByStatus_Name(String statusName);

    long countByIsFeatured(boolean featured);

    @EntityGraph(attributePaths = {"status", "topics"})
    @NullMarked
    Optional<News> findById(Long id);
    @EntityGraph(attributePaths = {"status", "topics"})
    @NullMarked
    Page<NewsSummaryProjection> findAllByCreatedAtBetween(long start, long end, Pageable pageable);

    long countByCreatedAtBetween(long start, long end);

    @EntityGraph(attributePaths = {"status", "topics"})
    @NullMarked
    Page<NewsSummaryProjection> findProjectedByTopics_IdAndStatus_Name(Long topicId, String statusName, Pageable pageable);
}
