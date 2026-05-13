package com.intern.hub.news.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsStatusResponse {
    private String id;
    private String name;
    private String description;
}