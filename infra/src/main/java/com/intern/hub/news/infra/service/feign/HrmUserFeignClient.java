package com.intern.hub.news.infra.service.feign;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "hrm-service",
    url = "${service.hrm.url:${services.gateway.url:http://localhost:8765}/api}"
)
public interface HrmUserFeignClient {

    @GetMapping("/hrm/internal/users/{userId}")
    Map<String, Object> getUserById(@PathVariable("userId") Long userId);
}
