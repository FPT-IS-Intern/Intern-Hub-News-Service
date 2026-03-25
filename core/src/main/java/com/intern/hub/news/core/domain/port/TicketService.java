package com.intern.hub.news.core.domain.port;

import com.intern.hub.news.core.domain.command.CreateTicketCommand;

public interface TicketService {
    
    void createTicket(CreateTicketCommand command);
}
