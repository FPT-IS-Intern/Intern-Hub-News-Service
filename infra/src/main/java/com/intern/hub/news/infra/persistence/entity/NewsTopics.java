package com.intern.hub.news.infra.persistence.entity;

import com.intern.hub.library.common.annotation.SnowflakeGeneratedId;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "news_topics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsTopics {
    @Id
    @SnowflakeGeneratedId
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 255)
    private String description;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<News> newsList;
}
