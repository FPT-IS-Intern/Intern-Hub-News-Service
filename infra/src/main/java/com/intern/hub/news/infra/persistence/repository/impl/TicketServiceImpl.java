package com.intern.hub.news.infra.persistence.repository.impl;

import com.intern.hub.library.common.exception.BadRequestException;
import com.intern.hub.library.common.exception.ExceptionConstant;
import com.intern.hub.news.core.domain.command.CreateTicketCommand;
import com.intern.hub.news.core.domain.port.TicketService;
import com.intern.hub.news.infra.service.feign.TicketServiceFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketServiceFeignClient ticketServiceFeignClient;
    private final ObjectMapper objectMapper;
    private final String ticketInternalBaseUrl;
    private final String internalSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String CREATE_TICKET_URL = "/ticket/internal";

    @Override
    public Long createTicket(CreateTicketCommand command) {
        try {
            if (command.getUserId() == null) {
                throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE, "creatorId is required");
            }

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("creatorId", String.valueOf(command.getUserId()));

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            body.add("request", new HttpEntity<>(TicketServiceFeignClient.TicketCreateRequest.from(command), requestHeaders));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("X-Internal-Secret", internalSecret);

            ResponseEntity<String> response = restTemplate.exchange(
                    buildCreateTicketUrl(),
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers),
                    String.class);

            String responseBody = response.getBody();
            if (responseBody == null || responseBody.isBlank()) {
                return null;
            }

            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode ticketIdNode = root.path("data").path("ticketId");
            if (ticketIdNode.isMissingNode() || ticketIdNode.isNull()) {
                return null;
            }
            return ticketIdNode.asLong();
        } catch (Exception e) {
            throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE,
                    "Failed to create ticket via internal API: " + e.getMessage());
        }
    }

    @Override
    public boolean isTicketApproved(Long ticketId) {
        var response = ticketServiceFeignClient.getTicketDetail(ticketId, internalSecret);
        return response != null
                && response.data() != null
                && response.data().ticketDetail() != null
                && "APPROVED".equalsIgnoreCase(response.data().ticketDetail().status());
    }

    private String buildCreateTicketUrl() {
        String baseUrl = ticketInternalBaseUrl == null ? "" : ticketInternalBaseUrl.trim();
        while (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        if (baseUrl.endsWith(CREATE_TICKET_URL)) {
            return baseUrl;
        }

        return baseUrl + CREATE_TICKET_URL;
    }
}
