package com.intern.hub.news.core.domain.command;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNewsCommand {
    private Long userId;

    private String title;

    private String body;

    private String shortDescription;

    private List<Long> topicIds;

    private Long statusId;

    private Boolean featured;

    private String thumbnail;
}
