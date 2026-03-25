package com.intern.hub.news.infra.persistence.repository.impl;

import com.intern.hub.news.core.domain.command.CreateTicketCommand;
import com.intern.hub.news.core.domain.port.TicketService;
import com.intern.hub.news.infra.service.feign.TicketServiceFeignClient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketServiceFeignClient ticketServiceFeignClient;

    @Override
    public void createTicket(CreateTicketCommand command) {
        ticketServiceFeignClient.createTicket(command);
    }
}
