package com.photogen.service;

import ai.fal.client.queue.QueueStatus;
import com.photogen.dto.Images;
import com.photogen.dto.requests.GenerateRequest;

public interface GenerationService {
    String submitPrompt(GenerateRequest request);
    QueueStatus.Status getStatus(String taskId);
    Images getResult(String taskId);
}
