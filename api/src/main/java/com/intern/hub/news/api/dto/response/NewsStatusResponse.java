package com.intern.hub.news.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsStatusResponse {
    private String id;
    private String name;
    private String description;
}