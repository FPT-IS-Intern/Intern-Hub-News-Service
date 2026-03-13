package com.intern.hub.news.app.config;

import com.intern.hub.news.core.domain.port.NewsRepository;
import com.intern.hub.news.core.domain.port.NewsTopicRepository;
import com.intern.hub.news.core.domain.port.NewsStatusRepository;
import com.intern.hub.news.core.domain.usecase.NewsUsecase;
import com.intern.hub.news.core.domain.usecase.NewsTopicUsecase;
import com.intern.hub.news.core.domain.usecase.NewsStatusUsecase;
import com.intern.hub.news.core.domain.usecase.impl.NewsUsecaseImpl;
import com.intern.hub.news.core.domain.usecase.impl.NewsTopicUsecaseImpl;
import com.intern.hub.news.core.domain.usecase.impl.NewsStatusUsecaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

  @Bean
  public NewsUsecase newsUsecase(NewsRepository newsRepository) {
    return new NewsUsecaseImpl(newsRepository);
  }

  @Bean
  public NewsTopicUsecase newsTopicUsecase(NewsTopicRepository newsTopicRepository) {
    return new NewsTopicUsecaseImpl(newsTopicRepository);
  }

  @Bean
  public NewsStatusUsecase newsStatusUsecase(NewsStatusRepository newsStatusRepository) {
    return new NewsStatusUsecaseImpl(newsStatusRepository);
  }
}
