package com.intern.hub.news.infra.service.impl;

import com.intern.hub.news.core.domain.port.UserProfilePort;
import com.intern.hub.news.infra.service.feign.HrmUserFeignClient;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserProfilePortImpl implements UserProfilePort {

    private final HrmUserFeignClient hrmUserFeignClient;

    @Override
    public String getFullNameByUserId(Long userId) {
        if (userId == null) {
            return null;
        }

        try {
            Map<String, Object> response = hrmUserFeignClient.getUserById(userId);
            if (response == null) {
                return null;
            }

            Object data = response.get("data");
            if (!(data instanceof Map<?, ?> dataMap)) {
                return null;
            }

            Object fullName = dataMap.get("fullName");
            if (!(fullName instanceof String fullNameText) || fullNameText.isBlank()) {
                return null;
            }

            return fullNameText;
        } catch (Exception ex) {
            log.warn("Cannot get fullName from HRM for userId={}", userId, ex);
            return null;
        }
    }
}
