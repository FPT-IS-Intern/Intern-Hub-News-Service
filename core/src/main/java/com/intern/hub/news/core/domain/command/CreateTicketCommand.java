package com.intern.hub.news.core.domain.command;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketCommand {
    private Long userId;
    private Long ticketTypeId;
    private Map<String, Object> payload;
}
