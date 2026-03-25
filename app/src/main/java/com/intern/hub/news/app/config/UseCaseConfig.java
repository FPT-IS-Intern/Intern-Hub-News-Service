package com.intern.hub.news.app.config;

import com.intern.hub.news.core.domain.port.NewsRepository;
import com.intern.hub.news.core.domain.port.NewsTopicRepository;
import com.intern.hub.news.core.domain.port.NewsStatusRepository;
import com.intern.hub.news.core.domain.port.TicketService;
import com.intern.hub.news.core.domain.usecase.NewsTopicUseCase;
import com.intern.hub.news.core.domain.usecase.NewsStatusUseCase;
import com.intern.hub.news.core.domain.usecase.NewsUseCase;
import com.intern.hub.news.core.domain.usecase.impl.NewsUseCaseImpl;
import com.intern.hub.news.core.domain.usecase.impl.NewsTopicUseCaseImpl;
import com.intern.hub.news.core.domain.usecase.impl.NewsStatusUseCaseImpl;
import com.intern.hub.news.infra.service.feign.TicketServiceFeignClient;
import com.intern.hub.news.infra.persistence.repository.impl.TicketServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

  @Bean
  public NewsUseCase newsUsecase(NewsRepository newsRepository) {
    return new NewsUseCaseImpl(newsRepository);
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
  public TicketService ticketService(TicketServiceFeignClient ticketServiceFeignClient) {
    return new TicketServiceImpl(ticketServiceFeignClient);
  }

}
