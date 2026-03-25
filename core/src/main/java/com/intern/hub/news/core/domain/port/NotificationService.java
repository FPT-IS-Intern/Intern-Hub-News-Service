package com.intern.hub.news.core.domain.port;

public interface NotificationService {
    void sendNews(String title, String url);
}
