package com.intern.hub.news.infra.service.feign;

import com.intern.hub.news.core.domain.command.CreateTicketCommand;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ticket-service", url = "${TICKET_SERVICE_INTERNAL_URL:http://localhost:8082/ticket/internal}")
public interface TicketServiceFeignClient {
    @PostMapping
    void createTicket(@RequestBody CreateTicketCommand request);
}

