package com.intern.hub.news.infra.persistence.entity;

import com.intern.hub.library.common.annotation.SnowflakeGeneratedId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "news_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsStatuses {

    @Id
    @SnowflakeGeneratedId
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 255)
    private String description;

    @OneToMany(mappedBy = "status", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<News> newsList;

}
