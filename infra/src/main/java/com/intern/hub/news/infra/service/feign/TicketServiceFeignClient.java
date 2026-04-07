package com.intern.hub.news.infra.service.feign;

import com.intern.hub.news.core.domain.command.CreateTicketCommand;
import com.intern.hub.library.common.dto.ResponseApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

import java.util.Map;

@FeignClient(name = "ticket-service", url = "${ticket.service.internal-url:${TICKET_SERVICE_INTERNAL_URL:http://localhost:8081}}")
public interface TicketServiceFeignClient {
    @PostMapping(value = "/ticket/internal?creatorId={creatorId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseApi<TicketCreateResponse> createTicket(
            @RequestParam("creatorId") Long creatorId,
            @RequestPart("request") TicketCreateRequest request);

    @GetMapping("/ticket/internal/{ticketId}")
    ResponseApi<TicketDetailResponse> getTicketDetail(
            @PathVariable("ticketId") Long ticketId,
            @RequestHeader("X-Internal-Secret") String internalSecret);

    record TicketCreateRequest(Long ticketTypeId, Map<String, Object> payload) {
        public static TicketCreateRequest from(CreateTicketCommand command) {
            return new TicketCreateRequest(command.getTicketTypeId(), command.getPayload());
        }
    }

    record TicketCreateResponse(Long ticketId) {
    }

    record TicketDetailResponse(TicketDetail ticketDetail) {
    }

    record TicketDetail(String status) {
    }
}

