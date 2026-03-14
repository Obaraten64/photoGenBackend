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
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnchancerServiceImpl implements EnhancerService {
    private final String prompt = "Transform the uploaded clothing or footwear photo into a clean, realistic marketplace-ready fashion image.\n" +
            "\n" +
            "Preserve the exact product design, color, shape, material, and details. \n" +
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
            "Output a clean, high-quality product image suitable for online store listings. \n";
    @Value("${fal.model-id.enhancement}")
    private final String modelId;
    @Value("${fal.api-key}")
    private final String falApiKey;
    private final FalClient falClient;

    private String uploadToFal(MultipartFile file) throws Exception {
        String boundary = "----FalBoundary" + System.currentTimeMillis();
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload.jpg";

        byte[] fileBytes = file.getBytes();

        String partHeader =
                "--" + boundary + "\r\n" +
                        "Content-Disposition: form-data; name=\"file_upload\"; filename=\"" + filename + "\"\r\n" +
                        "Content-Type: " + (file.getContentType() != null ? file.getContentType() : "application/octet-stream") + "\r\n\r\n";

        String partFooter = "\r\n--" + boundary + "--\r\n";

        byte[] body = concat(
                partHeader.getBytes(StandardCharsets.UTF_8),
                fileBytes,
                partFooter.getBytes(StandardCharsets.UTF_8)
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.fal.ai/v1/serverless/files/file/local/uploads/" + filename))
                .header("Authorization", "Key " + falApiKey)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Fal upload failed: " + response.statusCode() + " " + response.body());
        }

        // Відповідь містить file_url
        JsonObject json = com.google.gson.JsonParser.parseString(response.body()).getAsJsonObject();
        return json.get("file_url").getAsString();
    }

    private byte[] concat(byte[]... arrays) {
        int total = 0;
        for (byte[] arr : arrays) {
            total += arr.length;
        }

        byte[] result = new byte[total];
        int pos = 0;

        for (byte[] arr : arrays) {
            System.arraycopy(arr, 0, result, pos, arr.length);
            pos += arr.length;
        }

        return result;
    }

    @Override
    public String enhanceFromUpload(MultipartFile file, String userPrompt) throws Exception {
        String imageUrl = uploadToFal(file);
        String fullPrompt = prompt + userPrompt;

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
