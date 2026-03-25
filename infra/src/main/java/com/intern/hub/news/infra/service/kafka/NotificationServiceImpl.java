package com.intern.hub.news.infra.service.kafka;

import com.intern.hub.library.common.utils.Snowflake;
import com.intern.hub.news.core.domain.port.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

        private final KafkaTemplate<String, Object> kafkaTemplate;
        private final Snowflake snowflake;

        @Override
        public void sendNews(String title, String url) {
                List<Long> listUserIds = new ArrayList<>();
                Message<NewMessage> sendNewsMessage = new Message<>(
                                snowflake.next(),
                                new NewMessage(title),
                                List.of("IN_APP", "PUSH"),
                                "new-service",
                                Map.of(
                                                "IN_APP", new ArrayList<>(listUserIds),
                                                "PUSH", new ArrayList<>(listUserIds)),
                                Map.of(
                                                "locale", List.of("vi"),
                                                "targetUrl", List.of(url)),
                                "NEWS");
                kafkaTemplate.send("notification.event.request", sendNewsMessage);
        }

        private record Message<T>(
                        Long eventId,
                        T payload,
                        List<String> channels,
                        String sourceService,
                        Map<String, List<Object>> recipients,
                        Map<String, List<Object>> metadata,
                        String notificationType) {
        }

        private record NewMessage(
                        String title) {
        }

}
