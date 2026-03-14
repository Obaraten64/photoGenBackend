package com.photogen.service.impl;

import ai.fal.client.FalClient;
import ai.fal.client.queue.QueueResultOptions;
import ai.fal.client.queue.QueueStatus;
import ai.fal.client.queue.QueueStatusOptions;
import ai.fal.client.queue.QueueSubmitOptions;

import com.google.gson.JsonObject;
import com.photogen.dto.Images;
import com.photogen.model.FalApiRequest;
import com.photogen.service.GenerationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenerationServiceImpl implements GenerationService {
    @Value("${fal.model-id}")
    private final String modelId;
    private final FalClient falClient;
    private final ObjectMapper objectMapper;

    @Override
    public String submitPrompt(String prompt) {
        String requestId = falClient.queue()
                .submit(modelId,
                QueueSubmitOptions.builder()
                        .input(new FalApiRequest(prompt))
                        .build()
        ).getRequestId();
        log.info("Submitted prompt '{}' with requestId {} for model {}", prompt, requestId, modelId);
        return requestId;
    }

    @Override
    public QueueStatus.Status getStatus(String taskId) {
        log.info("Getting status for taskId {} and model {}", taskId, modelId);
        return falClient.queue()
                .status(modelId, QueueStatusOptions.withRequestId(taskId))
                .getStatus();
    }

    @Override
    public Images getResult(String taskId) {
        log.info("Getting result for taskId {} and model {}", taskId, modelId);

        JsonObject jsonObject = falClient.queue()
                .result(modelId, QueueResultOptions.withRequestId(taskId))
                .getData();
        log.info("Result for taskId {}: {}", taskId, jsonObject);
        return objectMapper.readValue(jsonObject.toString(), Images.class);
    }
}
