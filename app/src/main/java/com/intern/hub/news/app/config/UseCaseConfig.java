package com.intern.hub.news.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.intern.hub.news.core.domain.port.NewsRepository;
import com.intern.hub.news.core.domain.port.NewsStatusRepository;
import com.intern.hub.news.core.domain.port.NewsTopicRepository;
import com.intern.hub.news.core.domain.port.TicketService;
import com.intern.hub.news.core.domain.usecase.NewsStatusUseCase;
import com.intern.hub.news.core.domain.usecase.NewsTopicUseCase;
import com.intern.hub.news.core.domain.usecase.NewsUseCase;
import com.intern.hub.news.core.domain.usecase.impl.NewsStatusUseCaseImpl;
import com.intern.hub.news.core.domain.usecase.impl.NewsTopicUseCaseImpl;
import com.intern.hub.news.core.domain.usecase.impl.NewsUseCaseImpl;
import com.intern.hub.news.infra.persistence.repository.impl.TicketServiceImpl;
import com.intern.hub.news.infra.service.feign.TicketServiceFeignClient;

@Configuration
public class UseCaseConfig {

  @Bean
  public NewsUseCase newsUsecase(
      NewsRepository newsRepository,
      NewsStatusRepository newsStatusRepository,
      TicketService ticketService,
      @Value("${news.ticket-type-id:5}") Long newsTicketTypeId) {
    return new NewsUseCaseImpl(newsRepository, newsStatusRepository, ticketService, newsTicketTypeId);
  }

  @Bean
  public NewsTopicUseCase newsTopicUsecase(NewsTopicRepository newsTopicRepository) {
    return new NewsTopicUseCaseImpl(newsTopicRepository);
  }

  @Bean
  public NewsStatusUseCase newsStatusUsecase(NewsStatusRepository newsStatusRepository) {
    return new NewsStatusUseCaseImpl(newsStatusRepository);
  }

  @Bean
  public TicketService ticketService(
      TicketServiceFeignClient ticketServiceFeignClient,
      @Value("${ticket.service.internal-url:${TICKET_SERVICE_INTERNAL_URL:http://localhost:8081}}") String ticketInternalBaseUrl,
      @Value("${security.internal-secret}") String internalSecret,
      tools.jackson.databind.ObjectMapper objectMapper) {
    return new TicketServiceImpl(ticketServiceFeignClient, objectMapper, ticketInternalBaseUrl, internalSecret);
  }

}
