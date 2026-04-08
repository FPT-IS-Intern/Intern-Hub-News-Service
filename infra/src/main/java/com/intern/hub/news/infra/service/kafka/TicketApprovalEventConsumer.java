package com.intern.hub.news.infra.service.kafka;

import com.intern.hub.news.core.domain.usecase.NewsUseCase;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketApprovalEventConsumer {

    private static final String STATUS_APPROVED = "APPROVED";
    private static final String STATUS_REJECTED = "REJECTED";

    private final ObjectMapper objectMapper;
    private final NewsUseCase newsUseCase;

    @PostConstruct
    void init() {
        log.info("[News][Kafka] TicketApprovalEventConsumer initialized");
    }

    @KafkaListener(
            topics = "${news.kafka.ticket-events-topic:ih.ticket.events}",
            groupId = "${news.kafka.ticket-events-group:${spring.application.name}-ticket-events}",
            autoStartup = "${news.kafka.ticket-events-enabled:true}")
    public void consumeTicketEvent(String message) {
        try {
            log.info("[News][Kafka] Received ticket event message: {}", message);
            Map<String, Object> event = parseEvent(message);
            Long ticketId = extractLong(event.get("ticketId"));
            String status = extractStatus(event.get("status"));
            Long approverId = extractLong(event.get("approverId"));
            if (ticketId == null) {
                log.warn("[News][Kafka] Skip event because ticketId is missing. Parsed event: {}", event);
                return;
            }
            if (STATUS_REJECTED.equals(status)) {
                log.info("[News][Kafka] Processing ticket rejected event for ticketId={}", ticketId);
                newsUseCase.rejectByTicketId(ticketId);
                return;
            }
            if (STATUS_APPROVED.equals(status) || approverId != null) {
                log.info("[News][Kafka] Processing ticket approval event for ticketId={}", ticketId);
                newsUseCase.approveByTicketId(ticketId);
                return;
            }
            log.debug("[News][Kafka] Skip event for ticketId={} because status/approver not eligible. event={}", ticketId, event);
        } catch (Exception ex) {
            log.error("[News] Failed to process ticket event: {}", message, ex);
        }
    }

    private Long extractLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String extractStatus(Object value) {
        if (value == null) {
            return null;
        }
        return String.valueOf(value).trim().toUpperCase();
    }

    private Map<String, Object> parseEvent(String message) throws Exception {
        JsonNode node = objectMapper.readTree(message);
        // Some producers send a JSON string that itself contains a JSON object.
        if (node != null && node.isTextual()) {
            node = objectMapper.readTree(node.asText());
        }
        if (node == null || !node.isObject()) {
            throw new IllegalArgumentException("Kafka event payload is not a JSON object");
        }
        return objectMapper.convertValue(node, new TypeReference<Map<String, Object>>() {
        });
    }
}

