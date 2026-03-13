package com.intern.hub.news.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/news/health")
public class HealthCheckController {

  @GetMapping
  public String healthCheck() {
    return "OK";
  }
}

