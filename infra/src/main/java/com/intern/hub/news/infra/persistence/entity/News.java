package com.intern.hub.news.infra.persistence.entity;

import com.intern.hub.library.common.annotation.SnowflakeGeneratedId;
import com.intern.hub.starter.security.entity.AuditEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "news")
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

  @Column(nullable = false, columnDefinition = "TEXT")
  String body;

  @Column(nullable = false, length = 150)
  String thumbnail;

  boolean isFeatured;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "status_id", nullable = false)
  NewsStatuses status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "topic_id")
  NewsTopics topic;

}
