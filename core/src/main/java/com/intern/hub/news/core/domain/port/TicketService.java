package com.intern.hub.news.core.domain.port;

import com.intern.hub.news.core.domain.command.CreateTicketCommand;

public interface TicketService {

    Long createTicket(CreateTicketCommand command);

    boolean isTicketApproved(Long ticketId);
}
