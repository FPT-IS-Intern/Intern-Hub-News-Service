package com.intern.hub.news.infra.persistence.entity;

import com.intern.hub.library.common.annotation.SnowflakeGeneratedId;
import com.intern.hub.starter.security.entity.AuditEntity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "news", indexes = {
    @Index(name = "idx_news_status", columnList = "status_id"),
    @Index(name = "idx_news_featured", columnList = "is_featured"),
    @Index(name = "idx_news_created_at", columnList = "created_at"),
    @Index(name = "idx_news_approval_ticket", columnList = "approval_ticket_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class News extends AuditEntity {

  @Id
  @SnowflakeGeneratedId
  Long id;

  @Column(nullable = false, length = 150)
  String title;

  @Basic(fetch = FetchType.LAZY)
  @Column(nullable = false, columnDefinition = "TEXT")
  String body;

  @Column(nullable = false, columnDefinition = "TEXT")
  String thumbnail;

  @Column(nullable = true, length = 255)
  String shortDescription;

  @Column(name = "approval_ticket_id")
  Long approvalTicketId;

  boolean isFeatured;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "status_id", nullable = false)
  NewsStatuses status;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "news_news_topics",
      joinColumns = @JoinColumn(name = "news_id"),
      inverseJoinColumns = @JoinColumn(name = "topic_id")
  )
  Set<NewsTopics> topics;

}
