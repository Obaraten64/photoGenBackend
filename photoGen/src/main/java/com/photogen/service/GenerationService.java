package com.photogen.service;

import ai.fal.client.queue.QueueStatus;
import com.photogen.dto.Images;

public interface GenerationService {
    String submitPrompt(String prompt);
    QueueStatus.Status getStatus(String taskId);
    Images getResult(String taskId);
}
