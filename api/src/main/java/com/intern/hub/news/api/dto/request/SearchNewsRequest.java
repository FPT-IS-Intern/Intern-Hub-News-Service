package com.intern.hub.news.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchNewsRequest {
    private String title;
    private int page;
    private int size;
    private String sortColumn;
    private String sortDirection;
}
