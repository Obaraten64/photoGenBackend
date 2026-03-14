package com.photogen.service.impl;

import ai.fal.client.FalClient;
import ai.fal.client.queue.QueueSubmitOptions;

import com.photogen.service.EnhancerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EnchancerServiceImpl implements EnhancerService {

    @Value("${fal.model-id.enhancement}")
    private final String modelId;
    @Qualifier("cleanPrompt")
    private final String cleanPrompt;
    @Qualifier("withBodyPrompt")
    private final String withBodyPrompt;
    @Qualifier("withoutBodyPrompt")
    private final String withoutBodyPrompt;
    private final FalClient falClient;

    private String toDataUri(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = "image/jpeg";
        }

        byte[] fileBytes = file.getBytes();
        String base64 = Base64.getEncoder().encodeToString(fileBytes);

        return "data:" + contentType + ";base64," + base64;
    }

    @Override
    public String enhanceFromUpload(MultipartFile file, String userPrompt, Boolean withBody) throws Exception {
        String imageUrl = toDataUri(file);
        String fullPrompt = cleanPrompt + (userPrompt == null || userPrompt.isEmpty() ? "" : userPrompt)
                + (withBody ? withBodyPrompt : withoutBodyPrompt);

        var input = Map.of(
                "prompt", fullPrompt,
                "image_url", imageUrl
        );

        var response = falClient.queue().submit(
                modelId,
                QueueSubmitOptions.builder()
                        .input(input)
                        .build()
        );

        log.info("Submitted enhancement request for requestId {} for model {}", response.getRequestId(), modelId);
        return response.getRequestId();
    }
}
