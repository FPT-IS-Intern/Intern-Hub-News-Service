package com.intern.hub.news.infra.persistence.repository.jpa;

import com.intern.hub.news.infra.persistence.entity.News;
import com.intern.hub.news.infra.persistence.projection.NewsSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NewsJpaRepository extends JpaRepository<News, Long> {

    @Override
    List<News> findAll();

    @EntityGraph(attributePaths = { "status", "topics" })
    Page<NewsSummaryProjection> findAllProjectedBy(Pageable pageable);

    @EntityGraph(attributePaths = { "status", "topics" })
    Page<NewsSummaryProjection> findProjectedByStatus_Name(String statusName, Pageable pageable);

    @EntityGraph(attributePaths = { "status", "topics" })
    Page<NewsSummaryProjection> findProjectedByIsFeatured(boolean featured, Pageable pageable);

    @EntityGraph(attributePaths = { "status", "topics" })
    Page<NewsSummaryProjection> findProjectedByIsFeaturedAndStatus_Name(boolean featured, String statusName,
            Pageable pageable);

    @EntityGraph(attributePaths = { "status", "topics" })
    Page<NewsSummaryProjection> findProjectedByIsFeaturedAndStatus_Id(boolean featured, Long statusId,
            Pageable pageable);

    long countByStatus_Name(String statusName);

    long countByIsFeatured(boolean featured);

    long countByIsFeaturedAndStatus_Name(boolean featured, String statusName);

    long countByIsFeaturedAndStatus_Id(boolean featured, Long statusId);

    @Override
    @EntityGraph(attributePaths = { "status", "topics" })
    Optional<News> findById(Long id);

    @EntityGraph(attributePaths = { "status", "topics" })
    Page<NewsSummaryProjection> findAllByCreatedAtBetween(long start, long end, Pageable pageable);

    long countByCreatedAtBetween(long start, long end);

    @EntityGraph(attributePaths = { "status", "topics" })
    Page<NewsSummaryProjection> findProjectedByTopics_IdAndStatus_Name(Long topicId, String statusName,
            Pageable pageable);

    @EntityGraph(attributePaths = { "status", "topics" })
    Page<NewsSummaryProjection> findProjectedByStatus_NameAndTitleContainingIgnoreCase(String statusName, String title,
            Pageable pageable);

    long countByStatus_NameAndTitleContainingIgnoreCase(String statusName, String title);
}
