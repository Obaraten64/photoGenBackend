package com.photogen.service.impl;

import ai.fal.client.FalClient;
import ai.fal.client.queue.QueueSubmitOptions;

import com.google.gson.JsonObject;
import com.photogen.service.EnhancerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
@Slf4j
public class EnchancerServiceImpl implements EnhancerService {

    private static final String BASE_PROMPT =
            "Transform the uploaded clothing or footwear photo into a clean, realistic marketplace-ready fashion image.\n" +
                    "\n" +
                    "Preserve the exact product design, color, shape, material, and details.\n" +
                    "Do not change the clothing style, fabric texture, logo, or product structure.\n" +
                    "\n" +
                    "Remove any text, graphics, promotional elements, arrows, labels, or collage design from the original image.\n" +
                    "\n" +
                    "Create a natural lifestyle fashion photo with a realistic environment such as a modern apartment, living room, bedroom, or wardrobe interior. The background should include subtle furniture or interior elements but must not distract from the product.\n" +
                    "\n" +
                    "Ensure the product remains the clear focus of the image.\n" +
                    "\n" +
                    "Lighting should be soft, natural, and suitable for fashion product photography. The image should look like it was taken in a professional fashion photoshoot.\n" +
                    "\n" +
                    "If a model is present, keep the clothing unchanged and natural on the model. If needed, adjust pose or framing slightly while preserving the product.\n" +
                    "\n" +
                    "Avoid overly stylized AI aesthetics. The result must look like a realistic e-commerce product photo used on fashion marketplaces.\n" +
                    "\n" +
                    "Output a clean, high-quality product image suitable for online store listings.\n";

    private final String modelId;
    private final String falApiKey;
    private final FalClient falClient;

    public EnchancerServiceImpl(
            @Value("${fal.model-id.enhancement}") String modelId,
            @Value("${fal.api-key}") String falApiKey,
            FalClient falClient
    ) {
        this.modelId = modelId;
        this.falApiKey = falApiKey;
        this.falClient = falClient;
    }

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
    public String enhanceFromUpload(MultipartFile file, String userPrompt) throws Exception {
        String imageUrl = toDataUri(file);
        String fullPrompt = BASE_PROMPT + (userPrompt == null ? "" : userPrompt);

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
